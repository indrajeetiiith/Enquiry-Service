package com.citruspay.enquiry.persistence.interfaces;


import java.util.List;

import com.citruspay.enquiry.persistence.entity.Merchant;
import com.citruspay.enquiry.persistence.entity.Transaction;

public interface TransactionDAO {


	public Transaction getLastTxnByMtxAndMerchant(String merchantTxId,
			Merchant merchant);
	public List<Transaction> findByMerchantTxnId(String merTxnId,
			Merchant merchant);
	public List<Transaction> findByMerchantTxnIdAndMerchantRefundTxId(
			String merTxnId, String merRefundTxId, Merchant merchant);
	public Transaction getRefundTxnByMtxAndMerchantAndId(String merchantTxId,
			String merchantRefundTxId, Merchant merchant);

	}
