package com.active.services.redeemer.synchronizer;

import java.util.Date;
import java.util.List;

import com.mongodb.DBObject;

public interface DataSynchronizer<T> {

	List<T> additionalSyncData(Date modifiedAfter);
	
	String collectionName();
	
	default String uniqueKey() {
		return "id";
	}
	
	List<T> fullSyncData(int batchSize, int pageNum);
	
	List<T> fullSyncData();
	
	DBObject indexKeys();
	
	int fullSyncDataCount();
}
