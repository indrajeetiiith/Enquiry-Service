/*
 * Copyright (c) 2012 CitrusPay. All Rights Reserved.
 *
 * This software is the proprietary information of CitrusPay.
 * Use is subject to license terms.
 */
package com.citruspay.enquiry.persistence.entity;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import com.citruspay.enquiry.type.PhoneType;

@Embeddable
public class PhoneNumber {

	@NotNull
	@Enumerated(EnumType.STRING)
	private PhoneType type;

	@NotNull
	private String phoneNumber;

	public PhoneNumber() {
		
	}
	public PhoneNumber(PhoneType type, String phoneNumber) {
		this.type = type;
		this.phoneNumber = phoneNumber;
	}
	
	public PhoneType getType() {
		return type;
	}

	public void setType(PhoneType type) {
		this.type = type;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

}
