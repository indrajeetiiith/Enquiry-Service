/*
 * Copyright (c) 2012 CitrusPay. All Rights Reserved.
 *
 * This software is the proprietary information of CitrusPay.
 * Use is subject to license terms.
 */
package com.citruspay.enquiry.persistence.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Embeddable;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.citruspay.CommonUtil;

@SuppressWarnings("serial")
@Embeddable
public class Amount implements Serializable {
	@NotNull
	@Size(min=1)
	private String currency = Currency.INR.name();

	@NotNull
	@DecimalMin(value = "0.0")
	private BigDecimal amount;

	public Amount() {

	}

	public Amount(String currency, BigDecimal amount) {
		super();
		this.currency = currency;
		if(CommonUtil.isNotNull(amount)){
			this.amount = amount.setScale(CommonUtil.DECIMAL_PLACES, BigDecimal.ROUND_HALF_UP);
		}else{
			this.amount = amount;
		}
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getAmount() {
		return amount;
				
	}

	public void setAmount(BigDecimal amount) {
		if(CommonUtil.isNotNull(amount)){
			this.amount = amount.setScale(CommonUtil.DECIMAL_PLACES, BigDecimal.ROUND_HALF_UP);
		}else{
			this.amount = amount;
		}
		
	}

	public String toString(){
		return amount + " " + currency;
	}	
}
