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

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;


@Entity
<<<<<<< HEAD
=======
<<<<<<< HEAD
=======

>>>>>>> 63cbfe1fb644b0ed860a22788730461b6a199082
>>>>>>> b856f04f9362059bd510fec0f3a9b4a984435d96
@Table(name = "MER_MERCHANT")
public class Merchant implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String merchantOpusId;

	@NotNull
	@Size(min = 1, message = "error.merchant.name.notempty")
	private String name;

	private String siteUrl;

	private String vanityUrlPart;

	private String merchantUrlPart;


<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> b856f04f9362059bd510fec0f3a9b4a984435d96
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Temporal(TemporalType.TIMESTAMP)
	@Version
	@JsonIgnore
	private Date lastModified;

<<<<<<< HEAD
=======
=======
>>>>>>> 63cbfe1fb644b0ed860a22788730461b6a199082
>>>>>>> b856f04f9362059bd510fec0f3a9b4a984435d96
	@OneToMany(mappedBy = "merchant", cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.TRUE)
	@NotAudited
	@JsonIgnore
	private List<MerchantKey> merchantKey;

	private Integer acountLocked;

	private int pureMoto;

	private boolean autoDetectDevice;

	private String panCard;

	private boolean panCardValidated;

	private String blockMailers;

	private String perfTemplate = "checkout_nitro";
<<<<<<< HEAD
=======
<<<<<<< HEAD
=======
	/*
	 * @OneToOne
	 * 
	 * @JoinColumn(name="amex_cat_code_id") private AmexCategoryCode
	 * amexCategoryCode;
	 */
//	TODO: PPD DEPENDENCY REMOVAL
//	@JsonIgnore
//	@OneToMany(mappedBy = "merchantPrepaidSchemePk.merchant")
//	@LazyCollection(LazyCollectionOption.TRUE)
//	@NotAudited
//	private List<PrepaidSemiclosedMerchants> prepaidSemiclosedMerchants;
>>>>>>> 63cbfe1fb644b0ed860a22788730461b6a199082
>>>>>>> b856f04f9362059bd510fec0f3a9b4a984435d96
	
	public String getPerfTemplate() {
		return perfTemplate;
	}

	public void setPerfTemplate(String perfTemplate) {
		this.perfTemplate = perfTemplate;
	}

	private String notifyUrl;
	
	private Integer redCategory;
	
	@Transient
	private boolean nitro;
	
	private boolean nitroForMerchant;

	
	private boolean cookieBased;
	
	private Integer dccEnabled;
	
	private boolean citrusMotoTxnBased;

	private boolean sslAutoSave;
	
	private boolean nitroIframe;
	
<<<<<<< HEAD
=======
<<<<<<< HEAD
=======
	private boolean nitroIframeMobile;
