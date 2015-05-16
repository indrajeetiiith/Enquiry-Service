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

import org.codehaus.jackson.annotate.JsonIgnore;

import com.citruspay.enquiry.persistence.util.KeyType;

@Entity
@Table(name = "MER_MERCHANT_SEC_KEY")
public class MerchantKey implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Enumerated(EnumType.STRING)
	private KeyType keyType;

<<<<<<< HEAD
=======
	private String keyString;

	private Integer useKey;
>>>>>>> 63cbfe1fb644b0ed860a22788730461b6a199082

	private String secretId;

	@ManyToOne
	@JoinColumn(name = "MER_MERCHANT_ID", nullable = true)
	private Merchant merchant;
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public KeyType getKeyType() {
		return keyType;
	}

	public void setKeyType(KeyType keyType) {
		this.keyType = keyType;
	}

<<<<<<< HEAD
=======
	public String getKeyString() {
		return keyString;
	}

>>>>>>> 63cbfe1fb644b0ed860a22788730461b6a199082
	public String getSecretId() {
		return secretId;
	}

	public void setSecretId(String secretId) {
		this.secretId = secretId;
	}

<<<<<<< HEAD
=======
	public void setKeyString(String keyString) {
		this.keyString = keyString;
	}

	public Boolean getUseKey() {
		return useKey != null && useKey == 1;
	}

	public void setUseKey(Boolean checked) {
		if (checked) {
			this.useKey = 1;
		} else {
			this.useKey = 0;
		}
	}
>>>>>>> 63cbfe1fb644b0ed860a22788730461b6a199082

	@JsonIgnore
	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

}
