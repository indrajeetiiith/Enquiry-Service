package com.citruspay.enquiry.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.citruspay.CommonUtil;
import com.citruspay.enquiry.api.EnquiryResultList;
import com.citruspay.enquiry.persistence.entity.PaymentMode;
import com.citruspay.enquiry.GatewayServiceImpl;
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
	
	private PaymentGatewayDAOImpl pgDaoImpl = new PaymentGatewayDAOImpl();

	public EnquiryResponse enquiry(EnquiryRequest enquiryRequest) {

		EnquiryResponse enquiryResponse = new EnquiryResponse();
		
		try {
			// Validate request parameters
			String merchantAccessKey = enquiryRequest.getMerchantAccessKey();
			String transactionId =  enquiryRequest.getMerchantTxnId();
			String merchantRefundTxId = enquiryRequest.getMerchantRefundTxId();
			if (!IsValidRequest(merchantAccessKey, transactionId)) {
				LOGGER.error("Enquiry API : Mandatory request parameter missing for enquiry id: "
						+ transactionId);
				handleValidationError(BAD_REQUEST, BAD_REQUEST_MSG, enquiryResponse);
				return enquiryResponse;
				
			}
			// Find merchant . if it's not valid merchant then fill the resonse with error code and message as merchant not found and return
			Merchant merchant = new MerchantDAOImpl()
					.findBySecretId(merchantAccessKey);
			System.out.println("merchant="+merchant);

			if (merchant == null) {
				LOGGER.error("Enquiry API : Merchant not found for enquiry request id:"
						+ transactionId);
				handleValidationError(ERROR_CODE_404, MERCHANT_NOT_FOUND,enquiryResponse);
				return enquiryResponse;
			}
			else
				System.out.println( " merchant="+merchant);
			LOGGER.debug("Enquiry API : Txn Enquiry request received for :"
					+ merchant.getName() + ":id=" + transactionId);
			// Get last modified transaction's pg for enquiry call
			
			Transaction txn = new TransactionDAOImpl().getLastTxnByMtxAndMerchant(transactionId, merchant);
			if (CommonUtil.isNull(txn)) {
				LOGGER.error("Enquiry API : Txn not found for given txn id in citrus system:"
						+ merchant.getName() + ":" + transactionId);
				handleValidationError(RESP_CODE_SUCCESS, NO_TXN_FOUND, enquiryResponse);
				return enquiryResponse;
			}
			// validate merchant refund txnid given in the request
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
			
			PaymentGateway pg = CommonUtil.isNotNull(txn.getPgId()) ? pgDaoImpl.findById(txn.getPgId()) : null;
					
			System.out.println(" value="+inquiryRespFromCitrusDB(txn,"10", "30", pg));
			// fill the enquiry response with the data whatever we have for the time being
			//TODO read the value for settlement hr and settlement min from the properties file
			//if(inquiryRespFromCitrusDB(txn,"10", "30", pg) == true)
			{
				//Take data from Citrus DB and no need to go for External PG's enquiry call 
				populdateEnquiryResponse(enquiryResponse,txn,pg,merchantRefundTxId);
			}
			//else
			{
				//TODO call corresponding PG's enquiry , parse the response and fill the response and return;
			}
			//modify the credit/debit card expiration date.. increase the month by  one since in the db values are coming as 0-11 which is not good to present to the user.should be 1-12 so modifying the response

			modifyExpiryDateForCCandDC(enquiryResponse);

			
			return enquiryResponse;

		

		
		}catch (Exception ex) {
			LOGGER.error(
					"Enquiry API : Exception during enquiry API call due to : "
							+ ex.getMessage(), ex);
		}
		System.out.println("exiting function enquiry");

		return enquiryResponse;
	}
	private void modifyExpiryDateForCCandDC(EnquiryResponse enquiryResponse){
		if(enquiryResponse.getData() != null) {
			EnquiryResultList enquiryResultList = (EnquiryResultList) enquiryResponse.getData();
			if(!CommonUtil.isEmpty(enquiryResultList.getEnquiryResultList())){
				List<EnquiryResult> enquiryList = enquiryResultList.getEnquiryResultList();
				for (int i=0;i<enquiryList.size();i++) {
					EnquiryResult enquiryResult = enquiryList.get(i);
					if(PaymentMode.CREDIT_CARD.toString().equals(enquiryResult.getPaymentMode()) || 
							PaymentMode.DEBIT_CARD.toString().equals(enquiryResult.getPaymentMode())){
						if(!StringUtils.isEmpty(enquiryResult.getCardExpiryMonth())){
							int month = Integer.parseInt(enquiryResult.getCardExpiryMonth());
							enquiryResult.setCardExpiryMonth(String.valueOf(month+1));
							enquiryList.set(i, enquiryResult);
						}
					}
				}
				enquiryResultList.setEnquiryResultList(enquiryList);
				enquiryResponse.setData(enquiryResultList);
			}
		}
	}
	private void populdateEnquiryResponse(EnquiryResponse enquiryResponse,Transaction transaction,PaymentGateway pg,String merchantRefundTxId)
	{
		EnquiryResultList enquiryResultList = new EnquiryResultList();
		List<EnquiryResult> enquiryResult = new ArrayList<EnquiryResult>();
		List<Transaction> transactions = null;
		if (!CommonUtil.isEmpty(merchantRefundTxId)) {
			transactions = new TransactionDAOImpl()
					.findByMerchantTxnIdAndMerchantRefundTxId(
							transaction.getMerchantTxId(), merchantRefundTxId,
							transaction.getMerchant());
		} else {
			transactions = new TransactionDAOImpl().findByMerchantTxnId(
					transaction.getMerchantTxId(), transaction.getMerchant());
		}
		System.out.println(" +++++++++ length ="+transactions.size());
		for(Transaction txn : transactions)
		{
			EnquiryResult enquirySingleResult = new EnquiryResult();
	
			enquirySingleResult.setOriginalAmount(txn.getOrderAmount().getAmount().toString());
			enquirySingleResult.setRespCode(TransactionStatus.SUCCESS_ON_VERIFICATION
							.equals(txn.getStatus()) ? "0" : String.valueOf(txn
							.getStatus().ordinal()));
			enquirySingleResult.setTxnGateway(txn.getTxnGateway()); // setting PG value to display in the transaction history
			enquirySingleResult.setPgTxnId(txn.getPgTxResp().getPgTxnId());
			enquirySingleResult.setAuthIdCode(txn.getPgTxResp().getAuthIdCode());
			enquirySingleResult.setRRN(txn.getPgTxResp().getIssuerRefNo());
			
			enquirySingleResult.setRespMsg(txn.getStatus().getDisplayMsg());
			enquirySingleResult.setTxnId(txn.getTxId());
			enquirySingleResult.setTxnDateTime(CommonUtil.getDateStringInIST(txn.getCreated()));
			enquirySingleResult.setTxnType(txn.getTransactionType().name());
			enquirySingleResult.setMerchantRefundTxId(txn.getMerchantRefundTxId());
			enquirySingleResult.setMerchantTxnId(txn.getMerchantTxId());
			
			new GatewayServiceImpl().updatePaymentDetailAndAddressDetail(txn, enquirySingleResult, pg);
			enquiryResult.add(enquirySingleResult);

		}
		enquiryResultList.setEnquiryResultList(enquiryResult);
		enquiryResponse.setData(enquiryResultList);
		enquiryResponse.setRespCode(RESP_CODE_SUCCESS);
		enquiryResponse.setRespMsg(transaction.getStatus().getDisplayMsg());
				
	}

	public boolean IsValidRequest(String merchantAccessKey, String transactionId)
	{
		if(CommonUtil.isEmpty(merchantAccessKey) || CommonUtil.isEmpty(transactionId)){
			return false;
		} else {
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
