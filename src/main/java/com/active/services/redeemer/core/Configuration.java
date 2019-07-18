package com.active.services.redeemer.core;

public class Configuration {
	
	static final String NOTHING = "N/A";
	
	private String mongoDBAdresses; // separated by comma
	
	// Username of MongoDB
	private String mongoUser = NOTHING;
		
	// Password of MongoDB
	private String mongoPassword = NOTHING;
	
	private int syncThreadPoolSize = 3;
	
	// synchronization interval sec
	private int syncIntervalSec;
	
	// interval of cleaning unused data
	private int cleanIntervalSec;

	public String getMongoDBAdresses() {
		return mongoDBAdresses;
	}

	public void setMongoDBAdresses(String mongoDBAdresses) {
		this.mongoDBAdresses = mongoDBAdresses;
	}

	public String getMongoUser() {
		return mongoUser;
	}

	public void setMongoUser(String mongoUser) {
		this.mongoUser = mongoUser;
	}

	public String getMongoPassword() {
		return mongoPassword;
	}

	public void setMongoPassword(String mongoPassword) {
		this.mongoPassword = mongoPassword;
	}

	public int getSyncThreadPoolSize() {
		return syncThreadPoolSize;
	}

	public void setSyncThreadPoolSize(int syncThreadPoolSize) {
		this.syncThreadPoolSize = syncThreadPoolSize;
	}

	public int getSyncIntervalSec() {
		return syncIntervalSec;
	}

	public void setSyncIntervalSec(int syncIntervalSec) {
		this.syncIntervalSec = syncIntervalSec;
	}

	public int getCleanIntervalSec() {
		return cleanIntervalSec;
	}

	public void setCleanIntervalSec(int cleanIntervalSec) {
		this.cleanIntervalSec = cleanIntervalSec;
	}
	
}
