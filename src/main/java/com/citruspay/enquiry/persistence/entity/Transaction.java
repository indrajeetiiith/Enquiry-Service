/*
 * Copyright (c) 2012 CitrusPay. All Rights Reserved.
 *
 * This software is the proprietary information of CitrusPay.
 * Use is subject to license terms.
 */
package com.citruspay.enquiry.persistence.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.citruspay.enquiry.persistence.entity.ConsumerDetail;
import com.citruspay.enquiry.persistence.entity.PaymentDetail;
import com.citruspay.enquiry.persistence.entity.Amount;
import com.citruspay.enquiry.persistence.entity.Merchant;
import com.citruspay.enquiry.persistence.entity.TransactionStatus;
import com.citruspay.enquiry.persistence.entity.TransactionType;

@Entity
@Table(name = "TXN_TRANSACTION")
public class Transaction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7662639016202126293L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	private ConsumerDetail consumerDetail;

	
	public ConsumerDetail getConsumerDetail() {
		return consumerDetail;
	}
	public void setConsumerDetail(ConsumerDetail consumerDetail) {
		this.consumerDetail = consumerDetail;
	}
	@NotNull
	@ManyToOne
	private Merchant merchant;

	private Integer paymentModeChange;

	@NotNull
	@Size(min = 1)
	private String merchantTxId;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private PGTransaction pgTxResp;

	
	public PGTransaction getPgTxResp() {
		return pgTxResp;
	}
	public void setPgTxResp(PGTransaction pgTxResp) {
		this.pgTxResp = pgTxResp;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Version
	private Date lastModified;
	
	private Integer pgId;

	@NotNull
	private TransactionStatus status;


	public TransactionStatus getStatus() {
		return status;
	}
	public void setStatus(TransactionStatus status) {
		this.status = status;
	}
	public Integer getPgId() {
		return pgId;
	}
	public void setPgId(Integer pgId) {
		this.pgId = pgId;
	}
	@Embedded
	@NotNull
	@Valid
	private Amount orderAmount;
	
	@OneToOne
	private PaymentDetail paymentDetails;

	
	public PaymentDetail getPaymentDetails() {
		return paymentDetails;
	}
	public void setPaymentDetails(PaymentDetail paymentDetails) {
		this.paymentDetails = paymentDetails;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;


	public Integer getPaymentModeChange() {
		return paymentModeChange;
	}
	public void setPaymentModeChange(Integer paymentModeChange) {
		this.paymentModeChange = paymentModeChange;
	}
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(id).append(",");
		sb.append(txId).append(",");
		sb.append(merchantTxId).append(",");
		sb.append(orderAmount).append(",");
		if (paymentDetails != null) {
			sb.append(paymentDetails).append(",");
		}
		sb.append(status).append(",");


		if (pgTxResp != null) {
			sb.append(pgTxResp).append(",");
		}
		if (merchant != null) {
			sb.append(merchant.getId()).append(",");
		}
		return sb.toString();
	}


	public Transaction() {
	}

	public Transaction(Merchant merchant,ConsumerDetail consumerDetail,
			String txId, Amount orderAmount) {
		super();

		this.merchant = merchant;
		this.consumerDetail = consumerDetail;
		this.merchantTxId = txId;
		this.orderAmount = orderAmount;
		this.created = new Date();

	}

	
	public Amount getOrderAmount() {
		return orderAmount;
	}
	public void setOrderAmount(Amount orderAmount) {
		this.orderAmount = orderAmount;
	}
	public Date getLastModified() {
		return lastModified;
	}
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	private String txId;
	private String txnGateway;

	public String getTxnGateway() {
		return txnGateway;
	}
	public void setTxnGateway(String txnGateway) {
		this.txnGateway = txnGateway;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTxId() {
		return txId;
	}
	public void setTxId(String txId) {
		this.txId = txId;
	}
	public Merchant getMerchant() {
		return merchant;
	}
	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}
	public String getMerchantTxId() {
		return merchantTxId;
	}
	public void setMerchantTxId(String merchantTxId) {
		this.merchantTxId = merchantTxId;
	}

}