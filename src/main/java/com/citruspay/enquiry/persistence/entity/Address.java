/*
 * Copyright (c) 2012 CitrusPay. All Rights Reserved.
 *
 * This software is the proprietary information of CitrusPay.
 * Use is subject to license terms.
 */
package com.citruspay.enquiry.persistence.entity;

import javax.persistence.Embeddable;

@Embeddable
public class Address {

	private String addressStreet1;

	private String addressStreet2;

	private String addressCity;

	private String addressState;

	private String addressCountry;

	private String addressZip;

	public Address() {
		
	}
			
	public Address(String addressStreet1, String addressCity, String addressState, String addressCountry, String addressZip) {
		this.addressStreet1 = addressStreet1;
		this.addressCity = addressCity;
		this.addressState = addressState;
		this.addressCountry = addressCountry;
		this.addressZip = addressZip;
	}
	
	public String getAddressStreet1() {
		return addressStreet1;
	}

	public void setAddressStreet1(String addressStreet1) {
		this.addressStreet1 = addressStreet1;
	}

	public String getAddressStreet2() {
		return addressStreet2;
	}

	public void setAddressStreet2(String addressStreet2) {
		this.addressStreet2 = addressStreet2;
	}

	public String getAddressCity() {
		return addressCity;
	}

	public void setAddressCity(String addressCity) {
		this.addressCity = addressCity;
	}

	public String getAddressState() {
		return addressState;
	}

	public void setAddressState(String addressState) {
		this.addressState = addressState;
	}

	public String getAddressCountry() {
		return addressCountry;
	}

	public void setAddressCountry(String addressCountry) {
		this.addressCountry = addressCountry;
	}

	public String getAddressZip() {
		return addressZip;
	}

	public void setAddressZip(String addressZip) {
		this.addressZip = addressZip;
	}
	
	@Override
	public String toString() {
		return addressStreet1+", "+addressStreet2+", City: "+addressCity+", State: "+addressState+", Country: "+addressCountry+", Zip Code: "+addressZip;
	}

}