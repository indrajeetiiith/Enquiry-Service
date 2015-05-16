package com.citruspay.enquiry.persistence.implementation;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;



import com.citruspay.enquiry.persistence.entity.Merchant;
import com.citruspay.enquiry.persistence.interfaces.MerchantDAO;
import com.citruspay.enquiry.persistence.util.KeyType;
import com.citruspay.CommonUtil;

public class MerchantDAOImpl implements MerchantDAO {

	@PersistenceContext
	private EntityManager em;

	public Merchant saveOrUpdate(Merchant merchant) {
		return em.merge(merchant);
	}

	
	public void delete(Integer id) {
		Merchant merchant = findById(id);
		if (merchant != null) {
			em.remove(merchant);
		}
	}

	public Merchant findById(Integer id) {
		if (id == null)
			return null;
		return em.find(Merchant.class, id);

	}

	public Merchant findByName(String name) {
		TypedQuery<Merchant> query = em.createQuery(
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
		TypedQuery<Merchant> query = em
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
