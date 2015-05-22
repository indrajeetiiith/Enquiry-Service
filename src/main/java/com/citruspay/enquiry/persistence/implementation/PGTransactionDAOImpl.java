package com.citruspay.enquiry.persistence.implementation;

import javax.persistence.EntityManager;

import com.citruspay.enquiry.persistence.PersistenceManager;
import com.citruspay.enquiry.persistence.entity.PGTransaction;
import com.citruspay.enquiry.persistence.interfaces.PGTransactionDAO;

public class PGTransactionDAOImpl implements PGTransactionDAO {

	public PGTransaction findById(Integer id) {
		EntityManager entityManager = null;
		
		entityManager = PersistenceManager.INSTANCE.getEntityManager();

		if (id == null)
			return null;
		return entityManager.find(PGTransaction.class, id);
	}

}
