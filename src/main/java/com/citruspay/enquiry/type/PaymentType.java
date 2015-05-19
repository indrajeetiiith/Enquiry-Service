package com.citruspay.enquiry.type;

public enum PaymentType {
	NET_BANKING("Net Banking"), CARD("Card"), ALL("All"), CREDIT_CARD(
			"Credit Card"), DEBIT_CARD("Debit Card"), CASH_ON_DELIVERY("COD"), PREPAID_CARD(
			"PPC"), IMPS("IMPS"), ATM_CARD("ATM Card");

	private String paymentType;

	private PaymentType() {

	}

	private PaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
}
