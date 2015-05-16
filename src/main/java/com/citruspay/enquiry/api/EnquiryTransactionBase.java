package com.citruspay.enquiry.api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.citruspay.CommonUtil;
import com.citruspay.enquiry.persistence.entity.Transaction;
import com.citruspay.enquiry.persistence.entity.Merchant;
import com.citruspay.enquiry.persistence.implementation.MerchantDAOImpl;
import com.citruspay.enquiry.persistence.implementation.TransactionDAOImpl;
import com.citruspay.enquiry.api.EnquiryResponse;
import com.citruspay.enquiry.api.EnquiryRequest;

public class EnquiryTransactionBase {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(EnquiryTransactionBase.class);

	public EnquiryResponse enquiry(EnquiryRequest enquiryRequest)
	{
		System.out.println("entering function enquiry request="+enquiryRequest);
		LOGGER.info("Enquiry API : Mandatory request parameter missing for enquiry id: merchantAccessKey="+enquiryRequest.getMerchantAccessKey() + " transactionId"+enquiryRequest.getMerchantTxnId());

		EnquiryResponse enquiryResponse = new EnquiryResponse();
		try {
			// Validate request parameters
			String merchantAccessKey = enquiryRequest.getMerchantAccessKey();
			String transactionId =  enquiryRequest.getMerchantTxnId();

			if (!validateRequest(merchantAccessKey, transactionId)) {
				LOGGER.error("Enquiry API : Mandatory request parameter missing for enquiry id: "
						+ transactionId);
			}

			// Find merchant
			Merchant merchant = new MerchantDAOImpl()
					.findBySecretId(merchantAccessKey);
			System.out.println("merchant="+merchant);

			if (merchant == null) {
				LOGGER.error("Enquiry API : Merchant not found for enquiry request id:"
						+ transactionId);
			}
			LOGGER.debug("Enquiry API : Txn Enquiry request received for :"
					+ merchant.getName() + ":id=" + transactionId);
			// Get last modified transaction's pg for enquiry call
			Transaction txn =new TransactionDAOImpl().getLastTxnByMtxAndMerchant(
					transactionId, merchant);

			// Validate transaction
			if (CommonUtil.isNull(txn)) {
				LOGGER.error("Enquiry API : Txn not found for given txn id in citrus system:"
						+ merchant.getName() + ":" + transactionId);
			}
			System.out.println("-----transaction = "+txn.getLastModified());
//			System.out.println("-----transaction = "+txn+" tx.getCreated()="+txn.getCreated()+"tx.getTransactionType().name()="+txn.getTransactionType().name()+"tx.getMerchantRefundTxId()=");/*+txn.getMerchantRefundTxId()+"tx.getPgTxResp()="
//					+txn.getPgTxResp().getAuthIdCode()+ "txn.getPgTxResp().getAuthIdCode()="+txn.getPgTxResp().getIssuerRefNo());*/




		} catch (Exception ex) {
			LOGGER.error(
					"Enquiry API : Exception during enquiry API call due to : "
							+ ex.getMessage(), ex);
		}
		System.out.println("exiting function enquriy line 49");

		return enquiryResponse;
	}
	public boolean validateRequest(String merchantAccessKey, String transactionId)
	{
		if(CommonUtil.isEmpty(merchantAccessKey) || CommonUtil.isEmpty(transactionId)){
			return false;
		}else{
			return true;
		}
		
		
	}
	
}
