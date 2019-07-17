package com.active.services.redeemer.core;

public class AdditionalSyncConfiguration extends Configuration {

	// synchronization thread pool size
	private int syncThreadPoolSize = 3;
	
	// synchronization interval sec
	private int syncIntervalSec;
	
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
