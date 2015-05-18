package com.citruspay.enquiry.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.citruspay.CommonUtil;
import com.citruspay.enquiry.persistence.entity.PGTransaction;
import com.citruspay.enquiry.type.GatewayType;
import com.citruspay.enquiry.type.PGCode;
import com.citruspay.enquiry.persistence.entity.TransactionStatus;
import com.citruspay.enquiry.persistence.entity.PaymentGateway;
import com.citruspay.enquiry.persistence.entity.Transaction;
import com.citruspay.enquiry.persistence.entity.Merchant;
import com.citruspay.enquiry.persistence.implementation.MerchantDAOImpl;
import com.citruspay.enquiry.persistence.implementation.PaymentGatewayDAOImpl;
import com.citruspay.enquiry.persistence.implementation.TransactionDAOImpl;
import com.citruspay.enquiry.api.EnquiryResponse;
import com.citruspay.enquiry.api.EnquiryRequest;

public class EnquiryTransactionBase {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(EnquiryTransactionBase.class);

	private static final int ERROR_CODE_502 = 502;
	private static final int ERROR_CODE_401 = 401;
	private static final int ERROR_CODE_404 = 404;
	public static final int RESP_CODE_SUCCESS = 200;
	public static final int BAD_REQUEST = 400;
	private static final int INTERNAL_SERVER_ERROR = 500;
	private static final String BAD_REQUEST_MSG = "Merchant mandatory parameter missing";
	private static final String MERCHANT_NOT_FOUND = "Bad Request: Merchant not found";
	private static final String NO_TXN_FOUND = "No transaction found";
	private static final String NO_REFUND_TXN_FOUND = "Refund Transaction does not exist";

	public EnquiryResponse enquiry(EnquiryRequest enquiryRequest)
	{
		System.out.println("entering function enquiry request="+enquiryRequest);

		EnquiryResponse enquiryResponse = new EnquiryResponse();
		try {
			// Validate request parameters
			String merchantAccessKey = enquiryRequest.getMerchantAccessKey();
			String transactionId =  enquiryRequest.getMerchantTxnId();
			String merchantRefundTxId = enquiryRequest.getMerchantRefundTxId();
			if (!IsValidRequest(merchantAccessKey, transactionId)) {
				LOGGER.error("Enquiry API : Mandatory request parameter missing for enquiry id: "
						+ transactionId);
				handleValidationError(BAD_REQUEST, BAD_REQUEST_MSG,enquiryResponse);
				return enquiryResponse;
				
			}
			// Find merchant
			Merchant merchant = new MerchantDAOImpl()
					.findBySecretId(merchantAccessKey);
			System.out.println("merchant="+merchant);

			if (merchant == null) {
				LOGGER.error("Enquiry API : Merchant not found for enquiry request id:"
						+ transactionId);
				handleValidationError(ERROR_CODE_404, MERCHANT_NOT_FOUND,enquiryResponse);
				return enquiryResponse;
			}
			LOGGER.debug("Enquiry API : Txn Enquiry request received for :"
					+ merchant.getName() + ":id=" + transactionId);
			// Get last modified transaction's pg for enquiry call
			
			
			
			Transaction txn = null;
			txn =new TransactionDAOImpl().getLastTxnByMtxAndMerchant(transactionId, merchant);
			if (CommonUtil.isNull(txn)) {
				LOGGER.error("Enquiry API : Txn not found for given txn id in citrus system:"
						+ merchant.getName() + ":" + transactionId);
				handleValidationError(RESP_CODE_SUCCESS,
						NO_TXN_FOUND,enquiryResponse);
				return enquiryResponse;


				
			}
			System.out.print("-----transaction = "+txn+ " txn id="+txn.getTxId()+" amount="+txn.getOrderAmount().getAmount().toString()+" lastmodified = "+txn.getLastModified()+ " txn.getTxngateway="+txn.getTxnGateway());
			/*if (txn.getPgTxResp()!= null) {
				System.out.println(" gateway = "+txn.getTxnGateway() + " pgtxnid = "+txn.getPgTxResp().getPgTxnId() + " authidcode="+txn.getPgTxResp().getAuthIdCode()+
						" issuer ref no="+txn.getPgTxResp().getIssuerRefNo());
			}*/
			if (!CommonUtil.isEmpty(merchantRefundTxId)) {
				// Get last modified transaction's pg for enquiry call
				Transaction refundTxn = new TransactionDAOImpl()
						.getRefundTxnByMtxAndMerchantAndId(transactionId,
								merchantRefundTxId, merchant);
	
				// Validate transaction
				if (CommonUtil.isNull(refundTxn)) {
					LOGGER.error("Enquiry API : Refund Txn not found for given txn id in citrus system:"
							+ merchant.getName() + ":" + transactionId);
					handleValidationError(RESP_CODE_SUCCESS,
							NO_REFUND_TXN_FOUND,enquiryResponse);
					return enquiryResponse;
				}
			}
			
			PaymentGateway pg = (CommonUtil.isNotNull(txn.getPgId())) ? new PaymentGatewayDAOImpl()
					.findById(txn.getPgId()) : null;

		

		
		}catch (Exception ex) {
			LOGGER.error(
					"Enquiry API : Exception during enquiry API call due to : "
							+ ex.getMessage(), ex);
		}
		System.out.println("exiting function enquiry");

		return enquiryResponse;
	}
	public boolean IsValidRequest(String merchantAccessKey, String transactionId)
	{
		if(CommonUtil.isEmpty(merchantAccessKey) || CommonUtil.isEmpty(transactionId)){
			return false;
		}else{
			return true;
		}
	}
	public void handleValidationError(int errorCode, String errorMessage,EnquiryResponse enquiryResponse) {
		enquiryResponse.setRespCode(errorCode);
		enquiryResponse.setRespMsg(errorMessage);
	}
	public static boolean inquiryRespFromCitrusDB(Transaction txn,
			String dailySettlementTime, String dailySettlementMin,
			PaymentGateway pg) {
		
		// display result from citrus DB if transaction status is ON_HOLD
		if (txn.getPaymentDetails() != null
				&& CommonUtil.isCreditOrDebitCard(txn.getPaymentDetails()
						.getPaymentMode())
				&& TransactionStatus.ON_HOLD.equals(txn.getStatus())) {
			return true;
		}

		PGTransaction pgTxn = txn.getPgTxResp();
		if ((CommonUtil.isNotNull(pgTxn) && pgTxn.getInquiryStatus() == 1)
				|| TransactionStatus.REVERSED.equals(txn.getStatus())) {
			return true;
		} else if (CommonUtil.isNotNull(pg)
				&& (pg.getGatewayType().equals(GatewayType.INTERNAL))
				&& (txn.getStatus().equals(TransactionStatus.FAIL)
						|| txn.getStatus().equals(TransactionStatus.FORWARDED)
						|| txn.getStatus().equals(TransactionStatus.SESSION_EXPIRED) 
						|| txn.getStatus().equals(TransactionStatus.DEBIT_REQ_SENT))) {
			return CommonUtil.validateDateTime(txn.getCreated(), dailySettlementTime,
					dailySettlementMin);

		} else if ((CommonUtil.isNotNull(pg) && PGCode.CITRUS_PG.toString()
				.equals(pg.getCode()))
				&& (txn.getStatus().equals(TransactionStatus.SUCCESS))) {
			return CommonUtil.validateDateTime(txn.getCreated(), dailySettlementTime,
					dailySettlementMin);
		}

		return false;
	}


	
}
