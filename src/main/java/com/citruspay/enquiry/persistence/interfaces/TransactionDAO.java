package com.citruspay.enquiry.persistence.interfaces;


import java.util.List;

import com.citruspay.enquiry.persistence.entity.Merchant;
import com.citruspay.enquiry.persistence.entity.Transaction;
import com.citruspay.enquiry.persistence.entity.TransactionType;

public interface TransactionDAO {


	public Transaction getLastTxnByMtxAndMerchant(String merchantTxId,
			Merchant merchant);
	public List<Transaction> findByMerchantTxnId(String merTxnId,
			Merchant merchant);
	public List<Transaction> findByMerchantTxnIdAndMerchantRefundTxId(
			String merTxnId, String merRefundTxId, Merchant merchant);
	public Transaction getRefundTxnByMtxAndMerchantAndId(String merchantTxId,
			String merchantRefundTxId, Merchant merchant);
	public List<Transaction> findByMerchantTxnIdAndGateway(String merTxnId,
			Merchant merchant, String pgCode);
	public Transaction findByCTXandMTXIdAndType(String transactionId,
			String merchantTxId, Merchant merchant, TransactionType type);
	public Transaction saveOrUpdate(Transaction transaction);
	public List<Transaction> findListByCitrusTransactionId(String ctx);

	


}
