package com.citruspay.enquiry.api;


import java.io.Serializable;

public class EnquiryResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String respCode;

	private String respMsg;

	private String txnId;

	private String pgTxnId;

	private String authIdCode;

	private String RRN;

	private String txnType;

	private String txnDateTime;

	private String amount;

	private String txnGateway;

	private String paymentMode;

	private String issuerCode;

	private String maskedCardNumber;

	private String cardType;

	private String cvResponseCode;

	private String totalRefundAmount;

	private String currency;

	private String originalAmount;

	private String adjustment;

	private String ruleName;
	
	private String couponCode;

	private String offerType;

	private String transactionAmount;
	
	private String impsMmid;
	
	private String impsMobileNumber;
	
	private String merchantRefundTxId;
	
	private String merchantTxnId;
	private String cardExpiryMonth;
	
	private String cardExpiryYear;
	
	private String cardHolderName;
	
	private String threeDsecure;
	
	private String eciValue;
	
	private String addressStreet1;

	private String addressStreet2;

	private String addressCity;

	private String addressState;

	private String addressCountry;

	private String addressZip;
	
		
	public String getRespCode() {
		return respCode;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}

	public String getRespMsg() {
		return respMsg;
	}

	public void setRespMsg(String respMsg) {
		this.respMsg = respMsg;
	}

	public String getTxnId() {
		return txnId;
	}

	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	public String getPgTxnId() {
		return pgTxnId;
	}

	public void setPgTxnId(String pgTxnId) {
		this.pgTxnId = pgTxnId;
	}

	public String getAuthIdCode() {
		return authIdCode;
	}

	public void setAuthIdCode(String authIdCode) {
		this.authIdCode = authIdCode;
	}

	public String getRRN() {
		return RRN;
	}

	public void setRRN(String rRN) {
		RRN = rRN;
	}

	public String getTxnType() {
		return txnType;
	}

	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}

	public String getTxnDateTime() {
		return txnDateTime;
	}

	public void setTxnDateTime(String txnDateTime) {
		this.txnDateTime = txnDateTime;
	}


	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getTxnGateway() {
		return txnGateway;
	}

	public void setTxnGateway(String txnGateway) {
		this.txnGateway = txnGateway;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getIssuerCode() {
		return issuerCode;
	}

	public void setIssuerCode(String issuerCode) {
		this.issuerCode = issuerCode;
	}

	public String getMaskedCardNumber() {
		return maskedCardNumber;
	}

	public void setMaskedCardNumber(String maskedCardNumber) {
		this.maskedCardNumber = maskedCardNumber;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getCvResponseCode() {
		return cvResponseCode;
	}

	public void setCvResponseCode(String cvResponseCode) {
		this.cvResponseCode = cvResponseCode;
	}

	public String getTotalRefundAmount() {
		return totalRefundAmount;
	}

	public void setTotalRefundAmount(String totalRefundAmount) {
		this.totalRefundAmount = totalRefundAmount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getOriginalAmount() {
		return originalAmount;
	}

	public void setOriginalAmount(String originalAmount) {
		this.originalAmount = originalAmount;
	}

	public String getAdjustment() {
		return adjustment;
	}

	public void setAdjustment(String adjustment) {
		this.adjustment = adjustment;
	}

	public String getOfferType() {
		return offerType;
	}

	public void setOfferType(String offerType) {
		this.offerType = offerType;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(String transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public String getImpsMmid() {
		return impsMmid;
	}

	public void setImpsMmid(String impsMmid) {
		this.impsMmid = impsMmid;
	}

	public String getImpsMobileNumber() {
		return impsMobileNumber;
	}

	public void setImpsMobileNumber(String impsMobileNumber) {
		this.impsMobileNumber = impsMobileNumber;
	}

	public String getMerchantRefundTxId() {
		return merchantRefundTxId;
	}

	public void setMerchantRefundTxId(String merchantRefundTxId) {
		this.merchantRefundTxId = merchantRefundTxId;
	}

	public String getMerchantTxnId() {
		return merchantTxnId;
	}

	public void setMerchantTxnId(String merchantTxnId) {
		this.merchantTxnId = merchantTxnId;
	}

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

	public String getCardExpiryMonth() {
		return cardExpiryMonth;
	}

	public void setCardExpiryMonth(String cardExpiryMonth) {
		this.cardExpiryMonth = cardExpiryMonth;
	}

	public String getCardExpiryYear() {
		return cardExpiryYear;
	}

	public void setCardExpiryYear(String cardExpiryYear) {
		this.cardExpiryYear = cardExpiryYear;
	}

	public String getCardHolderName() {
		return cardHolderName;
	}

	public void setCardHolderName(String cardHolderName) {
		this.cardHolderName = cardHolderName;
	}

	public String getThreeDsecure() {
		return threeDsecure;
	}

	public void setThreeDsecure(String threeDsecure) {
		this.threeDsecure = threeDsecure;
	}

	public String getEciValue() {
		return eciValue;
	}

	public void setEciValue(String eciValue) {
		this.eciValue = eciValue;
	}
	
	
	public String getAddressStreet1() {
		return addressStreet1;
	}

	public String getAddressStreet2() {
		return addressStreet2;
	}

	public String getAddressCity() {
		return addressCity;
	}

	public String getAddressState() {
		return addressState;
	}

	public String getAddressCountry() {
		return addressCountry;
	}

	public String getAddressZip() {
		return addressZip;
	}

	public void setAddress(String addressStreet1, String addressStreet2,
			String addressCity, String addressState, String addressCountry,
			String addressZip) {
		this.addressStreet1 = addressStreet1;
		this.addressStreet2 = addressStreet2;
		this.addressCity = addressCity;
		this.addressState = addressState;
		this.addressCountry = addressCountry;
		this.addressZip = addressZip;
	}

	@Override
	public String toString() {
		return "txnId:"+txnId +" merchantTxnId:"+ merchantTxnId+" cardExpiryMonth:"+cardExpiryMonth
				+" cardExpiryYear:"+cardExpiryYear+" cardHolderName:"+cardHolderName
				+" threeDSecure:"+threeDsecure+" eciValue:"+cardHolderName
;
	}
}

