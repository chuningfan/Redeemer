package com.active.services.redeemer.jmx;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.active.services.redeemer.core.Initializer;
import com.active.services.redeemer.synchronizer.DataSynchronizer;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

@ManagedResource(objectName = "camps-redeemer:name=Redeemer")
public class RedeemerMBean {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@ManagedOperation(description = "Synchronize all necessary data.")
    @ManagedOperationParameters({
    	@ManagedOperationParameter(name = "batchSize", description = "Data size of doing synchronization per time.")
    })
	public void syncData(int batchSize) throws IOException {
		String collectionName = null;
		DBCollection mCollection = null;
		DBObject indexKeys = null;
		List<?> dataList = null;
		int dataCount = 0;
		for (DataSynchronizer<?> sync: Initializer.synchronizers) {
			collectionName = sync.collectionName();
			if (mongoTemplate.collectionExists(collectionName)) {
				mongoTemplate.dropCollection(collectionName);
			}
			mCollection = mongoTemplate.createCollection(collectionName);
			indexKeys = sync.indexKeys();
			if (indexKeys != null) {
				mCollection.createIndex(indexKeys);
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
		}
	}
	
	private void insertIntoMongoDB(DBCollection mCollection, List<?> dataList) throws IOException {
		List<DBObject> documents = Lists.newArrayList();
		for (Object obj: dataList) {
			documents.add(BasicDBObject.parse(Initializer.MAPPER.writeValueAsString(obj)));
		}
		mCollection.insert(documents);
	}
	
	
}
