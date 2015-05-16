/*
 * Copyright (c) 2012 CitrusPay. All Rights Reserved.
 *
 * This software is the proprietary information of CitrusPay.
 * Use is subject to license terms.
 */

package com.citruspay.enquiry.persistence.implementation;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.citruspay.enquiry.persistence.entity.Merchant;
import com.citruspay.enquiry.persistence.entity.MerchantKey;
import com.citruspay.enquiry.persistence.interfaces.MerchantKeyDAO;

public class MerchantKeyDAOImpl implements MerchantKeyDAO {

	private static final Logger logger = LoggerFactory
			.getLogger(MerchantKeyDAOImpl.class);

	@PersistenceContext
	private EntityManager em;

	public List<MerchantKey> findMerchantKeys(Merchant merchant) {
		TypedQuery<MerchantKey> query = em.createQuery(
				"Select mk from MerchantKey mk where mk.merchant = ?1",
				MerchantKey.class);
		query.setParameter(1, merchant);
		List<MerchantKey> keys = query.getResultList();
		return keys;
	}

	public MerchantKey findById(Integer id) {
		if (id == null)
			return null;
		return em.find(MerchantKey.class, id);
	}


}
