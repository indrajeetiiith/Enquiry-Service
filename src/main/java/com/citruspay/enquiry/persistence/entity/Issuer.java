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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

@SuppressWarnings("serial")
@Entity
@Table(name = "BNK_ISSUER")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class Issuer implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	private String code;

	@NotNull
	private String name;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Temporal(TemporalType.TIMESTAMP)
	@Version
	private Date lastModified;

	private int enabled;

	@NotAudited
	@OneToMany(mappedBy = "issuer", cascade = CascadeType.ALL)
	private List<PGDownTime> pgDownTime;

	public Issuer() {
	}

	public Issuer(String code, String name) {
		super();
		this.code = code;
		this.name = name;
		this.created = new Date();
	}

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

	@Override
	public boolean equals(Object o) {
		if (o instanceof Issuer) {
			Issuer issuer = (Issuer) o;
			if (this.getCode().equals(issuer.getCode())) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.getCode().hashCode();
	}

	public int getEnabled() {
		return enabled;
	}

	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}

	@JsonIgnore
	public List<PGDownTime> getPgDownTime() {
		return pgDownTime;
	}

	public void setPgDownTime(List<PGDownTime> pgDownTime) {
		this.pgDownTime = pgDownTime;
	}

}
