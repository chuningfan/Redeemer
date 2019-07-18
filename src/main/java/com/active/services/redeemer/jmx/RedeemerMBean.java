package com.active.services.redeemer.jmx;

import java.io.IOException;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.active.services.redeemer.core.Initializer;
import com.active.services.redeemer.synchronizer.DataSynchronizer;
import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@ManagedResource(objectName = "camps-redeemer:name=Redeemer")
public class RedeemerMBean {

	@ManagedOperation(description = "Synchronize all necessary data.")
    @ManagedOperationParameters({
    	@ManagedOperationParameter(name = "batchSize", description = "Data size of doing synchronization per time.")
    })
	public void syncData(int batchSize) throws IOException {
		MongoClient client = null;
		MongoDatabase mDb = null;
		MongoCollection<Document> mCollection = null;
		List<Bson> indexKeys = null;
		List<?> dataList = null;
		int dataCount = 0;
		for (DataSynchronizer<?> sync: Initializer.synchronizers) {
			client = Initializer.getMongoClient(sync.mongoDBName());
			mDb = client.getDatabase(sync.mongoDBName());
			mCollection.drop();
			mDb.createCollection(sync.collectionName());
			mCollection = mDb.getCollection(sync.collectionName());
			indexKeys = sync.indexKeys();
			if (indexKeys != null && !indexKeys.isEmpty()) {
				for (Bson keys: indexKeys) {
					mCollection.createIndex(keys);
				}
			}
			dataCount = sync.fullSyncDataCount();
			if (dataCount > 0) {
				if (batchSize > 0) {
					int pageSize = dataCount/batchSize == 0 ? 1 : (dataCount/batchSize) + (dataCount/batchSize > 0 ? 1 : 0);
					for (int pageNum = 1; pageNum <= pageSize; pageNum ++) {
						dataList = sync.fullSyncData(batchSize, pageNum);
						insertIntoMongoDB(mCollection, dataList);
					}
				} else {
					dataList = sync.fullSyncData();
					insertIntoMongoDB(mCollection, dataList);
				}
			}
			client.close();
		}
	}
	
	private void insertIntoMongoDB(MongoCollection<Document> mCollection, List<?> dataList) throws IOException {
		List<Document> documents = Lists.newArrayList();
		for (Object obj: dataList) {
			documents.add(Document.parse(Initializer.MAPPER.writeValueAsString(obj)));
		}
		mCollection.insertMany(documents);
	}
	
	
}
