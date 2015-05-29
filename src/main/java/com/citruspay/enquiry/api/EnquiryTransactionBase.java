package com.citruspay.enquiry.api;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.citruspay.CommonUtil;
import com.citruspay.HMacUtil;
import com.citruspay.JDateUtil;
import com.citruspay.PaymentUtil;
import com.citruspay.enquiry.configuration.AppConfigManager;
import com.citruspay.enquiry.encryption.AESEncryptionDecryption;
import com.citruspay.enquiry.exceptions.EncryptionException;
import com.citruspay.enquiry.exceptions.SignatureValidationException;
import com.citruspay.enquiry.gateway.GatewayServiceFactory;
import com.citruspay.enquiry.persistence.entity.Address;
import com.citruspay.enquiry.persistence.entity.ConsumerPaymentDetail;
import com.citruspay.enquiry.persistence.entity.CreditCardPaymentDetail;
import com.citruspay.enquiry.persistence.entity.DebitCardPaymentDetail;
import com.citruspay.enquiry.persistence.entity.ImpsPaymentDetail;
import com.citruspay.enquiry.persistence.entity.Merchant;
import com.citruspay.enquiry.persistence.entity.MerchantKey;
import com.citruspay.enquiry.persistence.entity.NetBankingPaymentDetail;
import com.citruspay.enquiry.persistence.entity.PaymentGateway;
import com.citruspay.enquiry.persistence.entity.Transaction;
import com.citruspay.enquiry.persistence.entity.TransactionStatus;
import com.citruspay.enquiry.persistence.implementation.MerchantDAOImpl;
import com.citruspay.enquiry.persistence.implementation.MerchantKeyDAOImpl;
import com.citruspay.enquiry.persistence.implementation.PaymentGatewayDAOImpl;
import com.citruspay.enquiry.persistence.implementation.TransactionDAOImpl;
import com.citruspay.enquiry.persistence.interfaces.MerchantKeyDAO;
import com.citruspay.enquiry.persistence.interfaces.PaymentGatewayDAO;
import com.citruspay.enquiry.persistence.interfaces.TransactionDAO;
import com.citruspay.enquiry.persistence.util.KeyType;

public class EnquiryTransactionBase {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(EnquiryTransactionBase.class);

	private static final int ERROR_CODE_404 = 404;
	public static final int RESP_CODE_SUCCESS = 200;
	public static final int BAD_REQUEST = 400;
	private static final String BAD_REQUEST_MSG = "Merchant mandatory parameter missing";
	private static final String MERCHANT_NOT_FOUND = "Bad Request: Merchant not found";
	private static final String NO_TXN_FOUND = "No transaction found";
	private static final String ENQUIRY_SUCCESSFULL = "Enquiry successful";
	private static final String ENQUIRY_FAILURE = "Enquiry failure";
	private static final String NO_REFUND_TXN_FOUND = "Refund Transaction does not exist";
	private static final int ERROR_CODE_401 = 401;
	private static final String ENC_ERROR = "Bad Request:Invalid signature key";


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
			String signature =  enquiryRequest.getSignature();

			if (!IsValidRequest(merchantAccessKey, transactionId,signature)) {
				LOGGER.error("Enquiry API : Mandatory request parameter missing for enquiry id: "+ transactionId);
				handleValidationError(BAD_REQUEST, BAD_REQUEST_MSG, enquiryResponse);
				return enquiryResponse;
				
			}
			// Find merchant . if it's not valid merchant then fill the response with error code and message as merchant not found and return
			Merchant merchant = new MerchantDAOImpl()
					.findBySecretId(merchantAccessKey);

			if (merchant == null) {
				LOGGER.error("Enquiry API : Merchant not found for enquiry request id:"
						+ transactionId);
				handleValidationError(ERROR_CODE_404, MERCHANT_NOT_FOUND,enquiryResponse);
				return enquiryResponse;
			}
			//validate signature
			validateSignature(prepareRequestData(merchantAccessKey,transactionId),signature,merchant);

			
			// Get last modified transaction's pg for enquiry call
			TransactionDAO transactionDAO = new TransactionDAOImpl();
			Transaction txn = transactionDAO.getLastTxnByMtxAndMerchant(transactionId, merchant);
			if (CommonUtil.isNull(txn)) {
				LOGGER.error("Enquiry API : Txn not found for given txn id in citrus system:"
						+ merchant.getName() + ":" + transactionId);
				handleValidationError(RESP_CODE_SUCCESS, NO_TXN_FOUND, enquiryResponse);
				return enquiryResponse;
			}
			
			// validate merchant refund txnid given in the request.
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
					
