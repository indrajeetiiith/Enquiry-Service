/*
 * Copyright (c) 2012 CitrusPay. All Rights Reserved.
 *
 * This software is the proprietary information of CitrusPay.
 * Use is subject to license terms.
 */
package com.citruspay.enquiry.persistence.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import com.citruspay.enquiry.persistence.entity.Merchant;

@Entity
@Table(name = "mer_pg_setting")
@Audited
public class MerchantGatewaySetting implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8333800003308532942L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name="merchant_id")
	private Merchant merchant;

	@ManyToOne
	@JoinColumn(name="gateway_id")
	private PaymentGateway paymentGateway;

	private String merchantUsrid;

	private String merchantPswd;
	
	private String merAmexSellerId;
	
	private boolean acceptInternational;

	private int enabled;
	
	private String mpiId;
	
	private String partnerId;
	
	//Additional Column for PayZen PG
	private String payzenSiteId;
	private String payzenCertiNumber;
	private String payzenCtxMode;
	
	//Additional Column for IDBI PG
	private String pgInstanceId;
	private String mpiHashKey;
	private String paymentHashKey;
	
	
	/*TODO indra commenting for the time being@OneToOne
	@JoinColumn(name="amex_cat_code_id")
	@Cascade(value=CascadeType.SAVE_UPDATE)
	@Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
	private MerchantPGCategoryCode merchantPGCategoryCode;
	*/
	public String getMpiId() {
		return mpiId;
	}

	public void setMpiId(String mpiId) {
		this.mpiId = mpiId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMerchantUsrid() {
		return merchantUsrid;
	}

	public void setMerchantUsrid(String merchantUsrid) {
		this.merchantUsrid = merchantUsrid;
	}

	public String getMerchantPswd() {
		return merchantPswd;
	}

	public void setMerchantPswd(String merchantPswd) {
		this.merchantPswd = merchantPswd;
	}

	public int getEnabled() {
		return enabled;
	}

	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	public PaymentGateway getPaymentGateway() {
		return paymentGateway;
	}

	/**
	 * @return the acceptInternational
	 */
	public boolean getAcceptInternational() {
		return acceptInternational;
	}

	/**
	 * @param acceptInternational the acceptInternational to set
	 */
	public void setAcceptInternational(boolean acceptInternational) {
		this.acceptInternational = acceptInternational;
	}

	public void setPaymentGateway(PaymentGateway paymentGateway) {
		this.paymentGateway = paymentGateway;
	}

	public String getMerAmexSellerId() {
		return merAmexSellerId;
	}

	public void setMerAmexSellerId(String merAmexSellerId) {
		this.merAmexSellerId = merAmexSellerId;
	}

/*TODO commenting for the time being	public MerchantPGCategoryCode getMerchantPGCategoryCode() {
		return merchantPGCategoryCode;
	}

	public void setMerchantPGCategoryCode(MerchantPGCategoryCode merchantPGCategoryCode) {
		this.merchantPGCategoryCode = merchantPGCategoryCode;
	}
*/
	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}
	
	public String getPayzenSiteId() {
		return payzenSiteId;
	}

	public void setPayzenSiteId(String payzenSiteId) {
		this.payzenSiteId = payzenSiteId;
	}

	public String getPayzenCertiNumber() {
		return payzenCertiNumber;
	}

	public void setPayzenCertiNumber(String payzenCertiNumber) {
		this.payzenCertiNumber = payzenCertiNumber;
	}

	public String getPayzenCtxMode() {
		return payzenCtxMode;
	}

	public void setPayzenCtxMode(String payzenCtxMode) {
		this.payzenCtxMode = payzenCtxMode;
	}

	public String getPgInstanceId() {
		return pgInstanceId;
	}

	public void setPgInstanceId(String pgInstanceId) {
		this.pgInstanceId = pgInstanceId;
	}

	public String getMpiHashKey() {
		return mpiHashKey;
	}

	public void setMpiHashKey(String mpiHashKey) {
		this.mpiHashKey = mpiHashKey;
	}

	public String getPaymentHashKey() {
		return paymentHashKey;
	}

	public void setPaymentHashKey(String paymentHashKey) {
		this.paymentHashKey = paymentHashKey;
	}
}
