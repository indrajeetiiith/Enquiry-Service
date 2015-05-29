/*
 * Copyright (c) 2013 CitrusPay. All Rights Reserved.
 *
 * This software is the proprietary information of CitrusPay.
 * Use is subject to license terms.
 */

package com.citruspay.enquiry.persistence.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "PREPAID_CARD")
public class PrepaidPaymentDetail extends PaymentDetail {

	private static final long serialVersionUID = 1L;

	private String cardName;
	
	private String cardNumber;
	
	private String cardType;
	
	private String expiryMonth;
	
	private String expiryYear;
	
	public PrepaidPaymentDetail() {}
		
	public PrepaidPaymentDetail(String cardName, String cardNumber,
			String cardType, String expiryMonth, String expiryYear) {
		super();
		this.cardName = cardName;
		this.cardNumber = cardNumber;
		this.cardType = cardType;
		this.expiryMonth = expiryMonth;
		this.expiryYear = expiryYear;
	}

	public String getCardName() {
		return cardName;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public String getCardType() {
		return cardType;
	}

	public String getExpiryMonth() {
		return expiryMonth;
	}

	public String getExpiryYear() {
		return expiryYear;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public void setExpiryMonth(String expiryMonth) {
		this.expiryMonth = expiryMonth;
	}

	public void setExpiryYear(String expiryYear) {
		this.expiryYear = expiryYear;
	}

	@Override
	public String toString() {
		return "PrepaidPaymentDetail [cardName=" + cardName + ", cardType="
				+ cardType + ", expiryMonth=" + expiryMonth + ", expiryYear="
				+ expiryYear + "]";
	}


}
