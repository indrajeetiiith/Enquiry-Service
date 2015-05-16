/*
 * Copyright (c) 2012 CitrusPay. All Rights Reserved.
 *
 * This software is the proprietary information of CitrusPay.
 * Use is subject to license terms.
 */
package com.citruspay.enquiry.persistence.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "TXN_PG_TRANSACTION")
public class PGTransaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String responseCode;

	private String message;

	private String authIdCode;

	private String issuerRefNo;

	private String txnId;

	private String pgTxnId;

	private String paymentId;

	private String errorMessage;

	private String cvRespCode;

	/*
	 * private String fdmsResult;
	 * 
	 * private String fdmsScore;
	 */

	private String authReceiptNo;

	private String captureReceiptNo;

	private String refTxnNo;

	private int inquiryStatus;

	@Temporal(TemporalType.TIMESTAMP)
	private Date inquiryDate; 
	
	@Column(name = "three_d_secure")
	private String threeDSecure;

	private String eciValue;

	public PGTransaction() {
	}

	public PGTransaction(String responseCode, String message,
			String authIdCode, String issuerRefNo, String txnId,
			String pgTxnId, String paymentId) {
		this.responseCode = responseCode;
		this.message = message;
		this.authIdCode = authIdCode;
		this.issuerRefNo = issuerRefNo;
		this.txnId = txnId;
		this.pgTxnId = pgTxnId;
		this.paymentId = paymentId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getAuthIdCode() {
		return authIdCode;
	}

	public void setAuthIdCode(String authIdCode) {
		this.authIdCode = authIdCode;
	}

	public String getIssuerRefNo() {
		return issuerRefNo;
	}

	public void setIssuerRefNo(String issuerRefNo) {
		this.issuerRefNo = issuerRefNo;
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

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getCvRespCode() {
		return cvRespCode;
	}

	public void setCvRespCode(String cvRespCode) {
		this.cvRespCode = cvRespCode;
	}

	/*
	 * public String getFdmsResult() { return fdmsResult; }
	 * 
	 * public void setFdmsResult(String fdmsResult) { this.fdmsResult =
	 * fdmsResult; }
	 * 
	 * public String getFdmsScore() { return fdmsScore; }
	 * 
	 * public void setFdmsScore(String fdmsScore) { this.fdmsScore = fdmsScore;
	 * }
	 */

	public String getAuthReceiptNo() {
		return authReceiptNo;
	}

	public void setAuthReceiptNo(String authReceiptNo) {
		this.authReceiptNo = authReceiptNo;
	}

	public String getCaptureReceiptNo() {
		return captureReceiptNo;
	}

	public void setCaptureReceiptNo(String captureReceiptNo) {
		this.captureReceiptNo = captureReceiptNo;
	}

	public String getRefTxnNo() {
		return refTxnNo;
	}

	public void setRefTxnNo(String refTxnNo) {
		this.refTxnNo = refTxnNo;
	}

	public int getInquiryStatus() {
		return inquiryStatus;
	}

	public void setInquiryStatus(int inquiryStatus) {
		this.inquiryStatus = inquiryStatus;
	}

	public Date getInquiryDate() {
		return inquiryDate;
	}

	public void setInquiryDate(Date inquiryDate) {
		this.inquiryDate = inquiryDate;
	}

	public String getThreeDSecure() {
		return threeDSecure;
	}

	public void setThreeDSecure(String threeDSecure) {
		this.threeDSecure = threeDSecure;
	}

	public String getEciValue() {
		return eciValue;
	}

	public void setEciValue(String eciValue) {
		this.eciValue = eciValue;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(responseCode).append(",");
		sb.append(message).append(",");
		sb.append(authIdCode).append(",");
		sb.append(issuerRefNo).append(",");
		sb.append(txnId).append(",");
		sb.append(pgTxnId).append(",");
		sb.append(threeDSecure).append(",");
		sb.append(eciValue);
		return sb.toString();
	}
}
