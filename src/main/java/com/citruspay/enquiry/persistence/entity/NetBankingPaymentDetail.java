/*
 * Copyright (c) 2012 CitrusPay. All Rights Reserved.
 *
 * This software is the proprietary information of CitrusPay.
 * Use is subject to license terms.
 */
package com.citruspay.enquiry.persistence.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.citruspay.enquiry.persistence.entity.Issuer;

@SuppressWarnings("serial")
@Entity
@DiscriminatorValue(value = "NET_BANKING")
public class NetBankingPaymentDetail extends PaymentDetail {

	@NotNull
	@ManyToOne
	private Issuer bank;

	public NetBankingPaymentDetail() {
	}

	public NetBankingPaymentDetail(Issuer bank) {
		this.bank = bank;
	}

	public Issuer getBank() {
		return bank;
	}

	public void setBank(Issuer bank) {
		this.bank = bank;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append(getId()).append(",");
		if(bank != null){
			sb.append(bank.getCode());
		}
		
		return sb.toString();
	}
}
