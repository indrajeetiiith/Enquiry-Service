/*
 * Copyright (c) 2012 CitrusPay. All Rights Reserved.
 *
 * This software is the proprietary information of CitrusPay.
 * Use is subject to license terms.
 */
package com.citruspay.enquiry.persistence.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.validation.Valid;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.citruspay.enquiry.persistence.entity.Address;
import com.citruspay.enquiry.persistence.entity.PhoneNumber;


@Entity
@Audited
@Table(name = "CON_CONSUMER_DETAIL")
public class ConsumerDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2800067397501503076L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String firstName;

	private String lastName;

	private Date dateOfBirth;

	private String sex;

	private String panCard;

	private boolean panCardValidated;

	@JoinColumn(name = "aadhar_card")
	private String aadharCard;

	private boolean aadharCardValidated;

	private boolean emailValidated;

	private boolean mobileValidated;

	@Embedded
	private Address contactAddress;

	private String email;

	@ElementCollection//(fetch = FetchType.EAGER)
	@Valid
	@CollectionTable(name = "CON_CONSUMER_DETAIL_PHONE_NUMBER")
	@NotAudited
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<PhoneNumber> phoneNumber;
	 


	public ConsumerDetail() {
	}

	public ConsumerDetail(String firstName, String lastName,
			Address contactAddress, String email, List<PhoneNumber> phoneNumber) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.contactAddress = contactAddress;
		this.email = email;
		this.phoneNumber = phoneNumber;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Address getContactAddress() {
		return contactAddress;
	}

	public void setContactAddress(Address contactAddress) {
		this.contactAddress = contactAddress;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<PhoneNumber> getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(List<PhoneNumber> phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getPanCard() {
		return panCard;
	}

	public void setPanCard(String panCard) {
		this.panCard = panCard;
	}

	public boolean isPanCardValidated() {
		return panCardValidated;
	}

	public void setPanCardValidated(boolean panCardValidated) {
		this.panCardValidated = panCardValidated;
	}

	public String getAadharCard() {
		return aadharCard;
	}

	public void setAadharCard(String aadharCard) {
		this.aadharCard = aadharCard;
	}

	public boolean isAadharCardValidated() {
		return aadharCardValidated;
	}

	public void setAadharCardValidated(boolean aadharCardValidated) {
		this.aadharCardValidated = aadharCardValidated;
	}

	public boolean isEmailValidated() {
		return emailValidated;
	}

	public void setEmailValidated(boolean emailValidated) {
		this.emailValidated = emailValidated;
	}

	public boolean isMobileValidated() {
		return mobileValidated;
	}

	public void setMobileValidated(boolean mobileValidated) {
		this.mobileValidated = mobileValidated;
	}

	@Override
	public String toString() {
		return "ConsumerDetail [id=" + id + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", dateOfBirth=" + dateOfBirth
				+ ", sex=" + sex + ", panCard=" + panCard
				+ ", panCardValidated=" + panCardValidated + ", aadharCard="
				+ aadharCard + ", aadharCardValidated=" + aadharCardValidated
				+ ", emailValidated=" + emailValidated + ", mobileValidated="
				+ mobileValidated + ", contactAddress=" + contactAddress
				+ ", email=" + email + ", phoneNumber=" + phoneNumber + "]";
	}
}