>>>>>>> 63cbfe1fb644b0ed860a22788730461b6a199082
>>>>>>> b856f04f9362059bd510fec0f3a9b4a984435d96

	public Merchant() {
	}

	public Merchant(String name, String siteUrl, String vanityUrlPart) {
		super();
		this.name = name;
		this.siteUrl = siteUrl;
		this.vanityUrlPart = vanityUrlPart;
<<<<<<< HEAD
		this.created = new Date();
=======
<<<<<<< HEAD
		this.created = new Date();
=======
>>>>>>> 63cbfe1fb644b0ed860a22788730461b6a199082
>>>>>>> b856f04f9362059bd510fec0f3a9b4a984435d96
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSiteUrl() {
		return siteUrl;
	}

	public void setSiteUrl(String siteUrl) {
		this.siteUrl = siteUrl;
	}


	public String getVanityUrlPart() {
		return vanityUrlPart;
	}

	public void setVanityUrlPart(String vanityUrlPart) {
		this.vanityUrlPart = vanityUrlPart;
	}


<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> b856f04f9362059bd510fec0f3a9b4a984435d96
	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getCreated() {
		return created;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

<<<<<<< HEAD
=======
=======
>>>>>>> 63cbfe1fb644b0ed860a22788730461b6a199082
>>>>>>> b856f04f9362059bd510fec0f3a9b4a984435d96
	@Override
	public String toString() {
		return "Merchant [id=" + id + ", name=" + name + ", siteUrl=" + siteUrl
				+ ", vanityUrlPart=" + vanityUrlPart + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Merchant) {
			Merchant merchant = (Merchant) o;
			if (merchant.getId() == this.id) {
				return true;
			}
		}
		return false;
	}
	
	public String getMerchantOpusId() {
		return merchantOpusId;
	}

	public void setMerchantOpusId(String merchantOpusId) {
		this.merchantOpusId = merchantOpusId;
	}

	public String getMerchantUrlPart() {
		return merchantUrlPart;
	}

	public void setMerchantUrlPart(String merchantUrlPart) {
		this.merchantUrlPart = merchantUrlPart;
	}

	@JsonIgnore
	public List<MerchantKey> getMerchantKey() {
		return merchantKey;
	}

	public void setMerchantKey(List<MerchantKey> merchantKey) {
		this.merchantKey = merchantKey;
	}

	public boolean getAcountLocked() {
		return acountLocked != null && acountLocked == 1 ? true : false;
	}

	public void setAcountLocked(boolean acountLocked) {
		if (acountLocked) {
			this.acountLocked = 1;
		} else {
			this.acountLocked = 0;
		}

	}

	public int getPureMoto() {
		return pureMoto;
	}

	public void setPureMoto(int pureMoto) {
		this.pureMoto = pureMoto;
	}

	public boolean isAutoDetectDevice() {
		return autoDetectDevice;
	}

	public void setAutoDetectDevice(boolean autoDetectDevice) {
		this.autoDetectDevice = autoDetectDevice;
	}

<<<<<<< HEAD
=======
<<<<<<< HEAD
=======
	/*
	 * public AmexCategoryCode getAmexCategoryCode() { return amexCategoryCode;
	 * }
	 * 
	 * public void setAmexCategoryCode(AmexCategoryCode amexCategoryCode) {
	 * this.amexCategoryCode = amexCategoryCode; }
	 */
>>>>>>> 63cbfe1fb644b0ed860a22788730461b6a199082
>>>>>>> b856f04f9362059bd510fec0f3a9b4a984435d96

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

<<<<<<< HEAD

=======
<<<<<<< HEAD

=======
>>>>>>> 63cbfe1fb644b0ed860a22788730461b6a199082
>>>>>>> b856f04f9362059bd510fec0f3a9b4a984435d96
	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}	

	public String getBlockMailers() {
		return blockMailers;
	}

	public void setBlockMailers(String blockMailers) {
		this.blockMailers = blockMailers;
	}


	public Integer getRedCategory() {
		return redCategory;
	}

	public void setRedCategory(Integer redCategory) {
		this.redCategory = redCategory;
	}
	
	// sslv2 check
	public void setNitroForMerchant(boolean nitroForMerchant) {
		this.nitroForMerchant = nitroForMerchant;
	}
	
<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> b856f04f9362059bd510fec0f3a9b4a984435d96
	// TODO - Need to get value from DB
	// currently check only with RechangeItnow
	public boolean isNitroForMerchant() {

		return nitroForMerchant;
	}
	
	public void setNitro(boolean nitro) {
		this.nitro = nitro;
	}
	
	public boolean getNitro() {
		return nitro;
	}
<<<<<<< HEAD
=======
=======
>>>>>>> 63cbfe1fb644b0ed860a22788730461b6a199082
>>>>>>> b856f04f9362059bd510fec0f3a9b4a984435d96

	public boolean isCookieBased() {
		return cookieBased;
	}

	public void setCookieBased(boolean cookieBased) {
		this.cookieBased = cookieBased;
	}

	public Integer getDccEnabled() {
		return dccEnabled;
	}

	public void setDccEnabled(Integer dccEnabled) {
		this.dccEnabled = dccEnabled;
	}


	public boolean isCitrusMotoTxnBased() {
		return citrusMotoTxnBased;
	}

	public void setCitrusMotoTxnBased(boolean citrusMotoTxnBased) {
		this.citrusMotoTxnBased = citrusMotoTxnBased;
	}

	public boolean isSslAutoSave() {
		return sslAutoSave;
	}

	public void setSslAutoSave(boolean sslAutoSave) {
		this.sslAutoSave = sslAutoSave;
	}

	public boolean isNitroIframe() {
		return nitroIframe;
	}

	public void setNitroIframe(boolean nitroIframe) {
		this.nitroIframe = nitroIframe;
	}

<<<<<<< HEAD

=======
<<<<<<< HEAD

=======
	public boolean isNitroIframeMobile() {
		return nitroIframeMobile;
	}

	public void setNitroIframeMobile(boolean nitroIframeMobile) {
		this.nitroIframeMobile = nitroIframeMobile;
	}
>>>>>>> 63cbfe1fb644b0ed860a22788730461b6a199082
>>>>>>> b856f04f9362059bd510fec0f3a9b4a984435d96
}
