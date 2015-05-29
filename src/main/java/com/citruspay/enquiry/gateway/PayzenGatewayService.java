package com.citruspay.enquiry.gateway;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.citruspay.CommonUtil;
import com.citruspay.JDateUtil;
import com.citruspay.enquiry.GatewayServiceImpl;
import com.citruspay.enquiry.api.EnquiryResponse;
import com.citruspay.enquiry.api.EnquiryResult;
import com.citruspay.enquiry.api.EnquiryResultList;
import com.citruspay.enquiry.configuration.AppConfigManager;
import com.citruspay.enquiry.persistence.entity.MerchantGatewaySetting;
import com.citruspay.enquiry.persistence.entity.PaymentGateway;
import com.citruspay.enquiry.persistence.entity.Transaction;
import com.citruspay.enquiry.persistence.entity.TransactionStatus;
import com.citruspay.enquiry.persistence.implementation.MerchantGatewaySettingDAOImpl;
import com.citruspay.enquiry.persistence.implementation.TransactionDAOImpl;
import com.citruspay.enquiry.persistence.interfaces.MerchantGatewaySettingDAO;
import com.citruspay.enquiry.persistence.interfaces.TransactionDAO;
import com.lyra.vads.ws.v4.Standard;
import com.lyra.vads.ws.v4.StandardWS;
import com.lyra.vads.ws.v4.TransactionInfo;
import com.citruspay.enquiry.gateway.PayZenPGUtil;


