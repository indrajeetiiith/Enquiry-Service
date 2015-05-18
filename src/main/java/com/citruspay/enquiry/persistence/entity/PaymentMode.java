package com.citruspay.enquiry.persistence.entity;

public enum PaymentMode {
	NET_BANKING("NB", "Net Banking"), CREDIT_CARD("CC", "Credit Card"), DEBIT_CARD(
			"DC", "Debit Card"), CASH_ON_DELIVERY("COD", "Cash On Delivery"), PREPAID_CARD(
			"PPC", "Prepaid Card"), IMPS("IMPS", "IMPS"), EMI("EMI","EMI"), ATM_CARD("ATMC", "ATM Card");

	private String initial;
	private String displayString;

	private PaymentMode(String initial, String displayString) {
		this.initial = initial;
		this.displayString = displayString;
	}

	public String getInitial() {
		return this.initial;
	}

	public String getDisplayString() {
		return this.displayString;
	}

	public static PaymentMode getPaymentMode(String displayString) {
		for (PaymentMode mode : PaymentMode.values()) {
			if (mode.getDisplayString().equals(displayString)) {
				return mode;
			}
		}
		return null;
	}
}
