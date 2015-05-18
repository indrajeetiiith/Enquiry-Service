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
import com.citruspay.enquiry.persistence.entity.PaymentGateway;
import com.citruspay.enquiry.persistence.interfaces.PaymentGatewayDAO;

public class PaymentGatewayDAOImpl implements PaymentGatewayDAO {
	@PersistenceContext
	private EntityManager em;

	public PaymentGateway findById(Integer id) {

		PaymentGateway pg = null;
		try {
			EntityManager entityManager = null;
			entityManager = PersistenceManager.INSTANCE.getEntityManager();

			TypedQuery<PaymentGateway> query = entityManager.createQuery(
					"from PaymentGateway pg where pg.id=?1",
					PaymentGateway.class);
			query.setParameter(1, id);
			pg = query.getSingleResult();
		} catch (NoResultException ex) {

		}
		return pg;
	}


}
