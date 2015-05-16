package com.citruspay.enquiry.api;

public class EnquiryRequest {
	private String merchantTxnId;
	public String getMerchantTxnId() {
		return merchantTxnId;
	}
	public void setMerchantTxnId(String merchantTxnId) {
		this.merchantTxnId = merchantTxnId;
	}
	public String getMerchantAccessKey() {
		return merchantAccessKey;
	}
	public void setMerchantAccessKey(String merchantAccessKey) {
		this.merchantAccessKey = merchantAccessKey;
	}
	private String merchantAccessKey;
}

