package com.citruspay.enquiry.gateway;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.w3c.dom.CharacterData;



import com.citruspay.CommonUtil;
import com.citruspay.JDateUtil;
import com.citruspay.enquiry.GatewayServiceImpl;
import com.citruspay.enquiry.api.EnquiryResponse;
import com.citruspay.enquiry.api.EnquiryResult;
import com.citruspay.enquiry.api.EnquiryResultList;
import com.citruspay.enquiry.configuration.AppConfigManager;
import com.citruspay.enquiry.encryption.AESEncryptionDecryption;
import com.citruspay.enquiry.persistence.entity.Merchant;
import com.citruspay.enquiry.persistence.entity.MerchantGatewaySetting;
import com.citruspay.enquiry.persistence.entity.PGTransaction;
import com.citruspay.enquiry.persistence.entity.PaymentGateway;
import com.citruspay.enquiry.persistence.entity.Transaction;
import com.citruspay.enquiry.persistence.entity.TransactionStatus;
import com.citruspay.enquiry.persistence.implementation.MerchantGatewaySettingDAOImpl;
import com.citruspay.enquiry.persistence.implementation.TransactionDAOImpl;
import com.citruspay.enquiry.persistence.interfaces.MerchantGatewaySettingDAO;
import com.citruspay.enquiry.persistence.interfaces.TransactionDAO;

