/*
 * Copyright (c) 2012 CitrusPay. All Rights Reserved.
 *
 * This software is the proprietary information of CitrusPay.
 * Use is subject to license terms.
 */
package com.citruspay.enquiry.persistence.entity;

public enum TransactionType {
	PAYMENT("Payment"), SUBSCRIPTION("Subscription"), EMI("EMI"), DONATIONS(
			"Donations");

	private String desc;

	TransactionType(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}

}
