package com.active.services.redeemer.synchronizer;

import java.util.Date;
import java.util.List;

import com.mongodb.DBObject;

public interface DataSynchronizer<T> {

	List<T> additionalSyncData(Date modifiedAfter);
	
	String collectionName();
	
	String uniqueKey();
	
	List<T> fullSyncData(int batchSize, int pageNum);
	
	List<T> fullSyncData();
	
	DBObject indexKeys();
	
	MongoDataCleaner mongoDataCleaner();
	
	int fullSyncDataCount();
}
