package com.citruspay.enquiry.persistence.entity;

public enum TransactionStatus {
	SUCCESS("Success", "Transaction successful"),
	FAIL("Fail", "Transaction failure"),
	IN_PROGRESS("User dropped", "Transaction not completed by consumer"),
	CANCELED("Canceled", "Transaction not completed by consumer"),
	FORWARDED("Forwarded", "Transaction not completed by consumer"),
	PG_FORWARD_FAIL("Pg forward fail", "Transaction not completed by consumer"),
	INQUIRY_STATUS_FAILED("Inquiry status failed", "Transaction not completed by consumer"),
	SESSION_EXPIRED("Session expired", "Transaction not completed by consumer"),
	REFUND_INITIATED("Refund initiated", "Refund initiated"),
	REFUND_FORWARDED("Refund forwarded", "Refund in process"),
	REFUND_PROCESS("Refund process", "Refund in process"),
	REFUND_SUCCESS("Refund success", "Refund successful"),
	REFUND_FAILED("Refund failed", "Refund failure"),
	DEBIT_REQ_SENT("Pending verification", "Pending verification"),
	SUCCESS_ON_VERIFICATION("Success on verification", "Transaction successful"),
	PG_REJECTED("Rejected by payment gateway", "Rejected by payment gateway"),
	REVERSED("Reversed", "Void successful"),
	ON_HOLD("Denied by Risk", "Denied by Risk"),
	AUTHORIZATION_REQUEST_SENT("Athorization Request Sent", "Transaction Successful. Waiting For Authorisation"),
	AUTHORIZER_REJECTED("Authorizer Rejected", "Transaction Fail. Authorizer Rejected"),
	CAPTURE_INITIATED("Capture initiated","Capture initiated"), 
	CAPTURE_FORWARDED("Capture forwarded","Capture in process"), 
	CAPTURE_SUCCESS("Capture success","Capture successful"), 
	CAPTURE_FAILED("Capture failed","Capture failure"),
	V2_REQUEST_ARRIVED("User Request arrived", "Request records"),
	V2_PAGE_RENDERED("Checkout page rendered", "Request records");
	
	private String displayLabel;
	private String displayMsg;

	private TransactionStatus(String displayLabel) {
		this.displayLabel = displayLabel;
	}

	private TransactionStatus(String displayLabel, String displayMsg) {
		this.displayLabel = displayLabel;
		this.displayMsg = displayMsg;
	}

	public String getDisplayMsg() {
		return this.displayMsg;
	}

	public String getDisplayLabel() {
		return this.displayLabel;
	}
}
