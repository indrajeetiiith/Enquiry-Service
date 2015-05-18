package com.citruspay.enquiry.api;
import java.util.List;

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
			List<Transaction> transactions = null;
			transactions =new TransactionDAOImpl().findByMerchantTxnId(transactionId, merchant);
			if (CommonUtil.isNull(transactions)) {
				LOGGER.error("Enquiry API : Txn not found for given txn id in citrus system:"
						+ merchant.getName() + ":" + transactionId);
			}
			if(transactions != null)
			{
				System.out.println( "\n"+transactions.size() + " found\n");
			}

			// Validate transaction
					for(Transaction txn: transactions)
					{
						System.out.print("-----transaction = "+txn+ " txn id="+txn.getTxId()+" amount="+txn.getOrderAmount().getAmount().toString()+" lastmodified = "+txn.getLastModified()+ " txn.getTxngateway="+txn.getTxnGateway());
						if (txn.getPgTxResp()!= null) {
							
							System.out.println(" gateway = "+txn.getTxnGateway() + " pgtxnid = "+txn.getPgTxResp().getPgTxnId() + " authidcode="+txn.getPgTxResp().getAuthIdCode()+
									" issuer ref no="+txn.getPgTxResp().getIssuerRefNo());
						}
						else
							
						System.out.println();

						//			System.out.println("-----transaction = "+txn+" tx.getCreated()="+txn.getCreated()+"tx.getTransactionType().name()="+txn.getTransactionType().name()+"tx.getMerchantRefundTxId()=");/*+txn.getMerchantRefundTxId()+"tx.getPgTxResp()="
						//					+txn.getPgTxResp().getAuthIdCode()+ "txn.getPgTxResp().getAuthIdCode()="+txn.getPgTxResp().getIssuerRefNo());*/


					}


		} catch (Exception ex) {
			LOGGER.error(
					"Enquiry API : Exception during enquiry API call due to : "
							+ ex.getMessage(), ex);
		}
		System.out.println("exiting function enquiry");

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
