/*
 * Copyright (c) 2012 CitrusPay. All Rights Reserved.
 *
 * This software is the proprietary information of CitrusPay.
 * Use is subject to license terms.
 */
package com.citruspay.enquiry.persistence.entity;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import com.citruspay.enquiry.persistence.entity.Issuer;
import com.citruspay.enquiry.persistence.entity.Merchant;
import com.citruspay.enquiry.persistence.entity.PaymentGateway;
import com.citruspay.enquiry.persistence.entity.PaymentMode;
import com.citruspay.CommonUtil;

@Entity
@Table(name = "CON_CONSUMER_PAYMENT_DETAIL")
@Audited
public class ConsumerPaymentDetail implements Comparator<ConsumerPaymentDetail>{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Enumerated(EnumType.STRING)
	private PaymentMode paymentMode;

	@OneToOne
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private Issuer bank;

	private String cardNumber;

	private String cardName;

	private String cardType;

	private String expiryMonth;

	private String expiryYear;

	private String friendlyCardName;
	
	private String mobileNumber;

	@Transient
	private String mmid;
	
	@Transient
	private String otp;
	
	@Transient
	private String cvvNumber;

	@Transient
	private String displayCardNumber;

	@Transient
	private String maskedCardNumber;
/*TODO
	@ManyToOne
	@JoinColumn(name = "CON_CONSUMER_ID", nullable = true)
	private Consumer consumer;
*/
	@Transient
	private Boolean disabled;

	@Transient
	private String conPaymentOpt;

	@Transient
	private String ccCardType;

	@Transient
	private String dcCardType;

	@Transient
	private Boolean expiredCardDetail;

	@Transient
	private Boolean disableForRetry;

	@Transient
	private List<PaymentGateway> paymentGateways;

	@Transient
	private Boolean isCountryInterOnAndNonIndia;

	@Transient
	private Merchant merchant;

	@Transient
	private BigDecimal amount;

	@Transient
	private BigDecimal orignalAmount;

	@Transient
	private BigDecimal adjustment;

	@Transient
	private String ruleType;

	@Transient
	private String msgToConsumer;

	@Transient
	private String currency;

	@Transient
	private String paymentToken;

	@Transient
	private BigDecimal updatedAmount;

	@Transient
	private String cardIssuerBank;
	
	@Transient
	private String atm;

	@Transient
	private Boolean isEmiEnable;
	
	@Transient
	private String emiBankCode;

	@Transient
	private String emiTenureCode;
	
	@Transient
	private boolean invalidCoupon;
	
	@Transient
	private boolean hardRestricted;
	
	@Transient
	private String prepaidCardName;
	
	@Transient
	private String isSupportedByMerchant = CommonUtil.CARDS_NOT_SUPPORTED_BY_MERCHANT;

	public Boolean getIsEmiEnable() {
		return isEmiEnable;
	}

	public void setIsEmiEnable(Boolean isEmiEnable) {
		this.isEmiEnable = isEmiEnable;
	}

	public ConsumerPaymentDetail(Issuer bank) {
		super();
		this.bank = bank;
	}

	public ConsumerPaymentDetail(String cardNumber, String cardName,
			String cardType, String expiryMonth, String expiryYear) {
		super();
		this.cardNumber = cardNumber;
		this.cardName = cardName;
		this.cardType = cardType;
		this.expiryMonth = expiryMonth;
		this.expiryYear = expiryYear;
	}

	public ConsumerPaymentDetail() {
		// TODO Auto-generated constructor stub
	}

	public Issuer getBank() {
		return bank;
	}

