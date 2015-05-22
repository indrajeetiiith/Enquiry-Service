package com.citruspay.enquiry.api;

public class EnquiryRequest {
	private String merchantTxnId;
	private String merchantAccessKey;
	private String merchantRefundTxId;
	private String signature;
	private boolean paymentDetailsRequired;
	private boolean addressDetailsRequired;
	private boolean pricingDetailsRequired;
	
	
	public boolean isPaymentDetailsRequired() {
		return paymentDetailsRequired;
	}
	public void setPaymentDetailsRequired(boolean paymentDetailsRequired) {
		this.paymentDetailsRequired = paymentDetailsRequired;
	}
	public boolean isAddressDetailsRequired() {
		return addressDetailsRequired;
	}
	public void setAddressDetailsRequired(boolean addressDetailsRequired) {
		this.addressDetailsRequired = addressDetailsRequired;
	}
	public boolean isPricingDetailsRequired() {
		return pricingDetailsRequired;
	}
	public void setPricingDetailsRequired(boolean pricingDetailsRequired) {
		this.pricingDetailsRequired = pricingDetailsRequired;
	}
	public boolean isPaymentDetail() {
		return paymentDetail;
	}
	public void setPaymentDetail(boolean paymentDetail) {
		this.paymentDetail = paymentDetail;
	}
	public boolean isAddressDetail() {
		return addressDetail;
	}
	public void setAddressDetail(boolean addressDetail) {
		this.addressDetail = addressDetail;
	}
	@Override
	public String toString() {
		return "EnquiryRequest [merchantTxnId=" + merchantTxnId
				+ ", merchantAccessKey=" + merchantAccessKey
				+ ", merchantRefundTxId=" + merchantRefundTxId + ", signature="
				+ signature + ", paymentDetailsRequired="
				+ paymentDetailsRequired + ", addressDetailsRequired="
				+ addressDetailsRequired + ", pricingDetailsRequired="
				+ pricingDetailsRequired + ", paymentDetail=" + paymentDetail
				+ ", addressDetail=" + addressDetail + ", pricingDetail="
				+ pricingDetail + "]";
	}
	public boolean isPricingDetail() {
		return pricingDetail;
	}
	public void setPricingDetail(boolean pricingDetail) {
		this.pricingDetail = pricingDetail;
	}
	private boolean paymentDetail;
	private boolean addressDetail;
	private boolean pricingDetail;
	
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
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

