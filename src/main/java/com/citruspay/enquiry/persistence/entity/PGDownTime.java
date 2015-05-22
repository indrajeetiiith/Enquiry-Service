/*
 * Copyright (c) 2012 CitrusPay. All Rights Reserved.
 *
 * This software is the proprietary information of CitrusPay.
 * Use is subject to license terms.
 */
package com.citruspay.enquiry.persistence.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.citruspay.enquiry.type.PaymentType;

@SuppressWarnings("serial")
@Entity
@Table(name = "pg_downtime")
public class PGDownTime implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private PaymentType paymentType;

	@ManyToOne
	@JoinColumn(name = "gateway_id")
	private PaymentGateway paymentGateway;

	@ManyToOne
	@JoinColumn(name = "issuer_id")
	private Issuer issuer;

	private Date startTime;

	private Date endTime;

	private Integer merchantAffectedCount;

	private Date created;

	private Date lastUpdated;

	private String status;

	private Boolean unavailabilityNotified;

	private Boolean resumeNotified;

	@Transient
	private String synchronizedtoken;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public PaymentGateway getPaymentGateway() {
		return paymentGateway;
	}

	public void setPaymentGateway(PaymentGateway paymentGateway) {
		this.paymentGateway = paymentGateway;
	}

	public Issuer getIssuer() {
		return issuer;
	}

	public void setIssuer(Issuer issuer) {
		this.issuer = issuer;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Integer getMerchantAffectedCount() {
		return merchantAffectedCount;
	}

	public void setMerchantAffectedCount(Integer merchantAffectedCount) {
		this.merchantAffectedCount = merchantAffectedCount;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public Boolean getUnavailabilityNotified() {
		return unavailabilityNotified;
	}

	public void setUnavailabilityNotified(Boolean unavailabilityNotified) {
		this.unavailabilityNotified = unavailabilityNotified;
	}

	public Boolean getResumedNotified() {
		return resumeNotified;
	}

	public void setResumedNotified(Boolean resumedNotified) {
		this.resumeNotified = resumedNotified;
	}

	public Boolean getResumeNotified() {
		return resumeNotified;
	}

	public void setResumeNotified(Boolean resumeNotified) {
		this.resumeNotified = resumeNotified;
	}

	public String getSynchronizedtoken() {
		return synchronizedtoken;
	}

	public void setSynchronizedtoken(String synchronizedtoken) {
		this.synchronizedtoken = synchronizedtoken;
	}

	@Override
	public String toString() {
		return "PGDownTime [id=" + id + ", paymentGateway=" + paymentGateway
				+ ", issuer=" + issuer + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", merchantAffectedCount="
				+ merchantAffectedCount + ", status=" + status
				+ ", unavailabilityNotified=" + unavailabilityNotified
				+ ", resumedNotified=" + resumeNotified + "]";
	}

}