public class PayzenGatewayService extends GatewayServiceImpl implements
		GatewayService {

	private static final Logger log = LoggerFactory.getLogger(PayzenGatewayService.class);
	private int sequenceNumber = Integer.parseInt(AppConfigManager.INSTANCE.getAppConfig().getPropertiesWithPrefix("payzen").getProperty("sequenceNumber"));
	
	@Override
	public EnquiryResponse enquiry(Transaction transaction,
			String merchantRefundTxId, PaymentGateway paymentGateway) {
		
		
		EnquiryResponse enquiryResponse = new EnquiryResponse();

		// 1. If transaction is null return
		if (CommonUtil.isNull(transaction)) {
			enquiryResponse.setRespCode(RESP_CODE_SUCCESS);
			enquiryResponse.setRespMsg(ENQUIRY_NOT_PRESENT);
			return enquiryResponse;

		}

		List<EnquiryResult> inqueryResult = new ArrayList<EnquiryResult>();
		boolean isExternalPG = Boolean.TRUE;
		
		// Get PG detail
		// Is PG external or internal
		isExternalPG = isExternalGateway(paymentGateway);

		/**	If enquiry is of Failed Transaction, returning response without doing call
		 * to Payzen
		 */
		//if (TransactionStatus.REFUND_FAILED.ordinal() == transaction.getStatus().ordinal()) {
		if (STR_REFUND.equalsIgnoreCase(transaction.getTransactionType().toString())) {
			try {
				EnquiryResult inquirybean = new EnquiryResult();
				inquirybean.setRespCode(String.valueOf(transaction.getStatus().ordinal()));
				inquirybean.setRespMsg(transaction.getPgTxResp().getMessage());
				
				// Is PG external or internal
				inquirybean.setTxnId(isExternalPG ? transaction.getMerchantTxId() 
								: transaction.getTxId());
				inquirybean.setPgTxnId(transaction.getPgTxResp().getPgTxnId());
				inquirybean.setAuthIdCode(transaction.getPgTxResp().getAuthIdCode());
				inquirybean.setTxnDateTime(JDateUtil.getDateString(transaction.getCreated()));
				// Update amount explicitly
				inquirybean.setAmount(transaction.getOrderAmount().getAmount().toString());
				inquirybean.setCurrency(transaction.getOrderAmount().getCurrency());
				
				// set merchant refund tx id
				inquirybean.setMerchantRefundTxId(CommonUtil.isNotNull(transaction) ? transaction.getMerchantRefundTxId() : null);
				// set MTX
				inquirybean.setMerchantTxnId(CommonUtil.isNotNull(transaction) ? transaction.getMerchantTxId() : null);
				
				inqueryResult.add(inquirybean);
				
				
				enquiryResponse.setRespCode(RESP_CODE_SUCCESS);
				enquiryResponse.setRespMsg(ENQUIRY_SUCCESSFULL);
				

				EnquiryResultList txnEnquiryResponse = null;/* TODO indra getEnquiryResponse(merchantRefundTxId, inqueryResult);
				enquiryResponse.setData(txnEnquiryResponse);*/
				
				return enquiryResponse;
			} catch (Exception ex) {
				log.error(new StringBuffer("Error occurred during enquiry of Existing Refund Failed Txn for Payzen gateway: ")
					.append(transaction.getMerchant().getName())
					.append(" for CTx id: ").append(transaction.getTxId()).toString(), ex);
				enquiryResponse.setRespCode(ERROR_CODE_502);
				enquiryResponse.setRespMsg(ex.getMessage());

			}
		} else {
		
			// 1. If transaction is null return
			if (CommonUtil.isNull(transaction)) {
				enquiryResponse.setRespCode(RESP_CODE_SUCCESS);
				enquiryResponse.setRespMsg(ENQUIRY_NOT_PRESENT);

			}
			
			// 2. Setup
			MerchantGatewaySetting pgSetting = null;
			
			try {
				// 3. Get PG setting
				MerchantGatewaySettingDAO gatewaySettingDAO = new MerchantGatewaySettingDAOImpl();

				pgSetting = gatewaySettingDAO.findByMerchantAndGatewayCode(transaction.getMerchant().getId(),
						paymentGateway.getCode());
				
				// 4. Get Payzen merchant ID
				String payzenMerchantId = pgSetting != null ? pgSetting.getMerchantUsrid() : null;
				
				TransactionInfo payzenPGResp = null;
				String mtxCTXAttemptType = CTX_STRING;
				try {
					/** Inquiry response */
					payzenPGResp = doInquiry(pgSetting, payzenMerchantId, transaction.getTxId(),
							transaction.getCreated());
				} catch (Exception e) {
					log.error(new StringBuffer("Error occurred while creating the request enquiry " +
							"for PAYZEN gateway for merchant:")
						.append(transaction.getMerchant().getName())
						.append(" for ").append(mtxCTXAttemptType)
						.append(": and CTx: ").append(transaction.getTxId()).toString(), e);
					enquiryResponse.setRespCode(ERROR_CODE_502);
					enquiryResponse.setRespMsg(e.getMessage());

				}
				
				// 6. Fetch all transactions for CTX and merchant
				TransactionDAO transactionDAO = new TransactionDAOImpl();

				List<Transaction> transactions = transactionDAO.findListByCitrusTransactionId(transaction.getTxId());
				
				// 7. Prepare response
				// 7.1 No response received form PAYZEN
				if (!CommonUtil.isNotNull(payzenPGResp)) {
					log.info("Inquiry Response from PayZen is NULL");
					return prepareEnqiryResult(transactions, merchantRefundTxId,paymentGateway);
				}
				// 7.2 Response received from Payzen
				EnquiryResult inquirybean = new EnquiryResult();
				inquirybean.setRespCode(String.valueOf(payzenPGResp.getTransactionStatus()));
				inquirybean.setRespMsg(payzenPGResp.getTransactionStatusLabel());
				inquirybean.setTxnId(isExternalPG ? transaction.getMerchantTxId() 
								: transaction.getTxId());
				inquirybean.setPgTxnId(Long.toString(payzenPGResp.getTimestamp()));
				inquirybean.setAuthIdCode(payzenPGResp.getAuthorizationInfo().getAuthNumber());
				inquirybean.setTxnDateTime(JDateUtil.getDateString(transaction.getCreated()));
	
				Transaction txn = null;/* TODO indra matchTransaction(transactions, Long.toString(payzenPGResp.getTimestamp()), 
						payzenPGResp.getPaymentGeneralInfo().getTransactionId());
				*/
				if (CommonUtil.isNull(txn)) {
					txn = transactionDAO.getLastTxnByMtxAndMerchant(transaction.getMerchantTxId(), 
							transaction.getMerchant());
				}
				
				Transaction updatedTxn = null;
				//Update DB transaction status if mismatch with Payzen transaction
				if (payzenPGResp.getErrorCode() == CommonUtil.INT_ZERO
						&& PayZenPGConstants.PAYZENPG_TXN_STATUS_REFUSED.equalsIgnoreCase(payzenPGResp.getTransactionStatusLabel())) {
					//changing error code from Zero to One, as 0 for us means Success
					payzenPGResp.setErrorCode(CommonUtil.CONST_ONE);
					//Update DB transaction status if mismatch with Payzen transaction
					updatedTxn = null;//TODO indra updateCPTransactionStatus(payzenPGResp, txn);
				} else {
					//Update DB transaction status if mismatch with Payzen transaction
					updatedTxn = null;//TODO indra updateCPTransactionStatus(payzenPGResp, txn);
				}
				
				if (CommonUtil.isNotNull(updatedTxn)) {
					txn = updatedTxn;
				}

				if (CommonUtil.isNotNull(txn)) {
					// Update status code
					if (payzenPGResp.getErrorCode() == CommonUtil.INT_ZERO
							&& (payzenPGResp.getTransactionStatus() == CommonUtil.INT_FOUR 
							|| payzenPGResp.getTransactionStatus() == 6 
							|| payzenPGResp.getTransactionStatus() == 11 )) {
						inquirybean.setRespCode((TransactionStatus.SUCCESS_ON_VERIFICATION.equals(txn.getStatus())) 
								? "0" : String.valueOf(txn.getStatus().ordinal()));
					} else {
						//setting Citrus specified failure code
						inquirybean.setRespCode(String.valueOf(TransactionStatus.FAIL.ordinal()));
					}
					
					// Update amount explicitly
					inquirybean.setAmount(txn.getOrderAmount().getAmount().toString());
					inquirybean.setCurrency(txn.getOrderAmount().getCurrency());
				}
					
				// Update payment detail
				updatePaymentDetailAndAddressDetail(txn, inquirybean,paymentGateway);
				
				// set merchant refund tx id
				inquirybean.setMerchantRefundTxId(CommonUtil.isNotNull(txn) ? txn.getMerchantRefundTxId() : null);
				// set MTX
				inquirybean.setMerchantTxnId(CommonUtil.isNotNull(txn) ? txn.getMerchantTxId() : null);
				
				inqueryResult.add(inquirybean);
				
				// 8. Send response
				
				if (inqueryResult.isEmpty()) {
					enquiryResponse.setRespCode(RESP_CODE_SUCCESS);
					enquiryResponse.setRespMsg(ENQUIRY_NOT_PRESENT);

					
				} else {
					enquiryResponse.setRespCode(RESP_CODE_SUCCESS);
					enquiryResponse.setRespMsg(ENQUIRY_SUCCESSFULL);

					EnquiryResultList txnEnquiryResponse = fillEnquiryResponse(merchantRefundTxId, inqueryResult);
					enquiryResponse.setData(txnEnquiryResponse);
				}
				return enquiryResponse;
			} catch (Exception ex) {
				log.error(new StringBuffer("Error occurred during enquiry for Payzen gateway: ")
					.append(transaction.getMerchant().getName())
					.append(" for CTx id: ").append(transaction.getTxId()).toString(), ex);
				enquiryResponse.setRespCode(ERROR_CODE_502);
				enquiryResponse.setRespMsg(ex.getMessage());

			}
		}

		return enquiryResponse;

	}
	
	
	/**
	 * @param pgSetting 
	 * @param payzenMerchantId
	 * @param Ctx
	 * @param CTXAttemptType    
	 * @return
	 * @throws Exception
	 */
	private TransactionInfo doInquiry(MerchantGatewaySetting pgSetting, String payzenMerchantId,
			String ctx, Date txnDate) throws Exception {
		
		XMLGregorianCalendar xmlGregorianCalendar = PayZenPGUtil
				.getGregorianDate(txnDate, GatewayService.TIME_ZONE_UTC);
		
		Standard standard = new PayZenPGUtil().prepareStandard();
		
		// 5. Setup inquiry request
		String enquirySignature = null;
		enquirySignature = PayZenPGSignatureStringCreator
				.createSignatureDataForPayZenEnquiryCall(pgSetting.getPayzenSiteId(), 
						ctx, sequenceNumber, pgSetting.getPayzenCtxMode(), 
						pgSetting.getPayzenCertiNumber());

		StandardWS standardWS = new StandardWS();
		Standard port = standardWS.getPort(Standard.class);
		
		log.info(new StringBuffer("Making Inquiry to Payzen with parameters: PayZen Site Id: ")
				.append(pgSetting.getPayzenSiteId())
				.append(", Date: ").append(xmlGregorianCalendar)
				.append(", CTx: ").append(ctx)
				.append(", sequenceNumber: ").append(sequenceNumber)
				.append(", Mode: ").append(pgSetting.getPayzenCtxMode())
				.append(", enquirysignature: ").append(enquirySignature).toString());
		
		TransactionInfo transactionInfo = standard.getInfo(pgSetting.getPayzenSiteId(),
				xmlGregorianCalendar,
				ctx, 
				sequenceNumber,
				pgSetting.getPayzenCtxMode(), 
				enquirySignature);
		log.info(new StringBuffer("Inquiry Response from PayZen is: ")
				.append("ErrorCode: ").append(transactionInfo.getErrorCode())
				.append(" which means: ").append(PayZenPGConstants
						.transactionInfoErrorCodeDesc(transactionInfo.getErrorCode()))
				.append(", Transaction Status Code: ").append(transactionInfo.getTransactionStatus())
				.append(" which means: ").append(PayZenPGConstants
						.transactionInfoErrorCodeDesc(transactionInfo.getTransactionStatus()))
				.append(" and message is: ").append(transactionInfo.getTransactionStatusLabel())
				.toString());
		
		return transactionInfo;
	}


}
