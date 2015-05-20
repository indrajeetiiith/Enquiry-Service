package com.citruspay.enquiry.persistence.implementation;


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.citruspay.CommonUtil;
import com.citruspay.enquiry.persistence.PersistenceManager;
import com.citruspay.enquiry.persistence.entity.Merchant;
import com.citruspay.enquiry.persistence.entity.Transaction;
import com.citruspay.enquiry.persistence.entity.TransactionType;
import com.citruspay.enquiry.persistence.interfaces.TransactionDAO;

public class TransactionDAOImpl implements TransactionDAO {

	private static final Logger log = LoggerFactory.getLogger(TransactionDAOImpl.class);
	

	
	
	public List<Transaction> findByMerchantTxnIdAndMerchantRefundTxId(
			String merTxnId, String merRefundTxId, Merchant merchant) {
		EntityManager entityManager = null;
		
		entityManager = PersistenceManager.INSTANCE.getEntityManager();

		TypedQuery<Transaction> query = entityManager
				.createQuery(
						"SELECT txn from Transaction txn WHERE txn.merchant.id = ?1 and txn.merchantTxId = ?2 and ( txn.transactionType <> ?4 or (txn.transactionType = ?4 and txn.merchantRefundTxId = ?3) ) ",
						Transaction.class);
		query.setParameter(1, merchant.getId());
		query.setParameter(2, merTxnId);
		query.setParameter(3, merRefundTxId);
		query.setParameter(4, TransactionType.REFUND);
		return query.getResultList();
	}

	public Transaction getLastTxnByMtxAndMerchant(String merchantTxId,
			Merchant merchant) {
		try {
			EntityManager entityManager = null;
			
			entityManager = PersistenceManager.INSTANCE.getEntityManager();
			
			System.out.println("\n in function getLastTxnByMtxAndMerchant merchanttxnid:"+merchantTxId+"merchant="+merchant);
			
			TypedQuery<Transaction> query = entityManager
					.createQuery(
							"SELECT txn from Transaction txn WHERE txn.merchantTxId = ?1 and txn.merchant.id = ?2 ORDER BY txn.lastModified desc",
							Transaction.class);
			query.setParameter(1, merchantTxId);
			query.setParameter(2, merchant.getId());

			if (CommonUtil.isNotNull(query.getResultList())
					&& CommonUtil.isNotNull(query.getResultList().get(0))) {
				return query.getResultList().get(0);
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	public List<Transaction> findByMerchantTxnId(String merTxnId,
			Merchant merchant) {
		EntityManager entityManager = null;
		
		entityManager = PersistenceManager.INSTANCE.getEntityManager();

		TypedQuery<Transaction> query = entityManager
				.createQuery(
						"SELECT txn from Transaction txn WHERE txn.merchantTxId = ?1 and txn.merchant.id = ?2",
						Transaction.class);
		query.setParameter(1, merTxnId);
		query.setParameter(2, merchant.getId());
		return query.getResultList();
	}
	
	public Transaction getRefundTxnByMtxAndMerchantAndId(String merchantTxId,
			String merchantRefundTxId, Merchant merchant) {
		try {
			EntityManager entityManager = null;
			
			entityManager = PersistenceManager.INSTANCE.getEntityManager();

			TypedQuery<Transaction> query = entityManager
					.createQuery(
							"SELECT txn from Transaction txn WHERE txn.merchantTxId = ?1 and txn.merchant.id = ?2 and txn.merchantRefundTxId = ?3 ORDER BY txn.lastModified desc",
							Transaction.class);
			query.setParameter(1, merchantTxId);
			query.setParameter(2, merchant.getId());
			query.setParameter(3, merchantRefundTxId);

			if (CommonUtil.isNotNull(query.getResultList())
					&& CommonUtil.isNotNull(query.getResultList().get(0))) {
				return query.getResultList().get(0);
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}
	


}
