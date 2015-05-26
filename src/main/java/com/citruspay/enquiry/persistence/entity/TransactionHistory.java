/*
 * Copyright (c) 2012 CitrusPay. All Rights Reserved.
 *
 * This software is the proprietary information of CitrusPay.
 * Use is subject to license terms.
 */
package com.citruspay.enquiry.persistence.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.citruspay.enquiry.persistence.entity.TransactionStatus;

@Entity
@Table(name = "TXN_TRANSACTION_HISTORY")
public class TransactionHistory implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 131748038881435078L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long txnId;

	@NotNull
	private String merchantId;

	private String txId;

	@NotNull
	@Size(min = 1)
	private String merchantTxId;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private PGTransaction pgTxResp;

	@OneToOne
	private PaymentDetail paymentDetails;

	@NotNull
	private TransactionStatus status;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	private String clientAddr;

	private String txnGateway;


	private Integer pgId;

	private String itc;

	private Long dpRuleId;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Version
	private Date lastModified;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTxnId() {
		return txnId;
	}

	public void setTxnId(Long txnId) {
		this.txnId = txnId;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getTxId() {
		return txId;
	}

	public void setTxId(String txId) {
		this.txId = txId;
	}

	public String getMerchantTxId() {
		return merchantTxId;
	}

	public void setMerchantTxId(String merchantTxId) {
		this.merchantTxId = merchantTxId;
	}

	public PGTransaction getPgTxResp() {
		return pgTxResp;
	}

	public void setPgTxResp(PGTransaction pgTxResp) {
		this.pgTxResp = pgTxResp;
	}

	@JsonIgnore
	public PaymentDetail getPaymentDetails() {
		return paymentDetails;
	}

	public void setPaymentDetails(PaymentDetail paymentDetails) {
		this.paymentDetails = paymentDetails;
	}

	public TransactionStatus getStatus() {
		return status;
	}

	public void setStatus(TransactionStatus status) {
		this.status = status;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getClientAddr() {
		return clientAddr;
	}

	public void setClientAddr(String clientAddr) {
		this.clientAddr = clientAddr;
	}

	public String getTxnGateway() {
		return txnGateway;
	}

	public void setTxnGateway(String txnGateway) {
		this.txnGateway = txnGateway;
	}


	public Integer getPgId() {
		return pgId;
	}

	public void setPgId(Integer pgId) {
		this.pgId = pgId;
	}

	public String getItc() {
		return itc;
	}

	public void setItc(String itc) {
		this.itc = itc;
	}

	public Long getDpRuleId() {
		return dpRuleId;
	}

	public void setDpRuleId(Long dpRuleId) {
		this.dpRuleId = dpRuleId;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	@Override
	public String toString() {
		return "TransactionHistory [id=" + id + ", txnId=" + txnId
				+ ", merchantId=" + merchantId + ", txId=" + txId
				+ ", merchantTxId=" + merchantTxId + ", pgTxResp=" + pgTxResp
				+ ", paymentDetails=" + paymentDetails + ", status=" + status
				+ ", created=" + created + ", clientAddr=" + clientAddr
				+ ", txnGateway=" + txnGateway
				+ ", pgId=" + pgId + ", itc=" + itc + ", dpRuleId=" + dpRuleId
				+ ", lastModified=" + lastModified
				+ "]";
	}

}