	public void setBank(Issuer bank) {
		this.bank = bank;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public PaymentMode getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(PaymentMode paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public String getExpiryMonth() {
		return expiryMonth;
	}

	public void setExpiryMonth(String expiryMonth) {
		this.expiryMonth = expiryMonth;
	}

	public String getExpiryYear() {
		return expiryYear;
	}

	public void setExpiryYear(String expiryYear) {
		this.expiryYear = expiryYear;
	}

	public String getCvvNumber() {
		return cvvNumber;
	}

	public void setCvvNumber(String cvvNumber) {
		this.cvvNumber = cvvNumber;
	}

	public String getDisplayCardNumber() {
		return displayCardNumber;
	}

	public void setDisplayCardNumber(String displayCardNumber) {
		this.displayCardNumber = displayCardNumber;
	}

	public String getMaskedCardNumber() {
		return maskedCardNumber;
	}

	public void setMaskedCardNumber(String maskedCardNumber) {
		this.maskedCardNumber = maskedCardNumber;
	}

/*TODO	@JsonIgnore
	public Consumer getConsumer() {
		return consumer;
	}

	public void setConsumer(Consumer consumer) {
		this.consumer = consumer;
	}
*/
	public Boolean getDisabled() {
		return disabled;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	public String getConPaymentOpt() {
		return conPaymentOpt;
	}

	public void setConPaymentOpt(String conPaymentOpt) {
		this.conPaymentOpt = conPaymentOpt;
	}

	public String getCcCardType() {
		return ccCardType;
	}

	public void setCcCardType(String ccCardType) {
		this.ccCardType = ccCardType;
	}

	public String getDcCardType() {
		return dcCardType;
	}

	public void setDcCardType(String dcCardType) {
		this.dcCardType = dcCardType;
	}

	public Boolean getExpiredCardDetail() {
		return expiredCardDetail;
	}

	public void setExpiredCardDetail(Boolean expiredCardDetail) {
		this.expiredCardDetail = expiredCardDetail;
	}

	public Boolean getDisableForRetry() {
		return disableForRetry;
	}

	public void setDisableForRetry(Boolean disableForRetry) {
		this.disableForRetry = disableForRetry;
	}

	public List<PaymentGateway> getPaymentGateways() {
		return paymentGateways;
	}

	public void setPaymentGateways(List<PaymentGateway> paymentGateways) {
		this.paymentGateways = paymentGateways;
	}

	public Boolean getIsCountryInterOnAndNonIndia() {
		return isCountryInterOnAndNonIndia;
	}

	public void setIsCountryInterOnAndNonIndia(
			Boolean isCountryInterOnAndNonIndia) {
		this.isCountryInterOnAndNonIndia = isCountryInterOnAndNonIndia;
	}

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	public String getFriendlyCardName() {
		return this.friendlyCardName;
	}

	public void setFriendlyCardName(String friendlyCardName) {
		this.friendlyCardName = friendlyCardName;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getOrignalAmount() {
		return orignalAmount;
	}

	public void setOrignalAmount(BigDecimal orignalAmount) {
		this.orignalAmount = orignalAmount;
	}

	public BigDecimal getAdjustment() {
		return adjustment;
	}

	public void setAdjustment(BigDecimal adjustment) {
		this.adjustment = adjustment;
	}

	public String getMsgToConsumer() {
		return msgToConsumer;
	}

	public void setMsgToConsumer(String msgToConsumer) {
		this.msgToConsumer = msgToConsumer;
	}

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getUpdatedAmount() {
		return updatedAmount;
	}

	public void setUpdatedAmount(BigDecimal updatedAmount) {
		this.updatedAmount = updatedAmount;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getMmid() {
		return mmid;
	}

	public void setMmid(String mmid) {
		this.mmid = mmid;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}
	
	public String getCardIssuerBank() {
		return cardIssuerBank;
	}

	public void setCardIssuerBank(String cardIssuerBank) {
		this.cardIssuerBank = cardIssuerBank;
	}
	
	public String getEmiBankCode() {
		return emiBankCode;
	}

	public void setEmiBankCode(String emiBankCode) {
		this.emiBankCode = emiBankCode;
	}

	public String getEmiTenureCode() {
		return emiTenureCode;
	}

	public void setEmiTenureCode(String emiTenureCode) {
		this.emiTenureCode = emiTenureCode;
	}

	public String getAtm() {
		return atm;
	}

	public void setAtm(String atm) {
		this.atm = atm;
	}
	
	public boolean isInvalidCoupon() {
		return invalidCoupon;
	}

	public void setInvalidCoupon(boolean invalidCoupon) {
		this.invalidCoupon = invalidCoupon;
	}

	public boolean isHardRestricted() {
		return hardRestricted;
	}

	public void setHardRestricted(boolean hardRestricted) {
		this.hardRestricted = hardRestricted;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("paymentMode= ").append(paymentMode).append(", bank= ")
				.append(bank != null ? bank.getName() : "")
				.append(", issureCode= ")
				.append(bank != null ? bank.getCode() : "")
				.append(", cardName= ").append(cardName).append(", cardType= ")
				.append(cardType).append(", expiryMonth= ").append(expiryMonth)
				.append(", expiryYear= ").append(expiryYear)
				.append(", friendlyCardName= ").append(friendlyCardName)
				.append(", maskedCardNumber= ").append(maskedCardNumber);

		return builder.toString();
	}

	public String getPaymentToken() {
		return paymentToken;
	}

	public void setPaymentToken(String paymentToken) {
		this.paymentToken = paymentToken;
	}

	public String getPrepaidCardName() {
		return prepaidCardName;
	}

	public void setPrepaidCardName(String prepaidCardName) {
		this.prepaidCardName = prepaidCardName;
	}
	
	public String getIsSupportedByMerchant() {
		return isSupportedByMerchant;
	}

	public void setIsSupportedByMerchant(String isSupportedByMerchant) {
		this.isSupportedByMerchant = isSupportedByMerchant;
	}

	@Override
	public int compare(ConsumerPaymentDetail o1, ConsumerPaymentDetail o2) {

		if(o1.getPaymentMode() == PaymentMode.PREPAID_CARD && o2.getPaymentMode() == PaymentMode.PREPAID_CARD) {
			return 0;
		}
		
		if(o1.getPaymentMode()==PaymentMode.PREPAID_CARD) {
			return -1;
		}
		
		if(o2.getPaymentMode()==PaymentMode.PREPAID_CARD) {
			return 1;
		}
				
		if(!CommonUtil.CARDS_SUPPORTED_BY_MERCHANT.equalsIgnoreCase(o1.getIsSupportedByMerchant()) && !CommonUtil.CARDS_SUPPORTED_BY_MERCHANT.equalsIgnoreCase(o2.getIsSupportedByMerchant())) {
			return 0;
		}
		
		if(!CommonUtil.CARDS_SUPPORTED_BY_MERCHANT.equalsIgnoreCase(o1.getIsSupportedByMerchant())) {
			return 1;
		}
		
		if(!CommonUtil.CARDS_SUPPORTED_BY_MERCHANT.equalsIgnoreCase(o2.getIsSupportedByMerchant())) {
			return -1;
		}

		if(o1.getPaymentMode() == PaymentMode.CREDIT_CARD && o2.getPaymentMode() == PaymentMode.CREDIT_CARD) {
			return 0;
		}
		
		if(o1.getPaymentMode()==PaymentMode.CREDIT_CARD) {
			return -1;
		}
		
		if(o2.getPaymentMode()==PaymentMode.CREDIT_CARD) {
			return 1;
		}
		
		return 0;
	}

}
