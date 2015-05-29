/*
 * Copyright (c) 2012 CitrusPay. All Rights Reserved.
 *
 * This software is the proprietary information of CitrusPay.
 * Use is subject to license terms.
 */
package com.citruspay.enquiry.persistence.interfaces;

import com.citruspay.enquiry.persistence.interfaces.BaseDAO;
import com.citruspay.enquiry.persistence.entity.PGCatCredential;

public interface PGCatCredentialDAO extends BaseDAO<PGCatCredential, Long>{

	public PGCatCredential findById(Integer id);

	public PGCatCredential saveOrUpdate(PGCatCredential pgCatCredential);

}
