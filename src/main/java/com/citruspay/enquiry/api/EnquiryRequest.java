package com.citruspay.enquiry.api;

public class EnquiryRequest {
	private String merchantTxnId;
	private String merchantAccessKey;
	private String merchantRefundTxId;

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
	public String getMerchantRefundTxId() {
		return merchantRefundTxId;
	}
	public void setMerchantRefundTxId(String merchantRefundTxId) {
		this.merchantRefundTxId = merchantRefundTxId;
	}
}

