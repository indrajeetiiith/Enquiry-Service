package com.citruspay.enquiry.persistence.interfaces;


import com.citruspay.enquiry.persistence.entity.Merchant;
import com.citruspay.enquiry.persistence.entity.Transaction;

public interface TransactionDAO {


	public Transaction getLastTxnByMtxAndMerchant(String merchantTxId,
			Merchant merchant);
	
	}
