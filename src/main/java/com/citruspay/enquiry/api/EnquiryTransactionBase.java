package com.citruspay.enquiry.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.citruspay.CommonUtil;
import com.citruspay.PaymentUtil;
import com.citruspay.enquiry.persistence.entity.Address;
import com.citruspay.enquiry.persistence.entity.ConsumerPaymentDetail;
import com.citruspay.enquiry.persistence.entity.CreditCardPaymentDetail;
import com.citruspay.enquiry.persistence.entity.DebitCardPaymentDetail;
import com.citruspay.enquiry.persistence.entity.ImpsPaymentDetail;
import com.citruspay.enquiry.persistence.entity.Merchant;
import com.citruspay.enquiry.persistence.entity.NetBankingPaymentDetail;
import com.citruspay.enquiry.persistence.entity.PGTransaction;
import com.citruspay.enquiry.persistence.entity.PaymentGateway;
import com.citruspay.enquiry.persistence.entity.PaymentMode;
import com.citruspay.enquiry.persistence.entity.Transaction;
import com.citruspay.enquiry.persistence.entity.TransactionStatus;
import com.citruspay.enquiry.persistence.implementation.MerchantDAOImpl;
import com.citruspay.enquiry.persistence.implementation.PaymentGatewayDAOImpl;
import com.citruspay.enquiry.persistence.implementation.TransactionDAOImpl;
import com.citruspay.enquiry.persistence.interfaces.PaymentGatewayDAO;
import com.citruspay.enquiry.persistence.interfaces.TransactionDAO;
import com.citruspay.enquiry.type.GatewayType;
import com.citruspay.enquiry.type.PGCode;

public class EnquiryTransactionBase {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(EnquiryTransactionBase.class);

	private static final int ERROR_CODE_404 = 404;
	public static final int RESP_CODE_SUCCESS = 200;
	public static final int BAD_REQUEST = 400;
	private static final String BAD_REQUEST_MSG = "Merchant mandatory parameter missing";
	private static final String MERCHANT_NOT_FOUND = "Bad Request: Merchant not found";
	private static final String NO_TXN_FOUND = "No transaction found";
	private static final String NO_REFUND_TXN_FOUND = "Refund Transaction does not exist";
	private static final String ENQUIRY_SUCCESSFULL = "Enquiry successful";

	/**
	 * This function gets called from rest service and does all the business logic for the data required in response . 
	 * @param enquiryRequest
	 * @return
	 */
	public EnquiryResponse enquiry(EnquiryRequest enquiryRequest) {

		EnquiryResponse enquiryResponse = new EnquiryResponse();
		
		try {
			// Validate request parameters
			String merchantAccessKey = enquiryRequest.getMerchantAccessKey();
			String transactionId =  enquiryRequest.getMerchantTxnId();
			if (!IsValidRequest(merchantAccessKey, transactionId)) {
				LOGGER.error("Enquiry API : Mandatory request parameter missing for enquiry id: "
						+ transactionId);
				handleValidationError(BAD_REQUEST, BAD_REQUEST_MSG, enquiryResponse);
				return enquiryResponse;
				
			}
			// Find merchant . if it's not valid merchant then fill the response with error code and message as merchant not found and return
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
			TransactionDAO transactionDAO = new TransactionDAOImpl();
			Transaction txn = transactionDAO.getLastTxnByMtxAndMerchant(transactionId, merchant);
			if (CommonUtil.isNull(txn)) {
				LOGGER.error("Enquiry API : Txn not found for given txn id in citrus system:"
						+ merchant.getName() + ":" + transactionId);
				handleValidationError(RESP_CODE_SUCCESS, NO_TXN_FOUND, enquiryResponse);
				return enquiryResponse;
			}
			// validate merchant refund txnid given in the request
			String merchantRefundTxId = enquiryRequest.getMerchantRefundTxId();
			if (!CommonUtil.isEmpty(merchantRefundTxId)) {
				// Get last modified transaction's pg for enquiry call
				Transaction refundTxn = transactionDAO
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
			PaymentGatewayDAO pgDao = new PaymentGatewayDAOImpl();

			PaymentGateway pg = CommonUtil.isNotNull(txn.getPgId()) ? pgDao.findById(txn.getPgId()) : null;
					
			// fill the enquiry response with the data whatever we have for the time being
			//TODO read the value for settlement hr and settlement min from the properties file
			// read value for settlement hour and settlement minute from properties file
			//if(inquiryRespFromCitrusDB(txn,"10", "30", pg) == true)
			{
				//Take data from Citrus DB and no need to go for External PG's enquiry call 
				populdateEnquiryResponse(enquiryResponse,txn,pg,merchantRefundTxId,transactionDAO);
			}
			//else
			{
				modifyExpiryDateForCCandDC(enquiryResponse);

				//TODO call corresponding PG's enquiry , parse the response and fill the response and return;
			}
			

			return enquiryResponse;

		
		}catch (Exception ex) {
			LOGGER.error(
					"Enquiry API : Exception during enquiry API call due to : "
							+ ex.getMessage(), ex);
		}
		System.out.println("exiting function enquiry");

		return enquiryResponse;
	}
	
	/**This function modifies the credit/debit card expiration month by  one since in the db values are coming as 0-11 which is not good to present to the user.
	 * The numbering should be 1-12 so modify the response.
	 * 
	 * @param enquiryResponse
	 */
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
	
	/**
	 * This function populates all the fields of enquiry response 
	 * @param enquiryResponse
	 * @param transaction
	 * @param pg
	 * @param merchantRefundTxId
	 * @param transactionDAO
	 */
	private void populdateEnquiryResponse(EnquiryResponse enquiryResponse,Transaction transaction,PaymentGateway pg,String merchantRefundTxId,TransactionDAO transactionDAO)
	{
		EnquiryResultList enquiryResultList = new EnquiryResultList();
		List<EnquiryResult> enquiryResult = new ArrayList<EnquiryResult>();
		List<Transaction> transactions = null;
		if (!CommonUtil.isEmpty(merchantRefundTxId)) {
			transactions = transactionDAO
					.findByMerchantTxnIdAndMerchantRefundTxId(
							transaction.getMerchantTxId(), merchantRefundTxId,
							transaction.getMerchant());
		} else {
			transactions = transactionDAO.findByMerchantTxnId(
					transaction.getMerchantTxId(), transaction.getMerchant());
		}
		if (CommonUtil.isNotEmpty(transactions)) {

			enquiryResponse.setRespCode(RESP_CODE_SUCCESS);
			enquiryResponse.setRespMsg(ENQUIRY_SUCCESSFULL);

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
				enquirySingleResult.setAmount(txn.getOrderAmount().getAmount().toString());
	
				enquirySingleResult.setTxnType(txn.getTransactionType().name());
				enquirySingleResult.setMerchantRefundTxId(txn.getMerchantRefundTxId());
				enquirySingleResult.setMerchantTxnId(txn.getMerchantTxId());
				
				updatePaymentDetailAndAddressDetail(txn, enquirySingleResult, pg);
				enquiryResult.add(enquirySingleResult);
	
			}
		}
		enquiryResultList.setEnquiryResultList(enquiryResult);
		enquiryResponse.setData(enquiryResultList);
				
	}

