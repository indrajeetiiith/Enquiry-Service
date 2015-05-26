package com.citruspay.enquiry.exceptions;

/**
 * 
 * @author Indrajeet
 *
 */
public class SignatureValidationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public SignatureValidationException() {
		super("HMac Signature Validation Failed!");
	}

}
