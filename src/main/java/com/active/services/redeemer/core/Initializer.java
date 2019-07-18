package com.active.services.redeemer.core;

import java.io.IOException;
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

import org.bson.Document;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.active.services.redeemer.exception.RedeemerStartupException;
import com.active.services.redeemer.synchronizer.DataSynchronizer;
import com.active.services.redeemer.synchronizer.MongoDataCleaner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.ServerAddress;

public class Initializer {
	
	private static final Logger LOG = LoggerFactory.getLogger(Initializer.class);
	
	public static final ObjectMapper MAPPER = new ObjectMapper();
	
	public static ApplicationContext CXT;
	
	static Configuration additionalConfig;
	
	static List<ServerAddress> mongoAddressList = Lists.newArrayList();
	
	private MongoTemplate mongoTemplate;
	
	public static List<DataSynchronizer<?>> synchronizers = Lists.newArrayList();
	
	public static List<MongoDataCleaner> cleaners = Lists.newArrayList();
	
	Initializer(ApplicationContext cxt, MongoTemplate mongoTemplate) throws RedeemerStartupException {
		CXT = cxt;
		additionalConfig = cxt.getBean(Configuration.class);
		this.mongoTemplate = cxt.getBean(MongoTemplate.class);
		init();
	}

	private void init() throws RedeemerStartupException {
		collectSynchronizer();
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
		ses.scheduleWithFixedDelay(new Cleaner(mongoTemplate), 0, cleanIntervalSec, TimeUnit.SECONDS);
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
		ses.scheduleWithFixedDelay(new Patrol(mongoTemplate), 0, additionalIntervalSec, TimeUnit.SECONDS);
	}

	@SuppressWarnings("rawtypes")
	private void collectSynchronizer() {
		Map<String, DataSynchronizer> syncMap = CXT.getBeansOfType(DataSynchronizer.class);
		if (syncMap != null && syncMap.size() > 0) {
			Set<Entry<String, DataSynchronizer>> entrySet = Sets.newHashSet();
			for (Entry<String, DataSynchronizer> entry: entrySet) {
				synchronizers.add(entry.getValue());
			}
			// create synchronization thread pool
			int additionalIntervalSec = additionalConfig.getSyncIntervalSec();
			startScheduledSynchronizer(additionalIntervalSec);
			// prepare mongoDB cleaner
			Map<String, MongoDataCleaner> cleanMap = CXT.getBeansOfType(MongoDataCleaner.class);
			if (cleanMap != null && cleanMap.size() > 0) {
				Set<Entry<String, MongoDataCleaner>> cleanerEntrySet = Sets.newHashSet();
				for (Entry<String, MongoDataCleaner> entry: cleanerEntrySet) {
					cleaners.add(entry.getValue());
				}
				int cleanIntervalSec = additionalConfig.getCleanIntervalSec();
				startMongoDataCleaner(cleanIntervalSec);
			}
		} else {
			LOG.info("No mongo DB synchronizer was found!");
		}
	}

	private static class Patrol implements Runnable {
		private static final ExecutorService es = Executors.newFixedThreadPool(additionalConfig.getSyncThreadPoolSize());
		private MongoTemplate mongoTemplate;
		private Patrol(MongoTemplate mongoTemplate) {
			this.mongoTemplate = mongoTemplate;
		}
		@Override
		public void run() {
			if (!synchronizers.isEmpty()) {
				for (DataSynchronizer<?> sync: synchronizers) {
					es.submit(new SyncWorker(sync, mongoTemplate));
				}
			}
		}
	}
	
	private static class SyncWorker implements Runnable {
		private DataSynchronizer<?> sync;
		private MongoTemplate mongoTemplate;
		private SyncWorker(DataSynchronizer<?> sync, MongoTemplate mongoTemplate) {
			this.sync = sync;
			this.mongoTemplate = mongoTemplate;
		}
		@Override
		public void run() {
			String uniqueKey = sync.uniqueKey();
			DBCollection mCollection = mongoTemplate.getCollection(sync.collectionName());
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			calendar.add(Calendar.SECOND, additionalConfig.getSyncIntervalSec() * -1);
			List<?> additionalDataList = sync.additionalSyncData(calendar.getTime());
			if (additionalDataList != null && !additionalDataList.isEmpty()) {
				List<BasicDBObject> documents = Lists.newArrayList();
				Document doc = null;
				String jsonString = null;
				Object id = null;
				try {
					for (Object obj: additionalDataList) {
						jsonString = MAPPER.writeValueAsString(obj);
						doc = Document.parse(jsonString);
						id = doc.get(uniqueKey);
						mCollection.findAndRemove(BasicDBObject.parse("{" + uniqueKey + ": " + id + "}"));
						documents.add(BasicDBObject.parse(jsonString));
					}
					if (!documents.isEmpty()) {
						mCollection.insert(documents);
					}
				} catch (IOException e) {
					LOG.error(e.getMessage());
				}
			}
		}
	}
	
	private static class Cleaner implements Runnable {
		private MongoTemplate mongoTemplate;
		private Cleaner(MongoTemplate mongoTemplate) {
			this.mongoTemplate = mongoTemplate;
		}
		@Override
		public void run() {
			if (cleaners != null && !cleaners.isEmpty()) {
				DBCollection mCollection = null;
				for (MongoDataCleaner cleaner: cleaners) {
					mCollection = mongoTemplate.getCollection(cleaner.collectionName());
					mCollection.remove(cleaner.deleteFilter());
				}
			}
		}
	}
	
}
