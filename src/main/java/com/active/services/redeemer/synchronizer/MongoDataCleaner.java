package com.active.services.redeemer.synchronizer;

import org.bson.conversions.Bson;

public interface MongoDataCleaner {
	
	String mongoDBName();
	
	String collectionName();
	
	Bson deleteFilter();
	
}
