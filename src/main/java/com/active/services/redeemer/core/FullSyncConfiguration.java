package com.active.services.redeemer.core;

public class FullSyncConfiguration extends Configuration {
	
	private int batchSize;
	
	private String appDataSource;

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public String getAppDataSource() {
		return appDataSource;
	}

	public void setAppDataSource(String appDataSource) {
		this.appDataSource = appDataSource;
	}
	
}
