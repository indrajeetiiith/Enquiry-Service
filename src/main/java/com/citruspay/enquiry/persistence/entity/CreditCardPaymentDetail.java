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
@DiscriminatorValue(value = "CREDIT_CARD")
public class CreditCardPaymentDetail  extends PaymentDetail {

	//private String bankName;
	
	private String cardNumber;
	
	private String cardName;
	
	private String cardType;
	
	private String expiryMonth;
	
	private String expiryYear;
	
	public CreditCardPaymentDetail() {
	}
	
	
	public CreditCardPaymentDetail(String cardNumber, String cardName,
			String cardType, String expiryMonth, String expiryYear) {
		super();
		this.cardNumber = cardNumber;
		this.cardName = cardName;
		this.cardType = cardType;
		this.expiryMonth = expiryMonth;
		this.expiryYear = expiryYear;
	}


	/*public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}*/

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getExpiryMonth() {
		return expiryMonth;
	}

	public void setExpiryMonth(String expiryMonth) {
		this.expiryMonth = expiryMonth;
	}

	public String getExpiryYear() {
		return expiryYear;
	}

	public void setExpiryYear(String expiryYear) {
		this.expiryYear = expiryYear;
	}
	
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append(getId()).append(",");
		
		sb.append(cardType).append(",");
		sb.append(cardName).append(",");
		sb.append(expiryMonth).append(",");
		sb.append(expiryYear);
		return sb.toString();
	}

}
