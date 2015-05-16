package com.citruspay.enquiry.persistence.implementation;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;



<<<<<<< HEAD

import com.citruspay.enquiry.persistence.entity.Merchant;
import com.citruspay.enquiry.persistence.interfaces.MerchantDAO;
import com.citruspay.enquiry.persistence.PersistenceManager;
=======
import com.citruspay.enquiry.persistence.entity.Merchant;
import com.citruspay.enquiry.persistence.interfaces.MerchantDAO;
>>>>>>> 63cbfe1fb644b0ed860a22788730461b6a199082
import com.citruspay.enquiry.persistence.util.KeyType;
import com.citruspay.CommonUtil;

public class MerchantDAOImpl implements MerchantDAO {

<<<<<<< HEAD

	public Merchant saveOrUpdate(Merchant merchant) {
		EntityManager entityManager = null;
		
		entityManager = PersistenceManager.INSTANCE.getEntityManager();

		
		return entityManager.merge(merchant);
=======
	@PersistenceContext
	private EntityManager em;

	public Merchant saveOrUpdate(Merchant merchant) {
		return em.merge(merchant);
>>>>>>> 63cbfe1fb644b0ed860a22788730461b6a199082
	}

	
	public void delete(Integer id) {
<<<<<<< HEAD
		EntityManager entityManager = null;
		
			entityManager = PersistenceManager.INSTANCE.getEntityManager();
			

		Merchant merchant = findById(id);
		if (merchant != null) {
			entityManager.remove(merchant);
=======
		Merchant merchant = findById(id);
		if (merchant != null) {
			em.remove(merchant);
>>>>>>> 63cbfe1fb644b0ed860a22788730461b6a199082
		}
	}

	public Merchant findById(Integer id) {
<<<<<<< HEAD
		EntityManager entityManager = null;
		
		entityManager = PersistenceManager.INSTANCE.getEntityManager();
		
		if (id == null)
			return null;
		return entityManager.find(Merchant.class, id);
=======
		if (id == null)
			return null;
		return em.find(Merchant.class, id);
>>>>>>> 63cbfe1fb644b0ed860a22788730461b6a199082

	}

	public Merchant findByName(String name) {
<<<<<<< HEAD
		EntityManager entityManager = null;
		
		entityManager = PersistenceManager.INSTANCE.getEntityManager();

		
		TypedQuery<Merchant> query = entityManager.createQuery(
=======
		TypedQuery<Merchant> query = em.createQuery(
>>>>>>> 63cbfe1fb644b0ed860a22788730461b6a199082
				"SELECT m from Merchant m WHERE m.name = ?1", Merchant.class);
		query.setParameter(1, name);
		List<Merchant> results = query.getResultList();
		if (results.isEmpty()) {
			return null;
		} else {
			return results.get(0);
		}
	}

	public Merchant findBySecretId(String secretId) {
<<<<<<< HEAD
		EntityManager entityManager = null;
		
		entityManager = PersistenceManager.INSTANCE.getEntityManager();

		System.out.println("secred id ="+secretId);
		TypedQuery<Merchant> query = entityManager
=======
		TypedQuery<Merchant> query = em
>>>>>>> 63cbfe1fb644b0ed860a22788730461b6a199082
				.createQuery(
						"SELECT m from Merchant m LEFT JOIN FETCH m.merchantKey mkey WHERE mkey.secretId = ?1 and mkey.keyType = ?2",
						Merchant.class);
		query.setParameter(1, secretId);
		query.setParameter(2, KeyType.HMAC);
		List<Merchant> results = query.getResultList();
		if (results.isEmpty()) {
			return null;
		} else {
			return results.get(0);
		}
	}

	
}
