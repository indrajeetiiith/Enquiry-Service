package com.citruspay.enquiry.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.citruspay.BaseDomain;

@Entity
@Table(name="pg_cat_code")
public class MerchantPGCategoryCode extends BaseDomain{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4820490396526685699L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String industryCode;
	
	private String mid;
	
	private int isDeleted;
	
	private Integer pgCredentialId;
	
	private boolean isMaster;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getIndustryCode() {
		return industryCode;
	}

	public void setIndustryCode(String industryCode) {
		this.industryCode = industryCode;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public int getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(int isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Integer getPGCredentialId() {
		return pgCredentialId;
	}

	public void setPGCredentialId(Integer pgCredentialId) {
		this.pgCredentialId = pgCredentialId;
	}

	public boolean isMaster() {
		return isMaster;
	}

	public void setMaster(boolean isMaster) {
		this.isMaster = isMaster;
	}
	
}
