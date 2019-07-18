package com.active.services.redeemer.core;

public class Configuration {
	
	static final String NOTHING = "N/A";
	
	private int syncThreadPoolSize = 3;
	
	// synchronization interval sec
	private int syncIntervalSec;
	
	// interval of cleaning unused data
	private int cleanIntervalSec;

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
