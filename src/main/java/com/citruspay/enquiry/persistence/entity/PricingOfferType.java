/*
 * Copyright (c) 2012 CitrusPay. All Rights Reserved.
 *
 * This software is the proprietary information of CitrusPay.
 * Use is subject to license terms.
 */
package com.citruspay.enquiry.persistence.entity;

public enum PricingOfferType {
	DISCOUNT("Discount"), SURCHARGE("Surcharge"), MERCHANT_MANAGED("MerchantCoupon");

	private String displayLabel;

	private PricingOfferType(String displayLabel) {
		this.displayLabel = displayLabel;
	}

	public String getDisplayLabel() {
		return this.displayLabel;
	}

}
