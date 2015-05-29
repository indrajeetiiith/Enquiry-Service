/*
 * Copyright (c) 2012 CitrusPay. All Rights Reserved.
 *
 * This software is the proprietary information of CitrusPay.
 * Use is subject to license terms.
 */
package com.citruspay.enquiry.persistence.implementation;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.citruspay.enquiry.persistence.PersistenceManager;
import com.citruspay.enquiry.persistence.implementation.BaseDAOImpl;
import com.citruspay.enquiry.persistence.interfaces.PGCatCredentialDAO;
import com.citruspay.enquiry.persistence.entity.PGCatCredential;

public class PGCatCredentialDAOImpl extends
		BaseDAOImpl<PGCatCredential, Long> implements PGCatCredentialDAO {
	


	@PersistenceContext
	private EntityManager em;
	
	@Override
	public PGCatCredential saveOrUpdate(PGCatCredential amexCatCredential) {
		return em.merge(amexCatCredential);

	}
	
	@Override
	public PGCatCredential findById(Integer id) {

		EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

		PGCatCredential amexCatCredential = null;
		try {
			TypedQuery<PGCatCredential> query = entityManager.createQuery(
					"from PGCatCredential acc where acc.id=?1",
					PGCatCredential.class);
			query.setParameter(1, id);
			amexCatCredential = query.getSingleResult();
		} catch (NoResultException ex) {

		}
		return amexCatCredential;
	}
	
}
