package com.active.services.redeemer.synchronizer;

import java.util.Date;
import java.util.List;

import org.bson.conversions.Bson;

public interface DataSynchronizer<T> {

	List<T> additionalSyncData(Long lastId, Date modifiedDate);
	
	String mongoDBName();
	
	String collectionName();
	
	String uniqueKey();
	
	List<T> fullSyncData(int batchSize, int pageNum);
	
	List<Bson> indexKeys();
	
	String comparableUniqueKey();
	
	MongoDataCleaner mongoDataCleaner();
	
	int fullSyncDataCount();
}
