package com.citruspay.enquiry.gateway;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.citruspay.CommonUtil;
import com.citruspay.JDateUtil;
import com.citruspay.enquiry.GatewayServiceImpl;
import com.citruspay.enquiry.api.EnquiryResponse;
import com.citruspay.enquiry.api.EnquiryResult;
import com.citruspay.enquiry.api.EnquiryResultList;
import com.citruspay.enquiry.configuration.AppConfigManager;
import com.citruspay.enquiry.persistence.entity.PGCatCredential;
import com.citruspay.enquiry.persistence.entity.PGCategoryCredentialResolver;
import com.citruspay.enquiry.persistence.entity.PGTransaction;
import com.citruspay.enquiry.persistence.entity.PaymentGateway;
import com.citruspay.enquiry.persistence.entity.Transaction;
import com.citruspay.enquiry.persistence.entity.TransactionStatus;
import com.citruspay.enquiry.persistence.entity.TransactionType;
import com.citruspay.enquiry.persistence.implementation.MerchantGatewaySettingDAOImpl;
import com.citruspay.enquiry.persistence.implementation.PGCatCredentialDAOImpl;
import com.citruspay.enquiry.persistence.implementation.TransactionDAOImpl;
import com.citruspay.enquiry.persistence.interfaces.MerchantGatewaySettingDAO;
import com.citruspay.enquiry.persistence.interfaces.PGCatCredentialDAO;
import com.citruspay.enquiry.persistence.interfaces.TransactionDAO;
import com.citruspay.enquiry.type.GatewayType;

