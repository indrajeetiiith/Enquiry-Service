package com.citruspay.enquiry.gateway;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.citruspay.CommonUtil;
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
import com.citruspay.enquiry.persistence.implementation.MerchantGatewaySettingDAOImpl;
import com.citruspay.enquiry.persistence.implementation.TransactionDAOImpl;
import com.citruspay.enquiry.persistence.interfaces.MerchantGatewaySettingDAO;
import com.citruspay.enquiry.persistence.interfaces.TransactionDAO;


public class HDFC3DGatewayService extends GatewayServiceImpl implements GatewayService{

	private static final Logger log = LoggerFactory
			.getLogger(HDFC3DGatewayService.class);
	public static final String hdfcPGUrl =AppConfigManager.INSTANCE.getAppConfig().getPropertiesWithPrefix("hdfc3d.nonenrolled").getProperty("url"); 
	
	private final String DATE_PADDING = "XXXXXXXXXX";

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
		List<EnquiryResult> enquiryResultList = new ArrayList<EnquiryResult>();
		boolean mtxflag = Boolean.TRUE;
		boolean isExternalPG = Boolean.TRUE;

		try {

			// 3. Get all transactions for MTX and merchant from Citrus DB
			/*
			 * First attempt is made with hdfc3D, if no transaction found then
			 * attempt is made with hfdcCitrus(internal pg)
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

			// 1. If transaction does not exist in citrus DB 
			if (CommonUtil.isEmpty(transactions)) {
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
						return prepareEnqiryResult(transactions,
								merchantRefundTxId,paymentGateway);
					}
					responseString = doinquiry(txn, pgSetting, mtxflag);

					repeatEnquiry = (!repeatEnquiry && CommonUtil
							.isEmpty(responseString)) ? Boolean.TRUE
							: Boolean.FALSE;

				} while (repeatEnquiry);// keep calling till the response string is not null 

				/** Process response */
				if (!CommonUtil.isEmpty(responseString.toString())) {
					log.info("Response received for enquiry: "
							+ responseString.toString());

					EnquiryResult enquiryResult = parseEnquiryResponse(txn,
							responseString, txn.getMerchant(),
							txn.getMerchantTxId(), txn.getTxId(), mtxflag);
					
					
					updateInquiryForCardParamater(enquiryResult,transaction);
					updatePaymentDetailAndAddressDetail(transaction, enquiryResult,paymentGateway);
					// Update status if required

					//TODO indra need to update the transactionstatus to the table
					
					System.out.println(" pgResponseCode ="+enquiryResult.getRespCode()+" pgresp=CommonUtil.getInteger(bean.getRespCode(), -1);="+CommonUtil.getInteger(enquiryResult.getRespCode(),-1)
							
							
							+"txn.getStatus()="+txn.getStatus());

					enquiryResult.setTxnType(txn.getTransactionType().toString());

					// set merchant refund tx id
					enquiryResult.setMerchantRefundTxId(txn
							.getMerchantRefundTxId());
					// set MTX
					enquiryResult.setMerchantTxnId(txn.getMerchantTxId());

					// Update date if not present in response
					if (CommonUtil.isNull(enquiryResult.getTxnDateTime())) {
						enquiryResult.setTxnDateTime(txn.getCreated().toString());
					}

					enquiryResultList.add(enquiryResult);

				}

			}


			enquiryResponse.setRespCode(RESP_CODE_SUCCESS);
			enquiryResponse.setRespMsg(ENQUIRY_SUCCESSFULL);

			EnquiryResultList txnEnquiryResponse = fillEnquiryResponse(
					merchantRefundTxId, enquiryResultList);

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
	/** This function calls the HDFC's enquiry and gets the response
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
	

	/**
	 * This function parses the response received from HDFC 's enquiry call

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

		EnquiryResult bean = null;
		try {
			bean = new EnquiryResult();
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

					// TODO : Updating error msg as Transaction Failure for
					// status code 1
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

					bean.setRespMsg(HDFCStatusCode.statusMap.get(responseCode));

					NodeList auth = element.getElementsByTagName("auth");
					line = (Element) auth.item(0);
					bean.setAuthIdCode(getCharacterDataFromElement(line));

					NodeList ref = element.getElementsByTagName("ref");
					line = (Element) ref.item(0);
					bean.setRRN(getCharacterDataFromElement(line));

					NodeList tranid = element.getElementsByTagName("tranid");
					line = (Element) tranid.item(0);
					bean.setPgTxnId(getCharacterDataFromElement(line));

					// TODO : To check if used or not
					NodeList payid = element.getElementsByTagName("payid");
					line = (Element) payid.item(0);
					bean.setTxnId(getCharacterDataFromElement(line));
					// Changed to make txn id as MTX and not as pgTxnID
					bean.setTxnId(merchantTxnId);

					NodeList amt = element.getElementsByTagName("amt");
					line = (Element) amt.item(0);
					bean.setAmount(getCharacterDataFromElement(line));

					NodeList postDate = element
							.getElementsByTagName("postdate");
					line = (Element) postDate.item(0);
					String orderDate = getCharacterDataFromElement(line);
					orderDate = DATE_PADDING.substring(0,
							10 - orderDate.length())
							+ orderDate;

					bean.setTxnDateTime(orderDate);
					if (mtxflag) {
						bean.setTxnId(merchantTxnId);
					} else {
						bean.setTxnId(ctx);
					}

				}

			}

			log.info("Enquiry API response for mtx: " + merchantTxnId
					+ " code: " + bean.getRespCode() + " msg: "
					+ bean.getRespMsg());

		} catch (Exception ex) {
			log.error("Exceprion occurred during parsing enquiry response xml :"
					+ responseString);
		}

		return bean;
	}

	/**
	 * This function gets the data from a particular element
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


	
	
}