public class PNBATMGatewayService extends GatewayServiceImpl implements
		GatewayService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PNBATMGatewayService.class);
	private static final String fssNetEnquiryURL = AppConfigManager.INSTANCE.getAppConfig().getPropertiesWithPrefix("fssNet.atm.card.enquiry").getProperty("url");
	private Map<String, String> statusMap = new HashMap<String, String>();
	{
		statusMap.put("CAPTURED", "Transaction successful");
		statusMap.put("APPROVED", "Transaction Approved");
		statusMap.put("NOT CAPTURED", "Transaction failed");
		statusMap.put("NOT APPROVED", "Transaction failed");
		statusMap.put("DENIED BY RISK", "Risk denied the transaction processing");
		statusMap.put("HOST TIMEOUT", "The authorization system did not respond within the Time out limit");
		statusMap.put("SUCCESS", "Transaction is successful");
		statusMap.put("FAILURE(NOT CAPTURED)", "The transaction is failed");
		statusMap.put("FAILURE(SUSPECT)", "The transaction data is not matching, and hence failed.");
	}

	@Override
	public EnquiryResponse enquiry(Transaction transaction,
			String merchantRefundTxId, PaymentGateway paymentGateway) {
		LOGGER.info("Enquiry request start for TXN ID : " +  transaction.getId());

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
			// Get PG detail

			// Is PG external or internal
			isExternalPG = isExternalGateway(paymentGateway);

			// If external PG send MTX , if internal PG send CTX
			mtxflag = (isExternalPG) ? Boolean.TRUE : Boolean.FALSE;

			// Fetch transactions from citrus DB
			TransactionDAO transactionDAO = new TransactionDAOImpl();

			List<Transaction> transactions = transactionDAO.findByMerchantTxnIdAndGateway(
					transaction.getMerchantTxId(), transaction.getMerchant(),
					paymentGateway.getCode());

			// Get PG setting
			MerchantGatewaySettingDAO gatewaySettingDAO = new MerchantGatewaySettingDAOImpl();

			MerchantGatewaySetting pgSetting = gatewaySettingDAO.findByMerchantAndGatewayCode(transaction.getMerchant().getId(),
					paymentGateway.getCode());


			// 1. If transaction does not exist in citrus DB
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
					if (CommonUtil.isNull(transaction.getPgTxResp()) || 
							CommonUtil.isNull(transaction.getPgTxResp().getPgTxnId())) {
						return prepareEnqiryResult(transactions, merchantRefundTxId,paymentGateway);
					}
					//sending the inquiry request.
					if(TransactionStatus.SUCCESS.equals(txn.getStatus())) {
						responseString = doinquiry(txn, pgSetting, mtxflag, null);
					}
					 //If response is null then enquire with Citrus transaction id
					repeatEnquiry = (!repeatEnquiry && CommonUtil
							.isEmpty(responseString)) ? Boolean.TRUE : Boolean.FALSE;

				} while (repeatEnquiry);
				
				// Process response
				if(responseString != null) {
					if (!CommonUtil.isEmpty(responseString.toString())) {
						
						LOGGER.info("Response received for enquiry: " + responseString.toString());
	
						EnquiryResult inquiryBean = parseEnquiryResponse(txn,
								responseString, txn.getMerchant(),
								txn.getMerchantTxId(), txn.getTxId(), mtxflag);
						updatePaymentDetailAndAddressDetail(transaction, inquiryBean,paymentGateway);
						// Update status if required
						Transaction updatedTransaction = null;/*TODO indra updateCPTransactionStatus(
								inquiryBean, txn.getMerchantTxId(),
								txn.getMerchant(), txn.getTxId(),
								txn.getTransactionType());
	*/
						// Update result for transaction type
						inquiryBean.setTxnType(txn.getTransactionType().toString());
						
						// set merchant refund tx id
						inquiryBean.setMerchantRefundTxId(txn.getMerchantRefundTxId());
						// set MTX
						inquiryBean.setMerchantTxnId(txn.getMerchantTxId());
						
						// Update amount if not present in response
						if (CommonUtil.isNotNull(updatedTransaction)) {
							inquiryBean.setAmount(CommonUtil.isNotNull(updatedTransaction.getOrderAmount())
												? updatedTransaction.getOrderAmount().getAmount().toString(): "");
						}
						// Update date if not present in response
						if (CommonUtil.isNull(inquiryBean.getTxnDateTime())) {
							inquiryBean.setTxnDateTime(txn.getCreated().toString());
						}
						inqueryResult.add(inquiryBean);
					}
				} else {
					EnquiryResult bean = new EnquiryResult();
					
					bean.setRespCode((TransactionStatus.SUCCESS_ON_VERIFICATION
							.equals(txn.getStatus())) ? CommonUtil.ZERO_STRING : String.valueOf(txn
							.getStatus().ordinal()));
					
					String respMsg = txn.getStatus().getDisplayMsg();
					
					bean.setRespMsg(respMsg);
					bean.setTxnId(txn.getTxId());
					bean.setTxnDateTime(JDateUtil.getDateStringInIST(txn.getCreated()));
					bean.setAmount(txn.getOrderAmount().getAmount().toString());
					bean.setTxnType(txn.getTransactionType().name());
					bean.setMerchantRefundTxId(txn.getMerchantRefundTxId());
					// set MTX
					bean.setMerchantTxnId(txn.getMerchantTxId());
					bean.setTxnGateway(paymentGateway.getName());
					bean.setPgTxnId(transaction.getPgTxResp().getPgTxnId());
					bean.setAuthIdCode(transaction.getPgTxResp().getAuthIdCode());
					
					inqueryResult.add(bean);
				}
			}

			// Response list is empty, send data from citrus DB
			if (!CommonUtil.isNotEmpty(inqueryResult)) {
				
				return prepareEnqiryResult(transactions, merchantRefundTxId,paymentGateway);
			}

			// Return response list
			
			enquiryResponse.setRespCode(RESP_CODE_SUCCESS);
			enquiryResponse.setRespMsg(ENQUIRY_SUCCESSFULL);

			EnquiryResultList txnEnquiryResponse = fillEnquiryResponse(
					  	merchantRefundTxId, inqueryResult);
			
			enquiryResponse.setData(txnEnquiryResponse);
			
			return enquiryResponse;

		} catch (Exception ex) {
			LOGGER.error("Error occurred during  enquiry  for  FssNet gateway: "
					+ transaction.getMerchant().getName() + " for id: "
					+ transaction.getMerchantTxId(), ex);

			enquiryResponse.setRespCode(ERROR_CODE_502);
			enquiryResponse.setRespMsg(ex.getMessage());

		}
		return enquiryResponse;

	
	}
	/** This method used to sending the enquery request to fssNet gateway.
	 * @param transaction
	 * @param pgSetting
	 * @param mtxflag
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	private String doinquiry(Transaction transaction,
			MerchantGatewaySetting pgSetting, boolean mtxflag, String txId)
			throws GeneralSecurityException {

		LOGGER.info("Preparing the Request Parameters for TXN ID : " +  transaction.getId());
		
		Map<String, String> parameterMap = new LinkedHashMap<String, String>();

		parameterMap.put("id", pgSetting.getMerchantUsrid());
		parameterMap.put("password", new AESEncryptionDecryption().decrypt(pgSetting.getMerchantPswd()));
		parameterMap.put("action", ENQ_ACTION);
		parameterMap.put("amt", transaction.getOrderAmount().getAmount().toString());
		
		PGTransaction pgTxn = transaction.getPgTxResp();
		
		//sending the inquiry based on payment id.
		parameterMap.put("transid", CommonUtil.isNotNull(pgTxn) ? pgTxn.getPaymentId() : null);
		//txId not null only for history inquiry.
		if (!CommonUtil.isEmpty(txId)) {
			parameterMap.put("trackid", txId);
		} else {
			parameterMap.put("trackid", (mtxflag) ? transaction.getMerchantTxId()
				: transaction.getTxId());
		}
		//In udf5 we need to send PaymentID string because we inquiry is doing based on paymentID.
		parameterMap.put("udf5", "PaymentID");
		// this is the root element we need to append for parsing the response.
		StringBuilder txnFinalResponse = new StringBuilder("<response>");
		DataOutputStream dataoutputstream = null;
		BufferedReader bufferedReader = null;
		
		try {
			Object object = getConnection(fssNetEnquiryURL);
			// Here the HTTPS request URL is created
			dataoutputstream = new DataOutputStream(((URLConnection) object).getOutputStream());
			// here the request is sent to payment gateway
			dataoutputstream.writeBytes(getRequestString(parameterMap)); 
			dataoutputstream.flush();
			
			bufferedReader = new BufferedReader(new InputStreamReader(((URLConnection) object).getInputStream()));
			
			//Reading the response line by line
			String decodedString = null;
			while ((decodedString = bufferedReader.readLine()) != null) {
				txnFinalResponse.append(decodedString); 
			}
			
			txnFinalResponse.append("</response>");
		} catch (IOException ioException) {
			LOGGER.error("FssNet PG : Error connecting request for TXN ID: "
					+ transaction.getId()
					+ " :: "
					+ transaction.getMerchant().getId()
					+ " : "
					+ transaction.getMerchantTxId());

			return null;
		} finally {
			try {
				dataoutputstream.close();
				bufferedReader.close();
			} catch (IOException ioException) {
				LOGGER.error("Error occuered while closing the connections");
				return null;
			}
		}
		return txnFinalResponse.toString();
	}

	/**
	 * This method is used to parse the response received from the PG after enquiry call.
	 * @param transaction
	 * @param responseString
	 * @param merchant
	 * @param merchantTxnId
	 * @param ctx
	 * @param mtxflag
	 * @return
	 */

	private EnquiryResult parseEnquiryResponse(Transaction transaction,
			String responseString, Merchant merchant, String merchantTxnId,
			String ctx, boolean mtxflag) {
		
		LOGGER.info("Parsing the Enquiry response for TXN ID : " +  transaction.getId());

		EnquiryResult bean = new EnquiryResult();
		try {
			boolean isError = isErrorInResponse(responseString);

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(responseString));

			Document doc = db.parse(is);
			NodeList nodes = doc.getElementsByTagName("response");
			for (int i = 0; i < nodes.getLength(); i++) {

				if (isError) {

					Element element = (Element) nodes.item(i);

					NodeList errorCode = element
							.getElementsByTagName("error_code_tag");
					Element line = (Element) errorCode.item(0);
					String responseCode = getCharacterDataFromElement(line);

					if (STATUS_CAPTURED.equals(responseCode)
							|| STATUS_SUCCESS.equals(responseCode)
							|| STATUS_APPROVED.equals(responseCode)) {
						bean.setRespCode(RESP_CODE_0);
					} else {
						bean.setRespCode(RESP_CODE_1);
					}

					NodeList errorMessage = element
							.getElementsByTagName("result");
					line = (Element) errorMessage.item(0);
					String responseMessage = getCharacterDataFromElement(line);
					bean.setRespMsg(responseMessage.substring(responseMessage
							.lastIndexOf("-") + 1));

					// Set merchantTxnId as txnID

					bean.setTxnId((mtxflag) ? merchantTxnId : ctx);

					//  Updating error msg as Transaction Failure for status code 1
					if (RESP_CODE_1.equals(bean.getRespCode())) {
						bean.setRespMsg(TXN_FAILURE);
					}
				} else {

					Element element = (Element) nodes.item(i);

					NodeList name = element.getElementsByTagName("result");
					Element line = (Element) name.item(0);
					String responseCode = getCharacterDataFromElement(line);

					if (STATUS_CAPTURED.equals(responseCode)
							|| STATUS_SUCCESS.equals(responseCode)
							|| STATUS_APPROVED.equals(responseCode)) {
						bean.setRespCode(RESP_CODE_0);
					} else {
						bean.setRespCode(RESP_CODE_1);
					}
					if (STATUS_CAPTURED.equals(responseCode)) {
						bean.setTxnType(responseCode);
					} else {
						bean.setTxnType(responseCode);
					}

					bean.setRespMsg(statusMap.get(responseCode));

					NodeList auth = element.getElementsByTagName("auth");
					line = (Element) auth.item(0);
					bean.setAuthIdCode(getCharacterDataFromElement(line));

					NodeList ref = element.getElementsByTagName("ref");
					line = (Element) ref.item(0);
					bean.setRRN(getCharacterDataFromElement(line));

					NodeList tranid = element.getElementsByTagName("tranid");
					line = (Element) tranid.item(0);
					bean.setPgTxnId(getCharacterDataFromElement(line));

					// To check if used or not
					NodeList payid = element.getElementsByTagName("payid");
					line = (Element) payid.item(0);
					bean.setTxnId(getCharacterDataFromElement(line));
					// Changed to make txn id as MTX and not as pgTxnID
					bean.setTxnId(merchantTxnId);

					NodeList amt = element.getElementsByTagName("amt");
					line = (Element) amt.item(0);
					bean.setAmount(getCharacterDataFromElement(line));

					bean.setTxnDateTime(transaction.getLastModified().toString());
					
					if (mtxflag) {
						bean.setTxnId(merchantTxnId);
					} else {
						bean.setTxnId(ctx);
					}
				}
			}
			LOGGER.info("Enquiry API response for mtx: " + merchantTxnId
							+ " code: " + bean.getRespCode() + " msg: " + bean.getRespMsg());
		} catch (Exception ex) {
			LOGGER.error("Exceprion occurred during parsing enquiry response xml : " + responseString);
		}
		return bean;
	}
	/**
	 * This is for parsing the element data.
	 * @param e
	 * @return
	 */
	private String getCharacterDataFromElement(Element e) {
		if (e != null) {
			Node child = e.getFirstChild();
			if (child instanceof CharacterData) {
				CharacterData cd = (CharacterData) child;
				return cd.getData();
			}
			return "";
		}
		return null;
	}

	/**
	 *  This method used to get the connection object.
	 * @param fssNetURL
	 * @return
	 * @throws IOException
	 */
	private Object getConnection(String fssNetEnquiryURL) throws IOException {
		
		URL url = new URL(fssNetEnquiryURL);
		//create a SSL connection object server-to-server
		Object object = (HttpsURLConnection)url.openConnection();
		((URLConnection)object).setDoInput(TRUE);
		((URLConnection)object).setDoOutput(TRUE);
		((URLConnection)object).setUseCaches(FALSE);
		((URLConnection)object).setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		
		return object;
	}
	/**This method is used to prepare the request parameter
	 * 
	 * @param parameterMap
	 * @return
	 */
	private String getRequestString(Map<String, String> parameterMap) {

		StringBuffer sb = new StringBuffer();
		Set<String> keys = parameterMap.keySet();

		for (String key : keys) {
			sb.append("<" + key + ">");
			sb.append(parameterMap.get(key));
			sb.append("</" + key + ">");
		}
		return sb.toString();
	}



}
