/*
 * Copyright (c) 2012 CitrusPay. All Rights Reserved.
 *
 * This software is the proprietary information of CitrusPay.
 * Use is subject to license terms.
 */
package com.citruspay;

public class CPRuntimeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1038887053680748442L;

	private String errorTitle;
	
	private String errorMessage;
	
	public String getErrorTitle() {
		return errorTitle;
	}

	public void setErrorTitle(String errorTitle) {
		this.errorTitle = errorTitle;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public CPRuntimeException() {
		super();
		errorTitle = "error_uncaughtexception_title";
		errorMessage = "error_uncaughtexception_title";
	}
	
	public CPRuntimeException(String message) {
		super(message);
	}
	
	public CPRuntimeException(String errorTitle, String errorCode) {
		super();
		this.errorTitle = errorTitle;
		this.errorMessage = errorCode;
	}

	public CPRuntimeException(Exception e) {
		super(e);
	}

}
