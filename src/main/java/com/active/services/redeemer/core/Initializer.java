package com.active.services.redeemer.core;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.bson.BsonDocument;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.active.services.redeemer.exception.RedeemerStartupException;
import com.active.services.redeemer.synchronizer.DataSynchronizer;
import com.active.services.redeemer.synchronizer.MongoDataCleaner;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Initializer {
	
	private static final Logger LOG = LoggerFactory.getLogger(Initializer.class);
	
	public static final ObjectMapper MAPPER = new ObjectMapper();
	
	static final String MONGO_PROTOCOL = "mongodb://";
	
	public static ApplicationContext CXT;
	
	static AdditionalSyncConfiguration additionalConfig;
	
	static String mongoAddresses;
	
	public static List<DataSynchronizer<?>> synchronizers = Lists.newArrayList();
	
	Initializer(ApplicationContext cxt) throws RedeemerStartupException {
		CXT = cxt;
		additionalConfig = cxt.getBean(AdditionalSyncConfiguration.class);
		init();
	}

	private void init() throws RedeemerStartupException {
		// prepare mongoDB addresses
		String mongoAddresses = additionalConfig.getMongoDBAdresses();
		if (null != mongoAddresses && !"".equals(mongoAddresses.trim())) {
			List<String> addressList = Splitter.on(",").trimResults().splitToList(mongoAddresses);
			StringBuilder builder = new StringBuilder(MONGO_PROTOCOL);
			int i = 0;
			for (String addr: addressList) {
				if (i++ == 0) {
					builder.append(addr);
				} else {
					builder.append(",").append(addr);
				}
			}
			mongoAddresses = builder.toString();
		} else {
			throw new RedeemerStartupException("No mongo DB connection configuration was found!");
		}
		// collect synchronizer
		collectSynchronizer();
		// create synchronization thread pool
		int additionalIntervalSec = additionalConfig.getSyncIntervalSec();
		startScheduledSynchronizer(additionalIntervalSec);
		// prepare mongoDB cleaner
		int cleanIntervalSec = additionalConfig.getCleanIntervalSec();
		startMongoDataCleaner(cleanIntervalSec);
	}
	
	private void startMongoDataCleaner(int cleanIntervalSec) {
		ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				final Thread thread = Executors.defaultThreadFactory().newThread(r);
				thread.setName("redeemer-cleaner");
				thread.setDaemon(true);
				return thread;
			}
		});
		ses.scheduleWithFixedDelay(new Cleaner(), 0, cleanIntervalSec, TimeUnit.SECONDS);
	}

	private void startScheduledSynchronizer(int additionalIntervalSec) {
		ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				final Thread thread = Executors.defaultThreadFactory().newThread(r);
				thread.setName("redeemer-patrol");
				thread.setDaemon(true);
				return thread;
			}
		});
		ses.scheduleWithFixedDelay(new Patrol(), 0, additionalIntervalSec, TimeUnit.SECONDS);
	}

	@SuppressWarnings("rawtypes")
	private void collectSynchronizer() {
		Map<String, DataSynchronizer> syncMap = CXT.getBeansOfType(DataSynchronizer.class);
		if (syncMap != null && syncMap.size() > 0) {
			Set<Entry<String, DataSynchronizer>> entrySet = Sets.newHashSet();
			for (Entry<String, DataSynchronizer> entry: entrySet) {
				synchronizers.add(entry.getValue());
			}
		}
	}

	public static MongoClient getMongoClient() {
		return MongoClients.create(mongoAddresses);
	}
	
	private static final class Patrol implements Runnable {
		private static final ExecutorService es = Executors.newFixedThreadPool(additionalConfig.getSyncThreadPoolSize());
		@Override
		public void run() {
			if (!synchronizers.isEmpty()) {
				for (DataSynchronizer<?> sync: synchronizers) {
					es.submit(new SyncWorker(sync));
				}
			}
		}
	}
	
	private static final class SyncWorker implements Runnable {
		private DataSynchronizer<?> sync;
		private SyncWorker(DataSynchronizer<?> sync) {
			this.sync = sync;
		}
		@Override
		public void run() {
			MongoClient client = getMongoClient();
			MongoDatabase mDb = client.getDatabase(sync.mongoDBName());
			String comparableUniqueKey = sync.comparableUniqueKey();
			MongoCollection<Document> mCollection = mDb.getCollection(sync.collectionName());
			FindIterable<Document> fitr = mCollection.find()
					.sort(BsonDocument.parse("{" + comparableUniqueKey + ": -1}")).skip(0).limit(1);
			Long lastId = fitr.first().getLong(comparableUniqueKey);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			calendar.add(Calendar.SECOND, additionalConfig.getSyncIntervalSec() * -1);
			List<?> additionalDataList = sync.additionalSyncData(lastId, calendar.getTime());
			if (additionalDataList != null && !additionalDataList.isEmpty()) {
				List<Document> documents = Lists.newArrayList();
				Document doc = null;
				String jsonString = null;
				Long id = null;
				FindIterable<Document> tmpItr = null;
				try {
					for (Object obj: additionalDataList) {
						jsonString = MAPPER.writeValueAsString(obj);
						doc = Document.parse(jsonString);
						id = doc.getLong(comparableUniqueKey);
						tmpItr = mCollection.find(BsonDocument.parse("{" + comparableUniqueKey + ": " + id + "}"));
						if (tmpItr != null && tmpItr.first() != null) {
							mCollection.deleteOne(BsonDocument.parse("{" + comparableUniqueKey + ": " + id + "}"));
						}
						documents.add(Document.parse(jsonString));
					}
					if (!documents.isEmpty()) {
						mCollection.insertMany(documents);
					}
				} catch (JsonProcessingException e) {
					LOG.error(e.getMessage());
				} finally {
					client.close();
				}
			} else {
				client.close();
			}
		}
	}
	
	private static final class Cleaner implements Runnable {
		@Override
		public void run() {
			if (synchronizers != null && !synchronizers.isEmpty()) {
				MongoDataCleaner cleaner = null;
				MongoDatabase mDb = null;
				MongoCollection<Document> mCollection = null;
				MongoClient client = getMongoClient();
				for (DataSynchronizer<?> sync: synchronizers) {
					cleaner = sync.mongoDataCleaner();
					if (cleaner != null) {
						mDb = client.getDatabase(sync.mongoDBName());
						mCollection = mDb.getCollection(sync.collectionName());
						mCollection.deleteMany(cleaner.deleteFilter());
					}
				}
				client.close();
			}
		}
	}
	
}
