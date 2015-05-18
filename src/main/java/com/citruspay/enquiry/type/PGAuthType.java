/*
 * Copyright (c) 2012 CitrusPay. All Rights Reserved.
 *
 * This software is the proprietary information of CitrusPay.
 * Use is subject to license terms.
 */
package com.citruspay.enquiry.type;

public enum PGAuthType {


	KEY("key"), PSWD("pswd"), NONE("none"), SELLERID("sellerId"), MID("mId"), WALLETID("walletId"), CERTIFICATE("certificate");


	private final String desc;

	private PGAuthType(String desc) {
		this.desc = desc;

	}

	public String getDesc() {
		return desc;
	}

}
