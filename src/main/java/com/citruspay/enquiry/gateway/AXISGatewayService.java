package com.citruspay.enquiry.gateway;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
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

public class AXISGatewayService extends GatewayServiceImpl implements
		GatewayService {
	private static final Logger log = LoggerFactory
			.getLogger(AXISGatewayService.class);


	private String pgURL =AppConfigManager.INSTANCE.getAppConfig().getPropertiesWithPrefix("axis.enquiry.refund").getProperty("url"); 
 

	@Override
	public EnquiryResponse enquiry(Transaction transaction,
			String merchantRefundTxId, PaymentGateway paymentGateway) {

		EnquiryResponse enquiryResponse = new EnquiryResponse();

		if (CommonUtil.isNull(transaction)) {
			enquiryResponse.setRespCode(RESP_CODE_SUCCESS);
			enquiryResponse.setRespMsg(ENQUIRY_NOT_PRESENT);
			return enquiryResponse;

		}

		List<EnquiryResult> inqueryResult = new ArrayList<EnquiryResult>();
		try {

			log.info("AXIS PG enquiry request for transaction: "
					+ transaction.toString());

			// 2. Get PG detail

			// 3. Get merchant credentials
			MerchantGatewaySettingDAO merchantGatewaySettingDAO = new MerchantGatewaySettingDAOImpl();
			PGCatCredentialDAO pgCatCredentialDAO = new PGCatCredentialDAOImpl();
			PGCatCredential pgCatCredential = new PGCategoryCredentialResolver(merchantGatewaySettingDAO,pgCatCredentialDAO).getPGCategoryCredential(transaction);

			// retrieve all the request parameters into a hash map
			Map<String, String> requestFields = getEnquiryRequestParameterList(
					transaction, pgCatCredential.getMid(),
					pgCatCredential.getAccessCode(), VPC_ENQUIRY_COMMAND,
					pgCatCredential.getAmausrid(),
					pgCatCredential.getAmapswd(),
					paymentGateway.getGatewayType(), null);
			
			ArrayList<String> parmasNotToLog = new ArrayList<String>();
			parmasNotToLog.add("vpc_Password");
			log.info("AXIS PG enquiry request parameter for mtx: "
					+ transaction.getMerchantTxId()
					+ " :"
					+ AxisUtil.requestParamListForLogging(requestFields,
							parmasNotToLog));

			// create the post data string to send
			String postData = AxisUtil.createPostDataFromMap(requestFields);

			String resQS = "";
			String message = "";

			try {
				// create a URL connection to the Virtual Payment Client
				resQS = new AXISGatewayConnector().connect(pgURL, postData, null);

			} catch (Exception ex) {
				// The response is an error message so generate an Error Page
				message = ex.toString();
			} // try-catch

			// create a hash map for the response data
			// Extract the available receipt fields from the VPC Response
			// If not present then let the value be equal to 'No Value returned'
			// Not all data fields will return values for all transactions.

			Map<String, String> responseMap = AxisUtil
					.createMapFromResponse(resQS);

			log.info("AXIS PG enquiry response map for mtx: "
					+ transaction.getMerchantTxId() + " :" + responseMap);

			if (message.length() == 0) {
				message = null2unknown("vpc_Message", responseMap);
			}

			String txnResponseCode = null2unknown("vpc_TxnResponseCode",
					responseMap);
			if (txnResponseCode.equals(AXIS_PG_WRONG_CREDENTIALS_RETURN_CODE)) {
				enquiryResponse.setRespCode(BAD_REQUEST);
				enquiryResponse.setRespMsg(message);
			}

			String drExists = null2unknown("vpc_DRExists", responseMap);

			// 4. Fetch transactions from citrus DB
			TransactionDAO transactionDAO = new TransactionDAOImpl();

			List<Transaction> transactions = transactionDAO
					.findByMerchantTxnIdAndGateway(
							transaction.getMerchantTxId(),
							transaction.getMerchant(), paymentGateway.getCode());

			/* 5. Prepare response */
			if (inqueryResult.isEmpty()) {
				// 5.1 Empty list
				if ((!CommonUtil.isNotEmpty(transactions) || transactions
						.size() == 1) && drExists.equalsIgnoreCase("Y")) {
					if (isRefundableTransaction(transaction)) {
						EnquiryResult inquiryBean = prepareEnquiryResult(
								responseMap, transaction,paymentGateway);
						updatePaymentDetailAndAddressDetail(transaction, inquiryBean,paymentGateway);
						inqueryResult.add(inquiryBean);
					}
				} else {
					for (Transaction tx : transactions) {

						// Update transaction amount
						tx = null;//TODO indra updateTransactionAmount(tx);

						EnquiryResult bean = createInquiryBean(tx, null,paymentGateway);

						bean.setTxnId((paymentGateway.getGatewayType()
								.equals(GatewayType.INTERNAL)) ? tx.getTxId()
								: tx.getMerchantTxId());
						inqueryResult.add(bean);
						log.info("Enquiry API response for mtx: "
								+ transaction.getMerchantTxId() + " code: "
								+ bean.getRespCode() + " msg: "
								+ bean.getRespMsg());
					}
				}
			}

			enquiryResponse.setRespCode(RESP_CODE_SUCCESS);
			enquiryResponse.setRespMsg(ENQUIRY_SUCCESSFULL);

			
			EnquiryResultList txnEnquiryResponse = fillEnquiryResponse(
					merchantRefundTxId, inqueryResult);
			
			enquiryResponse.setData(txnEnquiryResponse);
			return enquiryResponse;

		} catch (Exception ex) {
			log.error("Error occurred during  enquiry  for  AXIS gateway:"
					+ transaction.getMerchant().getName() + "for id:"
					+ transaction.getMerchantTxId(), ex);
			enquiryResponse.setRespCode(ERROR_CODE_502);
			enquiryResponse.setRespMsg(ex.getMessage());

		}
		return enquiryResponse;


	}
	// Get input param list in Map
	private Map<String, String> getEnquiryRequestParameterList(Transaction txn,
			String merchantId, String accessCode, String vpcCommand,
			String amaUsername, String amaPassword, GatewayType gatewayType, String txId) {
		Map<String, String> fields = new HashMap<String, String>();

		fields.put("vpc_Version", VPC_VERSION);
		fields.put("vpc_Command", vpcCommand);
		fields.put("vpc_AccessCode", accessCode);
		fields.put("vpc_Merchant", merchantId);
		if (!CommonUtil.isEmpty(txId)) {//txId not null only for hsitory enquiry.
			fields.put("vpc_MerchTxnRef", txId);
		} else {
			fields.put(
					"vpc_MerchTxnRef",
					gatewayType.equals(GatewayType.INTERNAL) ? txn.getTxId() : txn
							.getMerchantTxId());
		}
		fields.put("vpc_User", amaUsername);
		fields.put("vpc_Password", amaPassword);

		return fields;
	}


	private EnquiryResult prepareEnquiryResult(Map<String, String> enquiryResponse,
			Transaction txn,PaymentGateway pg) {
		EnquiryResult bean = new EnquiryResult();


		boolean isenquiryResponseNull = CommonUtil.isNull(enquiryResponse);
		int pgResponseCode = 1;// default failure.
		pgResponseCode = CommonUtil.getInteger(isenquiryResponseNull ? null
				: enquiryResponse.get("vpc_TxnResponseCode"), -1);

		PGTransaction pgTxn = txn.getPgTxResp();

		String txn_amount = !isenquiryResponseNull ? enquiryResponse
				.get("vpc_Amount") : null;
		if (txn_amount != null && !txn_amount.equals("0")) {
			BigDecimal amount = BigDecimal.valueOf(
					Long.valueOf(txn_amount).longValue()).divide(
					BigDecimal.valueOf(CommonUtil.HUNDRED));
			amount = amount.setScale(CommonUtil.DECIMAL_PLACES);
			txn_amount = amount.toString();
		} else {
			txn_amount = txn.getOrderAmount().getAmount().toString();
		}

		String recieptNo = (!isenquiryResponseNull && enquiryResponse
				.get("vpc_ReceiptNo") != null) ? enquiryResponse
				.get("vpc_ReceiptNo") : pgTxn != null ? pgTxn.getIssuerRefNo()
				: null;

		String bank_txn_id = (!isenquiryResponseNull && enquiryResponse
				.get("vpc_TransactionNo") != null) ? enquiryResponse
				.get("vpc_TransactionNo") : pgTxn != null ? pgTxn.getPgTxnId()
				: null;
		String authIdCode = (!isenquiryResponseNull
				&& enquiryResponse.get("vpc_AuthorizeId") != null && !enquiryResponse
				.get("vpc_AuthorizeId").equalsIgnoreCase("null")) ? enquiryResponse
				.get("vpc_AuthorizeId") : pgTxn != null ? pgTxn.getAuthIdCode()
				: null;
		String txn_date = JDateUtil.getDateStringInIST(txn.getLastModified());

		bean.setAuthIdCode(authIdCode);
		if (pg.getGatewayType().equals(GatewayType.INTERNAL)) {
			bean.setTxnId(txn.getTxId());
		} else {
			bean.setTxnId(txn.getMerchantTxId());
		}

		bean.setPgTxnId(bank_txn_id);
		bean.setRRN(recieptNo);
		bean.setTxnType(txn.getTransactionType().name());
		bean.setTxnDateTime(txn_date);
		bean.setAmount(txn_amount);
		//set merchantRefundTxId from transaction
		bean.setMerchantRefundTxId(txn.getMerchantRefundTxId());
		// set MTX
		bean.setMerchantTxnId(txn.getMerchantTxId());
		String respMsg = "";

		if (pgResponseCode != -1) {
			//TODO indra txn = updateCPTransactionStatus(pgResponseCode, bean, txn);
			bean.setAmount(txn.getOrderAmount().getAmount().toString());
		}

		String respCode = "0";
		if (!TransactionStatus.SUCCESS_ON_VERIFICATION.equals(txn.getStatus())) {
			respCode = String.valueOf(txn.getStatus().ordinal());
		}

		if (txn.getStatus().equals(TransactionStatus.SUCCESS)
				|| txn.getStatus().equals(
						TransactionStatus.SUCCESS_ON_VERIFICATION)) {
			if (CommonUtil.isNotNull(txn.getTransactionType())
					&& txn.getTransactionType()
							.toString()
							.equalsIgnoreCase(
									TransactionType.PREAUTH.toString())) {
				respMsg = TXN_SEARCH_APPROVED;
			} else {
				respMsg = txn.getStatus().getDisplayMsg();
			}
		} else {
			respMsg = txn.getStatus().getDisplayMsg();
		}

		bean.setRespCode(respCode);
		bean.setRespMsg(respMsg);
		
		updateInquiryForCardParamater(bean,txn);

		return bean;
	}

}
