package com.citruspay.enquiry.persistence.implementation;


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.citruspay.CommonUtil;
import com.citruspay.enquiry.persistence.PersistenceManager;
import com.citruspay.enquiry.persistence.entity.Merchant;
import com.citruspay.enquiry.persistence.entity.Transaction;
import com.citruspay.enquiry.persistence.entity.TransactionStatus;
import com.citruspay.enquiry.persistence.entity.TransactionType;
import com.citruspay.enquiry.persistence.interfaces.TransactionDAO;

public class TransactionDAOImpl implements TransactionDAO {

	private static final Logger log = LoggerFactory.getLogger(TransactionDAOImpl.class);
	
	@PersistenceContext
	private EntityManager em= PersistenceManager.INSTANCE.getEntityManager();
;

	
	public List<Transaction> findByMerchantTxnIdAndMerchantRefundTxId(
			String merTxnId, String merRefundTxId, Merchant merchant) {
		EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

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
			EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();
			
			
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
		EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

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
			EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

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

	public List<Transaction> findByMerchantTxnIdAndGateway(String merTxnId,
			Merchant merchant, String pgCode) {
		EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

		TypedQuery<Transaction> query = entityManager
				.createQuery(
						"SELECT txn from Transaction txn WHERE txn.merchantTxId = ?1 and txn.merchant = ?2 and txn.txnGateway like ?3  order by txn.id",
						Transaction.class);
		query.setParameter(1, merTxnId);
		query.setParameter(2, merchant);
		query.setParameter(3, CommonUtil.JPQL_LIKE + pgCode
				+ CommonUtil.JPQL_LIKE);
		return query.getResultList();
	}
	public Transaction findByCTXandMTXIdAndType(String transactionId,
			String merchantTxId, Merchant merchant, TransactionType type) {
		try {
			EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();


			TypedQuery<Transaction> query = entityManager
					.createQuery(
							"SELECT txn from Transaction txn WHERE txn.txId = ?1 and txn.merchantTxId = ?2 and txn.merchant = ?3 and txn.transactionType=?4",
							Transaction.class);
			query.setParameter(1, transactionId);
			query.setParameter(2, merchantTxId);
			query.setParameter(3, merchant);
			query.setParameter(4, type);
			return query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}
	public Transaction saveOrUpdate(Transaction transaction) {

		EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

		Transaction v2 = findByMTXMerchantAndTxnStatus("R_"+transaction.getMerchantTxId(),
				transaction.getMerchant(), TransactionStatus.V2_PAGE_RENDERED);
		
		if(null !=v2) {
			removeTransaction(v2);
		}		


                v2 = findByMTXMerchantAndTxnStatus("R_"+transaction.getMerchantTxId(),
                                transaction.getMerchant(), TransactionStatus.V2_REQUEST_ARRIVED);

                if(null !=v2) {
                        removeTransaction(v2);
                }
		
		return entityManager.merge(transaction);
	}


	public void removeTransaction(Transaction transaction){
		if (transaction != null)
			em.remove(transaction);	
	}

	public Transaction findByMTXMerchantAndTxnStatus(String merTxnId,
			Merchant merchant, TransactionStatus status) {
		TypedQuery<Transaction> query = em
				.createQuery(
						"SELECT txn from Transaction txn WHERE txn.merchantTxId = ?1 and txn.merchant = ?2 and txn.status = ?3 ",
						Transaction.class);
		query.setParameter(1, merTxnId);
		query.setParameter(2, merchant);
		query.setParameter(3, status);

		try {
			return query.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}

	}

	public List<Transaction> findListByCitrusTransactionId(String ctx) {
		List<Transaction> results = null;
		TypedQuery<Transaction> query = em.createQuery(
				"SELECT txn from Transaction txn WHERE txn.txId = ?1 ",
				Transaction.class);
		query.setParameter(1, ctx);
		try {
			results = query.getResultList();
			if (results.size() > 0) {
				return results;
			} else {
				return null;
			}
		} catch (NoResultException ex) {
			return null;
		}
	}


}
