package com.citruspay.enquiry.gateway;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.citruspay.CommonUtil;
import com.citruspay.enquiry.GatewayServiceImpl;
import com.citruspay.enquiry.api.EnquiryResponse;


import com.citruspay.enquiry.api.EnquiryResult;
import com.citruspay.enquiry.api.EnquiryResultList;
import com.citruspay.enquiry.encryption.AESEncryptionDecryption;
import com.citruspay.enquiry.persistence.entity.MerchantGatewaySetting;
import com.citruspay.enquiry.persistence.entity.PGTransaction;
import com.citruspay.enquiry.persistence.entity.PaymentGateway;
import com.citruspay.enquiry.persistence.entity.Transaction;
import com.citruspay.enquiry.persistence.implementation.MerchantGatewaySettingDAOImpl;
import com.citruspay.enquiry.persistence.implementation.TransactionDAOImpl;
import com.citruspay.enquiry.persistence.interfaces.MerchantGatewaySettingDAO;
import com.citruspay.enquiry.persistence.interfaces.TransactionDAO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HDFC3DGatewayService extends GatewayServiceImpl implements GatewayService{

	private static final Logger log = LoggerFactory
			.getLogger(HDFC3DGatewayService.class);
	public static final String hdfcPGUrl ="https://securepgtest.fssnet.co.in/pgway/servlet/TranPortalXMLServlet"; 
	

	@Override
	public EnquiryResponse enquiry(Transaction transaction,
			String merchantRefundTxId,PaymentGateway paymentGateway) {

		EnquiryResponse enquiryResponse = new EnquiryResponse();
		// 1. If transaction is null return
		if (CommonUtil.isNull(transaction)) {
			
			enquiryResponse.setRespCode(RESP_CODE_SUCCESS);
			enquiryResponse.setRespMsg(ENQUIRY_NOT_PRESENT);
			return enquiryResponse;
		}

		// 2. Setup
		List<EnquiryResult> inqueryResult = new ArrayList<EnquiryResult>();
		boolean mtxflag = Boolean.TRUE;
		boolean isExternalPG = Boolean.TRUE;

		try {

			// 3. Get all transactions for MTX and merchant from Citrus DB
			/*
			 * First attempt is made with hdfc3D, if no transaction found then
			 * attempt is made with hfdcCitrus(internalpg)
			 */
			List<Transaction> transactions = null;
			MerchantGatewaySetting pgSetting = null;


			// Is PG external or internal
			isExternalPG = isExternalGateway(paymentGateway);

			// If external PG send MTX , if internal PG send CTX
			mtxflag = (isExternalPG) ? Boolean.TRUE : Boolean.FALSE;

			// Fetch transactions from citrus DB
			TransactionDAO transactionDAO = new TransactionDAOImpl();
			transactions = transactionDAO.findByMerchantTxnIdAndGateway(
					transaction.getMerchantTxId(), transaction.getMerchant(),
					paymentGateway.getCode());

			// Get PG setting
			MerchantGatewaySettingDAO gatewaySettingDAO = new MerchantGatewaySettingDAOImpl();
			pgSetting = gatewaySettingDAO.findByMerchantAndGatewayCode(transaction.getMerchant().getId(),
					paymentGateway.getCode());

			// 1. If transaction does not exist in citrus DB //TODO why are we reading here if we have already done an enquiry whether we need to get it from our DB using funtion inquiryRespFromCitrusDB
			if (!CommonUtil.isNotEmpty(transactions)) {
				enquiryResponse.setRespCode(RESP_CODE_SUCCESS);
				enquiryResponse.setRespMsg(ENQUIRY_NOT_PRESENT);
				return enquiryResponse;

			}

			
			// 2. Transaction exists in citrus system
			for (Transaction txn : transactions) {

				Boolean repeatEnquiry = Boolean.FALSE;
				String responseString = null;
				do {
					// return response from local db if pgTxnId is null
					if (CommonUtil.isNull(transaction.getPgTxResp())
							|| CommonUtil.isNull(transaction.getPgTxResp()
									.getPgTxnId())) {
						return enquiryResponse; /*TODOprepareEnqiryResult(transactions,
								merchantRefundTxId);
*/					}
					responseString = doinquiry(txn, pgSetting, mtxflag);

					/*
					 * If response is null then enquire with Citrus transaction
					 * id
					 */
					repeatEnquiry = (!repeatEnquiry && CommonUtil
							.isEmpty(responseString)) ? Boolean.TRUE
							: Boolean.FALSE;

				} while (repeatEnquiry);

				/** Process response */
				if (!CommonUtil.isEmpty(responseString.toString())) {
					log.info("Response received for enquiry: "
							+ responseString.toString());

					EnquiryResult enquiryResult = null;/* TODO indra parseEnquiryResponse(txn,
							responseString, txn.getMerchant(),
							txn.getMerchantTxId(), txn.getTxId(), mtxflag);
					*/
					enquiryResult = null;//TODO indra updateInquiryForCardParamater(enquiryResult,transaction);
					System.out.println("pg.getName().toString()="+paymentGateway.getName().toString());
					updatePaymentDetailAndAddressDetail(transaction, enquiryResult,paymentGateway);
					// Update status if required
					Transaction updatedTransaction = null;/* TODO indra updateCPTransactionStatus(
							enquiryResult, txn.getMerchantTxId(),
							txn.getMerchant(), txn.getTxId(),
							txn.getTransactionType());*/

					// Update result for transaction type
					enquiryResult.setTxnType(txn.getTransactionType().toString());

					// set merchant refund tx id
					enquiryResult.setMerchantRefundTxId(txn
							.getMerchantRefundTxId());
					// set MTX
					enquiryResult.setMerchantTxnId(txn.getMerchantTxId());

					// Update amount if not present in response
					if (CommonUtil.isNotNull(updatedTransaction)) {
						enquiryResult
								.setAmount(CommonUtil
										.isNotNull(updatedTransaction
												.getOrderAmount()) ? updatedTransaction
										.getOrderAmount().getAmount()
										.toString()
										: "");
					}

					// Update date if not present in response
					if (CommonUtil.isNull(enquiryResult.getTxnDateTime())) {
						enquiryResult.setTxnDateTime(txn.getCreated().toString());
					}

					inqueryResult.add(enquiryResult);

				}

			}

			// Response list is empty, send data from citrus DB
			if (!CommonUtil.isNotEmpty(inqueryResult)) {
				//TODO indra return prepareEnqiryResult(transactions, merchantRefundTxId);
			}


			enquiryResponse.setRespCode(RESP_CODE_SUCCESS);
			enquiryResponse.setRespMsg(ENQUIRY_SUCCESSFULL);

			EnquiryResultList txnEnquiryResponse = null;/* TODO indra getEnquiryResponse(
					merchantRefundTxId, inqueryResult);*/

			enquiryResponse.setData(txnEnquiryResponse);
			return enquiryResponse;

		} catch (Exception ex) {
			log.error("Error occurred during  enquiry  for  HDFC3D gateway:"
					+ transaction.getMerchant().getName() + "for id:"
					+ transaction.getMerchantTxId(), ex);
			enquiryResponse.setRespCode(ERROR_CODE_502);
			enquiryResponse.setRespMsg(ex.getMessage());
		}
		return enquiryResponse;

	}
	/**
	 * @param transaction
	 * @param pgSetting
	 * @param mtxflag
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	private String doinquiry(Transaction transaction,
			MerchantGatewaySetting pgSetting, boolean mtxflag)
			throws GeneralSecurityException, IOException {

		StringBuffer responseString = new StringBuffer();

		Map<String, String> parameterMap = new LinkedHashMap<String, String>();

		PGTransaction pgTxn = transaction.getPgTxResp();

		/** 1. Initialise map */
		parameterMap.put("id", pgSetting.getMerchantUsrid());
		parameterMap.put("action", INQUIRY_ACTION_CODE);
		parameterMap.put("trackid",(mtxflag) ? transaction.getMerchantTxId() : transaction.getTxId());
		parameterMap.put("transid",CommonUtil.isNotNull(pgTxn) ? pgTxn.getPgTxnId() : null);

		log.info("Enquiry request string (minus password): "+ CommonUtil.prepareXMLRequest(parameterMap));

		parameterMap.put("password",new AESEncryptionDecryption().decrypt(pgSetting.getMerchantPswd()));

		/** 2. Get request string */
		String requestString = CommonUtil.prepareXMLRequest(parameterMap);

		/** 3. Get inquiry response */
		URL url = new URL(hdfcPGUrl);
		URL newUrl = new URL("https", url.getHost(), url.getPort(),url.getFile());

		Object obj;
		obj = (HttpURLConnection) newUrl.openConnection();
		((URLConnection) obj).setDoInput(true);
		((URLConnection) obj).setDoOutput(true);
		((URLConnection) obj).setUseCaches(false);
		((URLConnection) obj).setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		DataOutputStream dataoutputstream = new DataOutputStream(
				((URLConnection) obj).getOutputStream());
		dataoutputstream.writeBytes(requestString);
		dataoutputstream.flush();
		dataoutputstream.close();
		BufferedReader bufferedreader = new BufferedReader(
				new InputStreamReader(((URLConnection) obj).getInputStream()));

		String responseData[];
		responseData = new String[100];
		int index = 0;
		String responseLine = null;
		responseString.append("<response>");
		do {
			responseLine = bufferedreader.readLine();
			responseData[index] = responseLine;
			index++;
			responseString
					.append(CommonUtil.isNotNull(responseLine) ? responseLine
							: "");
		} while (responseLine != null);

		responseString.append("</response>");

		return responseString.toString();
	}
	
/* TODO 	public Transaction updateCPTransactionStatus(EnquiryResult bean,
			String transactionId, Merchant merchant, String ctx,
			TransactionType type) {

		int pgResponseCode = CommonUtil.getInteger(bean.getRespCode(), -1);

		Transaction txn = transactionService.findByCTXandMTXIdAndType(ctx,
				transactionId, merchant, type);

		// Update transaction amount
		txn = updateTransactionAmount(txn);

		if (CommonUtil.isNotNull(txn)
				&& (txn.getStatus().ordinal() != pgResponseCode)) {
			if (txn.getStatus().equals(TransactionStatus.FORWARDED)
					&& pgResponseCode != 0) {
				return txn;
			}
			return updatePaymentResponseToTransaction(bean.getRespCode(),
					bean.getRespMsg(), bean.getAuthIdCode(), bean.getRRN(),
					bean.getTxnId(), bean.getPgTxnId(), null, null, txn, false);
		}

		return txn;
	}

*/
	
	
}
