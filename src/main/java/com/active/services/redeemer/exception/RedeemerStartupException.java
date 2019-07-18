package com.active.services.redeemer.exception;

public class RedeemerStartupException extends Exception {

	private static final long serialVersionUID = -7417854263693998615L;

	public RedeemerStartupException(String message) {
		super("[REDEEMER]: " + message);
	}
	
}