	/**
	 * This function validates the merchantAccessKey and transactionId for emptiness and null
	 * @param merchantAccessKey
	 * @param transactionId
	 * @return
	 */
	public boolean IsValidRequest(String merchantAccessKey, String transactionId)
	{
		if(CommonUtil.isEmpty(merchantAccessKey) || CommonUtil.isEmpty(transactionId)){
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * This function fills the enquiry response in case of error with response code and response message and return the response.
	 * @param errorCode
	 * @param errorMessage
	 * @param enquiryResponse
	 */
	public void handleValidationError(int errorCode, String errorMessage,EnquiryResponse enquiryResponse) {
		enquiryResponse.setRespCode(errorCode);
		enquiryResponse.setRespMsg(errorMessage);
	}
	
	/**
	 * This function decides whether the enquiry data can be given from Citrus DB or External PG based on various factors example . if it's previous day transaction and enquiry is done
	 * or not. If the status of the transaction is FAIL,FORWARD or SESSION_EXPIRED then checks for the date interval. if the enquiry comes before we do an enquiry with the PG
	 * then this function returns false and then corresponding PG's enquiry call is performed.
	 * @param txn
	 * @param dailySettlementTime
	 * @param dailySettlementMin
	 * @param pg
	 * @return
	 */
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

	/**
	 * This function fills the enquiry response with the payment details like card type,masked card number,mobile number etc
	 * @param txn
	 * @param enquiryResult
	 * @param pg
	 */
	public void updatePaymentDetailAndAddressDetail(Transaction txn, EnquiryResult enquiryResult,PaymentGateway pg) {

		if (CommonUtil.isNotNull(txn) && isRequiredStatus(txn)) {
			if (CommonUtil.isNotNull(txn.getTxnGateway())) {
				String transactionGatewayName = pg.getName().toString();
				enquiryResult.setTxnGateway(transactionGatewayName);

				if (CommonUtil.isNotNull(txn.getPaymentDetails())) {

					if (txn.getPaymentDetails() instanceof CreditCardPaymentDetail) {
						CreditCardPaymentDetail paymntDetails = (CreditCardPaymentDetail) txn
								.getPaymentDetails();
						ConsumerPaymentDetail conPaymntDetails = PaymentUtil
								.getPaymentDetailsForResponse(paymntDetails);

						enquiryResult.setMaskedCardNumber(conPaymntDetails
								.getMaskedCardNumber());
						enquiryResult.setCardType(paymntDetails.getCardType());
					} else if (txn.getPaymentDetails() instanceof DebitCardPaymentDetail) {
						DebitCardPaymentDetail paymntDetails = (DebitCardPaymentDetail) txn
								.getPaymentDetails();

						ConsumerPaymentDetail conPaymntDetails = PaymentUtil
								.getPaymentDetailsForResponse(paymntDetails);

						enquiryResult.setMaskedCardNumber(conPaymntDetails
								.getMaskedCardNumber());
						enquiryResult.setCardType(paymntDetails.getCardType());
					} else if (txn.getPaymentDetails() instanceof NetBankingPaymentDetail) {
						NetBankingPaymentDetail paymntDetails = (NetBankingPaymentDetail) txn
								.getPaymentDetails();
						enquiryResult.setIssuerCode(paymntDetails.getBank().getCode());

					} else if (txn.getPaymentDetails() instanceof ImpsPaymentDetail) {
						ImpsPaymentDetail paymntDetails = (ImpsPaymentDetail) txn
								.getPaymentDetails();

						ConsumerPaymentDetail conPaymntDetails = PaymentUtil
								.getPaymentDetailsForResponse(paymntDetails);

						enquiryResult.setImpsMmid(conPaymntDetails.getMmid());
						enquiryResult.setImpsMobileNumber(conPaymntDetails
								.getMobileNumber());
					}
					enquiryResult.setPaymentMode(txn.getPaymentDetails()
							.getPaymentMode().toString());
				}
			}
		}


		// update currency
		enquiryResult.setCurrency(txn.getOrderAmount().getCurrency());

		// update Pricing Transaction History if present
		updatePricingTransactionHistory(txn, enquiryResult);
		
		addAddressDetails(enquiryResult, txn);

	}
	
	/**
	 * This function fills the enquiry response with the pricing details like coupon code , offer type and rule name
	 * @param txn
	 * @param enquiryResult
	 */
	public void updatePricingTransactionHistory(Transaction txn,
			EnquiryResult enquiryResult) {
		if (CommonUtil.isNotNull(txn)
				&& CommonUtil.isNotNull(txn.getPricingTransactionHistory())) {
			enquiryResult.setOriginalAmount(CommonUtil.isNotNull(txn
					.getPricingTransactionHistory().getOriginalAmount()) ? txn
					.getPricingTransactionHistory().getOriginalAmount()
					.toString() : null);
			enquiryResult.setAdjustment(CommonUtil.isNotNull(txn
					.getPricingTransactionHistory().getAdjustment()) ? txn
					.getPricingTransactionHistory().getAdjustment().toString()
					: null);
			String ruleName = null;
			String couponCode = null;
			String ruleType = null;
			boolean isCitrusSponsored = Boolean.FALSE;
			if (CommonUtil.isNotNull(txn.getPricingTransactionHistory()
					.getPricingRule())) {
				ruleName = txn.getPricingTransactionHistory().getPricingRule()
						.getName();
				couponCode = txn.getPricingTransactionHistory().getPricingRule().getIsCoupon() == 1 ? ruleName : "";
				ruleType = txn.getPricingTransactionHistory().getPricingRule()
						.getOfferType().getDisplayLabel();
				if (CommonUtil.ONE == txn.getPricingTransactionHistory()
						.getPricingRule().getIsCitrusSponsored()) {
					isCitrusSponsored = Boolean.TRUE;
				}
			}
			enquiryResult.setRuleName(ruleName);
			//currently the rule name and the coupon code are one and the same
			enquiryResult.setCouponCode(couponCode);
			enquiryResult.setOfferType(ruleType);

			enquiryResult.setTransactionAmount((CommonUtil.isNotNull(txn
					.getPricingTransactionHistory().getTxnAmount())) ? txn
					.getPricingTransactionHistory().getTxnAmount().toString()
					: null);
			enquiryResult.setAmount(isCitrusSponsored ? txn
					.getPricingTransactionHistory().getOriginalAmount()
					.toString() : enquiryResult.getAmount());
		}
	}

	/**
	 * This function fills the enquiry response with consumer address.
	 * @param enquiryResult
	 * @param transaction
	 */

	private void addAddressDetails(EnquiryResult enquiryResult,
			Transaction transaction) {
		if (CommonUtil.isNotNull(transaction)
				&& CommonUtil.isNotNull(transaction.getConsumerDetail())) {
			Address addr = transaction.getConsumerDetail().getContactAddress();
			if (CommonUtil.isNotNull(addr)) {
				enquiryResult.setAddress(addr.getAddressStreet1(),
						addr.getAddressStreet2(), addr.getAddressCity(),
						addr.getAddressState(), addr.getAddressCountry(),
						addr.getAddressZip());
			}
		}
	}

	/**
	 * This function checks if status of the transaction is CANCELED or SESSION_EXPIRED or not. if it is CANCELED or SESSION_EXPIRED then data is not set in the response.
	 * @param txn
	 * @return
	 */
	public boolean isRequiredStatus(Transaction txn) {
		Boolean isRequiredStatus = Boolean.TRUE;
		if (CommonUtil.isNotNull(txn.getStatus())) {
			if (TransactionStatus.CANCELED.equals(txn.getStatus())
					|| TransactionStatus.SESSION_EXPIRED
							.equals(txn.getStatus())) {
				isRequiredStatus = Boolean.FALSE;
			}
		}
		return isRequiredStatus;
	}

	

	
	
}