			//check if we can get data from our DB rather than calling enquiry for External PG's. The criterial for fetching data from our DB depends upon various factors such
			// as if the transaction creation data and enquiry is on the same day. If the status of the transaction is success or success_on_verification. If the transaction's status
			// is on HOLD etc.
		/*	if(inquiryRespFromCitrusDB(txn) == true)
			{
				LOGGER.info("Enquiry API : Getting enquiry data from Citrus DB");
				populateEnquiryResponse(enquiryRequest,enquiryResponse,txn,pg,enquiryRequest.getMerchantRefundTxId(),transactionDAO);
			}
			else
		*/	{
				//TODO call corresponding PG's enquiry , parse the response and fill the response and return;
				enquiryResponse = new GatewayServiceFactory().getGatewayService(txn.getTxnGateway()).enquiry(txn, merchantRefundTxId,pg);
			}

			return enquiryResponse;
		
		}catch(SignatureValidationException signatureException){
			LOGGER.error("Signature validation failed",signatureException.getMessage(),signatureException);
			enquiryResponse.setRespCode(ERROR_CODE_401);
			enquiryResponse.setRespMsg(ENC_ERROR);
			
		}catch (Exception ex) {
			LOGGER.error(
					"Enquiry API : Exception during enquiry API call due to : "
							+ ex.getMessage(), ex);
		}
		LOGGER.info("Enquiry API : exiting from function enquiry");

		return enquiryResponse;
	}
	
	/**
	 * This function validates the signature
	 * @param requestData
	 * @param signature
	 * @param merchant
	 * @throws SignatureValidationException
	 */
	protected void validateSignature(String requestData,String signature,Merchant merchant) throws SignatureValidationException {
		
		LOGGER.info("Validating signature");
		
		try {
			
			String data = requestData;
			// get merchant key from the table
			MerchantKey merKey = getMerchantHMACKey(merchant);
			
			if(merKey!=null)
			{
	
				String keyString = merKey.getKeyString();
	
				AESEncryptionDecryption aesEncryptionDecryption = new AESEncryptionDecryption();
				String decrypt = aesEncryptionDecryption.decrypt(keyString);
				String hmac= "";
	
				hmac = HMacUtil.generateHMAC(data, decrypt);
				
				LOGGER.info(String.format("Generated HMAC [%s], Merchant Signature[%s]", hmac, signature));
				
				if(!hmac.equalsIgnoreCase(signature)) {
					throw new SignatureValidationException();
				}
			}
				
		} catch (EncryptionException e) {
			LOGGER.error("Signature was not validated", e);
			throw new SignatureValidationException();
		} catch (GeneralSecurityException e) {
			LOGGER.error("Signature was not validated", e);
			throw new SignatureValidationException();
		}
		LOGGER.info("Completed signature validation");
		
	}



	/**
	 * This function prepares  enquiry response with required details.
	 * @param enquiryRequest
	 * @param enquiryResponse
	 * @param transaction
	 * @param pg
	 * @param merchantRefundTxId
	 * @param transactionDAO
	 */
	private void populateEnquiryResponse(EnquiryRequest enquiryRequest,EnquiryResponse enquiryResponse,Transaction transaction,PaymentGateway pg,String merchantRefundTxId,TransactionDAO transactionDAO)
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
				if(CommonUtil.isNotNull(enquiryRequest.isPaymentDetailsRequired()) && enquiryRequest.isPaymentDetailsRequired()) {
					updatePaymentDetails(txn, enquirySingleResult, pg);
				}
				if(CommonUtil.isNotNull(enquiryRequest.isPricingDetailsRequired()) && enquiryRequest.isPricingDetailsRequired()) {

					updatePricingDetails(txn, enquirySingleResult);
				}
				if(CommonUtil.isNotNull(enquiryRequest.isAddressDetailsRequired()) && enquiryRequest.isAddressDetailsRequired()) {
					LOGGER.info("address is not null value="+enquiryRequest.isAddressDetailsRequired());

					addAddressDetails(enquirySingleResult, txn);
				}

				enquiryResult.add(enquirySingleResult);
	
			}
		}
		else {
				enquiryResponse.setRespCode(RESP_CODE_SUCCESS);
				enquiryResponse.setRespMsg(ENQUIRY_FAILURE);
				return;
			
		}
		enquiryResultList.setEnquiryResultList(enquiryResult);
		enquiryResponse.setData(enquiryResultList);
				
	}


	/**
	 * This function validates the merchantAccessKey,transactionId  and signature for emptiness and null
	 * @param merchantAccessKey
	 * @param transactionId
	 * @param signature
	 * @return
	 */
	public boolean IsValidRequest(String merchantAccessKey, String transactionId,String signature)
	{
		if(CommonUtil.isEmpty(merchantAccessKey) || CommonUtil.isEmpty(transactionId) || CommonUtil.isEmpty(signature)){
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
	 * @return
	 */
	public static boolean inquiryRespFromCitrusDB(Transaction txn) {

		// display result from citrus DB if transaction status is ON_HOLD
		if (txn.getPaymentDetails() != null && CommonUtil.isCreditOrDebitCard(txn.getPaymentDetails().getPaymentMode())&& TransactionStatus.ON_HOLD.equals(txn.getStatus())) {
			return true;
		}
		if(TransactionStatus.SUCCESS.equals(txn.getStatus()) || TransactionStatus.SUCCESS_ON_VERIFICATION.equals(txn.getStatus())) {
			LOGGER.info("Transaction status is SUCCESS or SUCCESS_ON_VERIFICATION so enquirying from Citrus DB");
			return true;
		}

		
		Interval lastDay = JDateUtil.getISTPreviousDay();
		DateTime txnCreatedDateDateTime = JDateUtil
				.getDateAndTime(txn.getCreated());
		
		DateTime currentDateTime = JDateUtil.getDateAndTime(new Date());

		// if transaction creation data and enquiry date is not on the same day i.e. more than a day old then take the data from our DB.
		if (txnCreatedDateDateTime.compareTo(lastDay.getStart()) < 0) {
			LOGGER.info("Transaction created is more than a day old so enquirying from Citrus DB TxnCreatedDateDateTime:"+txnCreatedDateDateTime+ " currenttime:"+currentDateTime+
					"last day start ="+lastDay.getStart());
			return true;
		}

		// if transaction creation data and enquiry date is on the same day i.e. txncreateddate is between last day end and current date 
		if ((txnCreatedDateDateTime.compareTo(lastDay.getEnd()) > 0) && (txnCreatedDateDateTime.compareTo(currentDateTime ) < 0 )) {
			LOGGER.info("Could not enquire from Citrus DB because of Txncreated date and enquiry request on same  day :: going to call PG txncreated date time:"+txnCreatedDateDateTime + "current date:"+currentDateTime);
			return false;
		}
		
		if( (txnCreatedDateDateTime.compareTo(lastDay.getStart()) > 0) && (txnCreatedDateDateTime.compareTo(lastDay.getEnd()) < 0))
		{
			LOGGER.info("Could not enquire from Citrus DB because of Txncreated date is less than a day old :: txncreated date time:"+txnCreatedDateDateTime + "current date:"+currentDateTime+"last day start ="+lastDay.getStart()+" last day end ="+lastDay.getEnd());
			return false;
		}


		LOGGER.debug("Could not enquire from Citrus DB::going to call PG txcreated date time:"+txnCreatedDateDateTime+ " currenttime:"+currentDateTime+"    last day start ="+lastDay.getStart()+" last day end ="+lastDay.getEnd());
		return false;
	}

	/**
	 * This function fills the enquiry response with the payment details like card type,masked card number,mobile number etc
	 * @param txn
	 * @param enquiryResult
	 * @param pg
	 */
	public void updatePaymentDetails(Transaction txn, EnquiryResult enquiryResult,PaymentGateway pg) {

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

	}
	
	/**
	 * This function fills the enquiry response with the pricing details like coupon code , offer type and rule name
	 * @param txn
	 * @param enquiryResult
	 */
	public void updatePricingDetails(Transaction txn,
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

/**	
 * This function generates the HMAC of a key
 * @param merchant
 * @return
 */
public MerchantKey getMerchantHMACKey(Merchant merchant) {
		List<MerchantKey> merchantKeys = getMerchantkeys(merchant);
		int size = merchantKeys.size();
		for (int i = 0; i < size; i++) {
			MerchantKey mk = merchantKeys.get(i);
			if (KeyType.HMAC.equals(mk.getKeyType())) {
				return mk;
			}
		}
		return null;
	}


/**
 * This function gets merchant key from the DB
 * @param merchant
 * @return
 */
	public List<MerchantKey> getMerchantkeys(Merchant merchant) {

		MerchantKeyDAO merchantKeyDAO = new MerchantKeyDAOImpl();

		List<MerchantKey> merchantKeys = merchantKeyDAO.findMerchantKeys(merchant);

		return merchantKeys;
	}

	/**
	 * This function prepares the request data which is to be used for validating the signature
	 * @param merchantAccessKey
	 * @param transactionId
	 * @return
	 */
	public String prepareRequestData(String merchantAccessKey, String transactionId) {
		StringBuilder req = new StringBuilder();
		req.append(kvPair("merchantAccessKey", merchantAccessKey));
		req.append("&");
		req.append(kvPair("transactionId", transactionId));
		return req.toString();
	}
	/**
	 * This function forms a key value pair for merchant access key and transaction id
	 * @param k
	 * @param v
	 * @return
	 */
	private String kvPair(String k, String v) {
		return String.format("%s=%s", new Object[] { k, v });
	}

}
