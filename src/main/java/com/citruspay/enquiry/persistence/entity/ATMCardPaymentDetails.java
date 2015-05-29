package com.citruspay.enquiry.persistence.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "ATM_CARD")
public class ATMCardPaymentDetails extends PaymentDetail {

	private static final long serialVersionUID = 1L;
	
	private String cardType;
	
	
	public ATMCardPaymentDetails() {
	}
	
	public ATMCardPaymentDetails(String cardType) {
		super();
		this.cardType = cardType;
	}

	/**
	 * @return the cardType
	 */
	public String getCardType() {
		return cardType;
	}

	/**
	 * @param cardType the cardType to set
	 */
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getId()).append(",");
		sb.append(cardType);
		return sb.toString();
	}
}
