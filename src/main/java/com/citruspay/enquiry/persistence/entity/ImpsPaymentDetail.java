/*
 * Copyright (c) 2012 CitrusPay. All Rights Reserved.
 *
 * This software is the proprietary information of CitrusPay.
 * Use is subject to license terms.
 */
package com.citruspay.enquiry.persistence.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


@SuppressWarnings("serial")
@Entity
@DiscriminatorValue(value = "IMPS")
public class ImpsPaymentDetail extends PaymentDetail {

	// card number refers to MMID
	private String cardNumber;

	private String mobileNumber;

	public ImpsPaymentDetail() {
	}

	public ImpsPaymentDetail(String cardNumber, String mobileNumber) {
		super();
		this.setCardNumber(cardNumber);
		this.mobileNumber = mobileNumber;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getId()).append(",");		
		sb.append(mobileNumber).append(",");
		return sb.toString();
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

}
