package com.citruspay.enquiry.persistence.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@SuppressWarnings("serial")
@Entity
@DiscriminatorValue(value = "EMI")
public class EmiPaymentDetail extends PaymentDetail {

	private String cardNumber;
	
	private String cardName;
	
	private String cardType;
	
	private String expiryMonth;
	
	private String expiryYear;
	
	public EmiPaymentDetail() {
	}
	
	public EmiPaymentDetail(String cardNumber, String cardName,
			String cardType, String expiryMonth, String expiryYear) {
		super();
		this.cardNumber = cardNumber;
		this.cardName = cardName;
		this.cardType = cardType;
		this.expiryMonth = expiryMonth;
		this.expiryYear = expiryYear;
	}

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

}
