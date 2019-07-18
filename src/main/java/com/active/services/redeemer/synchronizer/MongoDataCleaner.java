package com.active.services.redeemer.synchronizer;

import com.mongodb.DBObject;

public interface MongoDataCleaner {
	
	String collectionName();
	
	DBObject deleteFilter();
	
}
