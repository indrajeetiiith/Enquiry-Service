package com.citruspay.enquiry.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.citruspay.BaseDomain;

@Entity
//@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Table(name="pg_cat_credential")
public class PGCatCredential extends BaseDomain{

	/**
	 * 
	 */
	private static final long serialVersionUID = -435014961780320174L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String mid;
	
	private String accessCode;
	
	private String secretKey;
	
	private String amausrid;
	
	private String amapswd;
	
	private int isDeleted;	
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getAccessCode() {
		return accessCode;
	}

	public void setAccessCode(String accessCode) {
		this.accessCode = accessCode;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getAmausrid() {
		return amausrid;
	}

	public void setAmausrid(String amausrid) {
		this.amausrid = amausrid;
	}

	public String getAmapswd() {
		return amapswd;
	}

	public void setAmapswd(String amapswd) {
		this.amapswd = amapswd;
	}

	public int getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(int isDeleted) {
		this.isDeleted = isDeleted;
	}
}
