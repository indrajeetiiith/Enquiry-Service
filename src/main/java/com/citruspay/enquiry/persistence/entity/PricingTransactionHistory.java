/*
 * Copyright (c) 2012 CitrusPay. All Rights Reserved.
 *
 * This software is the proprietary information of CitrusPay.
 * Use is subject to license terms.
 */
package com.citruspay.enquiry.persistence.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.citruspay.BaseDomain;

@SuppressWarnings("serial")
@Entity
@Table(name = "dp_transaction_history")
public class PricingTransactionHistory extends BaseDomain {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long Id;

	private BigDecimal amount;

	private BigDecimal originalAmount;

	private BigDecimal adjustment;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "rule_id")
	private PricingRule pricingRule;
	
	private BigDecimal txnAmount;

	private int isDeleted;

	public PricingTransactionHistory() {
	}

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getOriginalAmount() {
		return originalAmount;
	}

	public void setOriginalAmount(BigDecimal originalAmount) {
		this.originalAmount = originalAmount;
	}

	public PricingRule getPricingRule() {
		return pricingRule;
	}

	public void setPricingRule(PricingRule pricingRule) {
		this.pricingRule = pricingRule;
	}

	public BigDecimal getAdjustment() {
		return adjustment;
	}

	public void setAdjustment(BigDecimal adjustment) {
		this.adjustment = adjustment;
	}

	public int getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(int isDeleted) {
		this.isDeleted = isDeleted;
	}

	public BigDecimal getTxnAmount() {
		return txnAmount;
	}

	public void setTxnAmount(BigDecimal txnAmount) {
		this.txnAmount = txnAmount;
	}

	
}
