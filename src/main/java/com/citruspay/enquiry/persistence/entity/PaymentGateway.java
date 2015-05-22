/*
 * Copyright (c) 2012 CitrusPay. All Rights Reserved.
 *
 * This software is the proprietary information of CitrusPay.
 * Use is subject to license terms.
 */
package com.citruspay.enquiry.persistence.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import com.citruspay.enquiry.type.GatewayType;
import com.citruspay.enquiry.type.PGAuthType;

@Entity
@Table(name = "pg_payment_gateway")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class PaymentGateway implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4395102519476992450L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String code;

	private String name;

	@ManyToOne
	@JoinColumn(name = "pg_gateway_id")
	private PaymentGateway parentGateway;

	private String gatewayIssuerCode;

	private int enabled;

	@Enumerated(EnumType.STRING)
	private PaymentMode paymentMode;

	@Enumerated(EnumType.STRING)
	private PGAuthType authType;

	private GatewayType gatewayType;
	
	private String onusCode;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getEnabled() {
		return enabled;
	}

	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}

	public PaymentGateway getParentGateway() {
		return parentGateway;
	}

	public void setParentGateway(PaymentGateway parentGateway) {
		this.parentGateway = parentGateway;
	}

	public String getGatewayIssuerCode() {
		return gatewayIssuerCode;
	}

	public void setGatewayIssuerCode(String gatewayIssuerCode) {
		this.gatewayIssuerCode = gatewayIssuerCode;
	}

	public PaymentMode getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(PaymentMode paymentMode) {
		this.paymentMode = paymentMode;
	}

	public PGAuthType getAuthType() {
		return authType;
	}

	public void setAuthType(PGAuthType authType) {
		this.authType = authType;
	}

	public GatewayType getGatewayType() {
		return gatewayType;
	}

	public void setGatewayType(GatewayType gatewayType) {
		this.gatewayType = gatewayType;
	}
	
	public String getOnusCode() {
		return onusCode;
	}

	public void setOnusCode(String onusCode) {
		this.onusCode = onusCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PaymentGateway other = (PaymentGateway) obj;
		if (authType != other.authType)
			return false;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (enabled != other.enabled)
			return false;
		if (gatewayIssuerCode == null) {
			if (other.gatewayIssuerCode != null)
				return false;
		} else if (!gatewayIssuerCode.equals(other.gatewayIssuerCode))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parentGateway == null) {
			if (other.parentGateway != null)
				return false;
		} else if (!parentGateway.equals(other.parentGateway))
			return false;
		if (paymentMode != other.paymentMode)
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		int hash = 3;
		hash = 7 * hash + this.name.hashCode();
		return hash;
	}

}
