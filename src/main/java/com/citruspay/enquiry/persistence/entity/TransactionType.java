package com.citruspay.enquiry.persistence.entity;

public enum TransactionType {
	SALE("Sale"), PREAUTH("Pre auth"), REFUND("Refund"), CHARGE_BACK(
			"Charge back"), LOAD("Prepaid Load"), RELOAD("Prepaid Reload"), CAPTURE(
			"Capture"), VOID("Reversed");

	private String displayLabel;

	private TransactionType(String displayLabel) {
		this.displayLabel = displayLabel;
	}

	public String getDisplayLabel() {
		return this.displayLabel;
	}
}