public class BillDeskGatewayService extends GatewayServiceImpl implements
		GatewayService {
	private static final Logger LOGGER = LoggerFactory.getLogger(BillDeskGatewayService.class);
	public static final String pgEnquiryURL = AppConfigManager.INSTANCE.getAppConfig().getPropertiesWithPrefix("billdesk.enquiry").getProperty("url");


	@Override
	public EnquiryResponse enquiry(Transaction transaction,
			String merchantRefundTxId, PaymentGateway paymentGateway) {

		// 1. If transaction is null return
		EnquiryResponse enquiryResponse = new EnquiryResponse();

		if (CommonUtil.isNull(transaction)) {
			enquiryResponse.setRespCode(RESP_CODE_SUCCESS);
			enquiryResponse.setRespMsg(ENQUIRY_NOT_PRESENT);
			return enquiryResponse;

		}

		List<EnquiryResult> inqueryResult = new ArrayList<EnquiryResult>();
		try {

			LOGGER.info("BillDesk PG enquiry request for transaction: "+ transaction.toString());


			// 3. Get merchant credentials
			MerchantGatewaySettingDAO merchantGatewaySettingDAO = new MerchantGatewaySettingDAOImpl();
			PGCatCredentialDAO pgCatCredentialDAO = new PGCatCredentialDAOImpl();

			PGCatCredential pgCatCredential = new PGCategoryCredentialResolver(merchantGatewaySettingDAO,pgCatCredentialDAO).getPGCategoryCredential(transaction);

			Map<String, String> requestFields = new BillDeskUtil().getEnquiryRequestParameterList(transaction, pgCatCredential);
			
			LOGGER.info("Bill Desk PG enquiry history request parameter for mtx: "
					+ transaction.getMerchantTxId()
					+ " :"
					+ requestFields.get("msg"));
			
			String postData = createPostDataFromMap(requestFields);
			
			String resQS = "";
			String message = "";

			try {
				// create a URL connection to the Virtual Payment Client
				resQS = new BillDeskGatewayConnector().connect(pgEnquiryURL, postData);

			} catch (Exception ex) {
				// The response is an error message so generate an Error Page
				message = ex.toString();
				LOGGER.error(message);
			} // try-catch

			Map<String, String> responseMap = new BillDeskUtil().createMapFromResponse(resQS);
			
			LOGGER.info("Bill Desk PG enquiry response map for mtx: "
					+ transaction.getMerchantTxId() + " :" + responseMap);
			
			/*
			 * 5. Fetch all transactions for MTX from Citrus DB first with
			 * external PG code , if no result found do the step with internal
			 * pg code
			 */
			TransactionDAO transactionDAO = new TransactionDAOImpl();

			List<Transaction> transactions = transactionDAO
					.findByMerchantTxnIdAndGateway(
							transaction.getMerchantTxId(),
							transaction.getMerchant(), paymentGateway.getCode());

			/* 6. Prepare response */
			if (responseMap != null
					&& "0".equals(responseMap.get("pgRespCode")) && "Y".equalsIgnoreCase(responseMap.get("queryStatus"))) {
				// Check if checksum verified and valid query status returned
				if (inqueryResult.isEmpty()) {
					// 6.1 Empty list
					if (!CommonUtil.isNotEmpty(transactions)
							|| transactions.size() == 1) {
						if (isRefundableTransaction(transaction)) {

							EnquiryResult inquiryBean = prepareInquiryBean(responseMap, transaction);
							inqueryResult.add(inquiryBean);
						}
					} else {
						for (Transaction tx : transactions) {

							// Update transaction amount
							tx = null;//TODO indra updateTransactionAmount(tx);

							EnquiryResult bean = createInquiryBean(tx, null,paymentGateway);

							bean.setTxnId((paymentGateway.getGatewayType().equals(GatewayType.INTERNAL)) ? tx.getTxId() : tx.getMerchantTxId());
							
							inqueryResult.add(bean);
							LOGGER.info("Enquiry API response for mtx: "
									+ transaction.getMerchantTxId() + " code: "
									+ bean.getRespCode() + " msg: "
									+ bean.getRespMsg());
						}
					}
				}
				enquiryResponse.setRespCode(RESP_CODE_SUCCESS);
				enquiryResponse.setRespMsg(ENQUIRY_SUCCESSFULL);

				
				EnquiryResultList txnEnquiryResponse = fillEnquiryResponse(merchantRefundTxId, inqueryResult);
				
				enquiryResponse.setData(txnEnquiryResponse);
				return enquiryResponse;
			} else {
				// Invalid checksum
				enquiryResponse.setRespCode(RESP_CODE_SUCCESS);
				enquiryResponse.setRespMsg(ENQUIRY_SUCCESSFULL);
				
				return enquiryResponse;
			}
			
			
		} catch (Exception ex) {
			LOGGER.error(
					"Error occurred during  enquiry  for  Bill Desk gateway:"
							+ transaction.getMerchant().getName() + "for id:"
							+ transaction.getMerchantTxId(), ex);
			enquiryResponse.setRespCode(ERROR_CODE_502);
			enquiryResponse.setRespMsg(ex.getMessage());
			return enquiryResponse;

		}

	
	}
	/**
	 * Create the InquiryBean from enquiry response map. If required parameters
	 * are found in map uses it otherwise gets it from transaction.
	 * 
	 * @param enquiryResponse
	 * @param txn
	 * @return
	 */
	private EnquiryResult prepareInquiryBean(Map<String, String> enquiryResponse,
			Transaction txn) {
		
		LOGGER.info("BillDesk PG : prepareInquiryBean method START of BillDeskGatewayService");
		
		String respMsg = "";
		EnquiryResult bean = new EnquiryResult();
		boolean isenquiryResponseNull = CommonUtil.isNull(enquiryResponse);
		PGTransaction pgTxn = txn.getPgTxResp();

		int pgResponseCode = 1;// default failure.
		pgResponseCode = Integer.valueOf(enquiryResponse.get("pgRespCode"));
		
		// setting amount
		String txnAmount = !isenquiryResponseNull ? enquiryResponse.get("txnAmount") : null;
		if (txnAmount == null || "0".equals(txnAmount)) {
			txnAmount = txn.getOrderAmount().getAmount().toString();
		}
		bean.setAmount(txnAmount);
		
		String issuerRefNo = enquiryResponse.get("IssuerRefNo");
		// setting receiptNo
		String recieptNo = (!isenquiryResponseNull && issuerRefNo != null) ? issuerRefNo : 
									(pgTxn != null ? pgTxn.getIssuerRefNo() : null);
		bean.setRRN(recieptNo);
		
		// setting authidcode
		String authIdCode = (!isenquiryResponseNull	&& !CommonUtil.isEmpty(issuerRefNo) && !"null".equalsIgnoreCase(issuerRefNo)) ? 
								issuerRefNo : (pgTxn != null ? pgTxn.getAuthIdCode() : null);
		bean.setAuthIdCode(authIdCode);
		
		// setting pgTxnId
		String pgTxnId = (!isenquiryResponseNull && enquiryResponse.get("PGTxnRefNo") != null) ? 
							enquiryResponse.get("PGTxnRefNo") : 
								(pgTxn != null ? pgTxn.getPgTxnId()	: null);
		bean.setPgTxnId(pgTxnId);
		
		bean.setTxnId(txn.getMerchantTxId());
		bean.setMerchantTxnId(txn.getMerchantTxId());
		bean.setMerchantRefundTxId(txn.getMerchantRefundTxId());
		bean.setTxnType(txn.getTransactionType().name());
		
		String txn_date = JDateUtil.getDateStringInIST(txn.getLastModified());
		bean.setTxnDateTime(txn_date);
		
		String totalRefundAmount = enquiryResponse.get("totalRefundAmount");
		if (totalRefundAmount == null || "0".equals(totalRefundAmount)) {
			totalRefundAmount = txn.getOrderAmount().getAmount().toString();
		}
		
		bean.setTotalRefundAmount(totalRefundAmount);
		
		txn = null;//TODO indra updateCPTransactionStatus(pgResponseCode, bean, txn, enquiryResponse.get("AuthStatus"), enquiryResponse.get("refundStatus"));
		bean.setAmount(txn.getOrderAmount().getAmount().toString());
		
		String respCode = "0";
		if (!TransactionStatus.SUCCESS_ON_VERIFICATION.equals(txn.getStatus())) {
			respCode = String.valueOf(txn.getStatus().ordinal());
		}
		
		if (txn.getStatus().equals(TransactionStatus.SUCCESS) || txn.getStatus().equals(TransactionStatus.SUCCESS_ON_VERIFICATION)) {
			
			if (CommonUtil.isNotNull(txn.getTransactionType()) && txn.getTransactionType().toString().equalsIgnoreCase(TransactionType.PREAUTH.toString())) {
				respMsg = TXN_SEARCH_APPROVED;
			} else {
				respMsg = txn.getStatus().getDisplayMsg();
			}
			
		} else {
			respMsg = txn.getStatus().getDisplayMsg();
		}

		// We get last refund Date as well in response. So not sure whether to store it anywhere
		
		bean.setRespCode(respCode);
		bean.setRespMsg(respMsg);
		
		LOGGER.info("BillDesk PG : prepareInquiryBean method END of BillDeskGatewayService");
		
		return bean;
	}
	
	/**
	 * This method is for creating a URL POST data string.
	 * 
	 * @param fields
	 *            is the input parameters from the order page
	 * @return is the output String containing POST data key value pairs
	 */
	@SuppressWarnings({ "rawtypes", "deprecation" })
	public static String createPostDataFromMap(Map<String, String> fields) {
		StringBuilder buf = new StringBuilder();

		String ampersand = "";
		for (Iterator i = fields.keySet().iterator(); i.hasNext();) {
			String key = (String) i.next();
			String value = fields.get(key);
			if (CommonUtil.isNotNull(value) && (value.length() > 0)) {
				// append the parameters
				buf.append(ampersand);
				buf.append(URLEncoder.encode(key));
				buf.append('=');
				buf.append(URLEncoder.encode(value));
			}
			ampersand = "&";
		}
		return buf.toString();
	}



}
