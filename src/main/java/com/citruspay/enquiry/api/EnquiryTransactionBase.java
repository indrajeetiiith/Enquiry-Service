package com.citruspay.enquiry.api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.citruspay.CommonUtil;
import com.citruspay.enquiry.persistence.entity.Merchant;
import com.citruspay.enquiry.persistence.implementation.MerchantDAOImpl;
import com.citruspay.enquiry.api.EnquiryResponse;
import com.citruspay.enquiry.api.EnquiryRequest;

public class EnquiryTransactionBase {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(EnquiryTransactionBase.class);

	public EnquiryResponse enquiry(EnquiryRequest enquiryRequest)
	{
<<<<<<< HEAD
		System.out.println("entering function enquiry request="+enquiryRequest);
		LOGGER.info("Enquiry API : Mandatory request parameter missing for enquiry id: merchantAccessKey="+enquiryRequest.getMerchantAccessKey() + " transactionId"+enquiryRequest.getMerchantTxnId());

=======
>>>>>>> 63cbfe1fb644b0ed860a22788730461b6a199082
		EnquiryResponse enquiryResponse = new EnquiryResponse();
		try {
			// Validate request parameters
			String merchantAccessKey = enquiryRequest.getMerchantAccessKey();
			String transactionId =  enquiryRequest.getMerchantTxnId();
<<<<<<< HEAD
=======
			LOGGER.info("Enquiry API : Mandatory request parameter missing for enquiry id: merchantAccessKey="+merchantAccessKey + " transactionId"+transactionId);
>>>>>>> 63cbfe1fb644b0ed860a22788730461b6a199082

			if (!validateRequest(merchantAccessKey, transactionId)) {
				LOGGER.error("Enquiry API : Mandatory request parameter missing for enquiry id: "
						+ transactionId);
			}

			// Find merchant
			Merchant merchant = new MerchantDAOImpl()
					.findBySecretId(merchantAccessKey);
<<<<<<< HEAD
			System.out.println("merchant="+merchant);

=======
>>>>>>> 63cbfe1fb644b0ed860a22788730461b6a199082
			if (merchant == null) {
				LOGGER.error("Enquiry API : Merchant not found for enquiry request id:"
						+ transactionId);
			}
			LOGGER.debug("Enquiry API : Txn Enquiry request received for :"
					+ merchant.getName() + ":id=" + transactionId);


		} catch (Exception ex) {
			LOGGER.error(
					"Enquiry API : Exception during enquiry API call due to : "
							+ ex.getMessage(), ex);
		}
<<<<<<< HEAD
		System.out.println("exiting function enquriy line 49");

=======
>>>>>>> 63cbfe1fb644b0ed860a22788730461b6a199082
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
