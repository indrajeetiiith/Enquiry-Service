/*
 * Copyright (c) 2012 CitrusPay. All Rights Reserved.
 *
 * This software is the proprietary information of CitrusPay.
 * Use is subject to license terms.
 */

package com.citruspay.enquiry.persistence.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import com.citruspay.BaseDomain;
import com.citruspay.enquiry.persistence.entity.PricingOfferType;
import com.citruspay.CommonUtil;

@Entity
@Table(name = "dp_pricing_rule")
public class PricingRule extends BaseDomain {

	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;


	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Temporal(TemporalType.TIMESTAMP)
	@Version
	private Date lastModified;

	private Integer isGlobal;

	private int isActive;

	private int isDeleted;

	private int thresoldPercentage;

	private int thresoldCount;

	@Transient
	private Integer merchantAddRule;


	private PricingOfferType offerType;

	@NotNull
	@DecimalMin(value = "0.0")
	private BigDecimal offerValue;

	@NotNull
	@DecimalMin(value = "0.0")
	private BigDecimal offerPercentage;

	@NotNull
	private String msgConsumer;

	private BigDecimal minAmount;

	private BigDecimal maxAmount;

	private int isCoupon;
	
	@Column(name = "hard_restrict")
	private int allowOnlyMerchantCoupon;
	
	public int getAllowOnlyMerchantCoupon() {
		return allowOnlyMerchantCoupon;
	}

	public void setAllowOnlyMerchantCoupon(
			int allowOnlyMerchantCoupon) {
		this.allowOnlyMerchantCoupon = allowOnlyMerchantCoupon;
	}

	private int isCitrusSponsored;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public Integer getIsGlobal() {
		return isGlobal;
	}

	public void setIsGlobal(Integer isGlobal) {
		this.isGlobal = isGlobal;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public int getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(int isDeleted) {
		this.isDeleted = isDeleted;
	}

	public int getThresoldPercentage() {
		return thresoldPercentage;
	}

	public void setThresoldPercentage(int thresoldPercentage) {
		this.thresoldPercentage = thresoldPercentage;
	}

	public int getThresoldCount() {
		return thresoldCount;
	}

	public void setThresoldCount(int thresoldCount) {
		this.thresoldCount = thresoldCount;
	}


	public Integer getMerchantAddRule() {
		return merchantAddRule;
	}

	public void setMerchantAddRule(Integer merchantAddRule) {
		this.merchantAddRule = merchantAddRule;
	}

	public PricingOfferType getOfferType() {
		return offerType;
	}

	public void setOfferType(PricingOfferType offerType) {
		this.offerType = offerType;
	}

	public BigDecimal getOfferValue() {
		return offerValue;
	}

	public void setOfferValue(BigDecimal offerValue) {
		if (CommonUtil.isNotNull(offerValue)) {
			this.offerValue = offerValue.setScale(CommonUtil.EIGHT_DECIMAL_PLACES,
					BigDecimal.ROUND_HALF_UP);
		} else {
			this.offerValue = offerValue;
		}
	}

	public BigDecimal getOfferPercentage() {
		return offerPercentage;
	}

	public void setOfferPercentage(BigDecimal offerPercentage) {
		if (CommonUtil.isNotNull(offerPercentage)) {
			this.offerPercentage = offerPercentage.setScale(
					CommonUtil.EIGHT_DECIMAL_PLACES, BigDecimal.ROUND_HALF_UP);
		} else {
			this.offerPercentage = offerPercentage;
		}
	}

	public String getMsgConsumer() {
		return msgConsumer;
	}

	public void setMsgConsumer(String msgConsumer) {
		this.msgConsumer = msgConsumer;
	}

	public BigDecimal getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(BigDecimal minAmount) {
		if (CommonUtil.isNotNull(minAmount)) {
			this.minAmount = minAmount.setScale(CommonUtil.DECIMAL_PLACES,
					BigDecimal.ROUND_HALF_UP);
		} else {
			this.minAmount = minAmount;
		}
	}

	public BigDecimal getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(BigDecimal maxAmount) {
		if (CommonUtil.isNotNull(maxAmount)) {
			this.maxAmount = maxAmount.setScale(CommonUtil.DECIMAL_PLACES,
					BigDecimal.ROUND_HALF_UP);
		} else {
			this.maxAmount = maxAmount;
		}
	}

	public int getIsCitrusSponsored() {
		return isCitrusSponsored;
	}

	public void setIsCitrusSponsored(int isCitrusSponsored) {
		this.isCitrusSponsored = isCitrusSponsored;
	}

	public int getIsCoupon() {
		return this.isCoupon;
	}
	
	public void setIsCoupon(int isCoupon) {
		this.isCoupon = isCoupon;
	}
	
	@Override
	public String toString() {
		return "PricingRule [id=" + id + ", name=" + name + ", lastModified=" + lastModified
				+ ", isGlobal=" + isGlobal + ", isActive=" + isActive
				+ ", isDeleted=" + isDeleted + ", thresoldPercentage="
				+ thresoldPercentage + ", thresoldCount=" + thresoldCount
				+ ", merchantAddRule=" + merchantAddRule 
				+ ",offerType=" + offerType + ", msgConsumer=" + msgConsumer
				+ "]";
	}

}