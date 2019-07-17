package com.active.services.redeemer.core;

public class Configuration {
	
	private String mongoDBAdresses; // separated by comma
	
	// Username of MongoDB
	private String mongoUser;
		
	// Password of MongoDB
	private String mongoPassword;

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
	
}
