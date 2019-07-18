package com.active.services.redeemer.exception;

import org.springframework.beans.BeansException;

public class RedeemerStartupException extends BeansException {

	private static final long serialVersionUID = -7417854263693998615L;

	public RedeemerStartupException(String message) {
		super("[REDEEMER]: " + message);
	}
	
}
