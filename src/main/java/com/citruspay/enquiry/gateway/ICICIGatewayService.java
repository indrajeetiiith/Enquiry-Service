package com.citruspay.enquiry.gateway;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.citruspay.CommonUtil;
import com.citruspay.JDateUtil;
import com.citruspay.enquiry.GatewayServiceImpl;
import com.citruspay.enquiry.api.EnquiryResponse;
import com.citruspay.enquiry.api.EnquiryResult;
import com.citruspay.enquiry.api.EnquiryResultList;
import com.citruspay.enquiry.persistence.entity.MerchantGatewaySetting;
import com.citruspay.enquiry.persistence.entity.PaymentGateway;
import com.citruspay.enquiry.persistence.entity.Transaction;
import com.citruspay.enquiry.persistence.entity.TransactionStatus;
import com.citruspay.enquiry.persistence.entity.TransactionType;
import com.citruspay.enquiry.persistence.implementation.MerchantGatewaySettingDAOImpl;
import com.citruspay.enquiry.persistence.implementation.TransactionDAOImpl;
import com.citruspay.enquiry.persistence.interfaces.MerchantGatewaySettingDAO;
import com.citruspay.enquiry.persistence.interfaces.TransactionDAO;
import com.opus.epg.icici.sfa.java.PGResponse;
import com.opus.epg.icici.sfa.java.PGSearchResponse;
import com.opus.epg.icici.sfa.java.PostLib;


public class ICICIGatewayService extends GatewayServiceImpl implements GatewayService {

	private static final Logger log = LoggerFactory
			.getLogger(ICICIGatewayService.class);

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

		// 2. Setup
		MerchantGatewaySetting pgSetting = null;

		List<EnquiryResult> inqueryResult = new ArrayList<EnquiryResult>();
		ArrayList<PGResponse> resultList = new ArrayList<PGResponse>();

		try {

			// Get PG detail

			// 3. Get PG setting

			MerchantGatewaySettingDAO gatewaySettingDAO = new MerchantGatewaySettingDAOImpl();

			pgSetting = gatewaySettingDAO.findByMerchantAndGatewayCode(transaction.getMerchant().getId(),
					paymentGateway.getCode());

			// 4. Get ICICI merchant ID
			String iciciMerchantId = pgSetting != null ? pgSetting
					.getMerchantUsrid() : null;

			// 5. Setup inquiry request

			String mtxCTXAttemptType = MTX_STRING;
			ArrayList<PGResponse> oPgRespArr = null;

			/*
			 * First attempt is made with MTX, if no response is received then
			 * second attempt is made with CTX
			 */
			for (int attemp = 0; attemp < 2 && CommonUtil.isNull(oPgRespArr); attemp++) {
				if (attemp > 0) {
					mtxCTXAttemptType = CTX_STRING;
				}

				try {
					/** Inquiry response */
					/*oPgRespArr = doInquiry(iciciMerchantId,
							(attemp == 0) ? transaction.getMerchantTxId()
									: transaction.getTxId(), mtxCTXAttemptType);
*/
					oPgRespArr = doInquiry(iciciMerchantId,
									 transaction.getTxId(), mtxCTXAttemptType);
				} catch (Exception e) {
					log.error(
							"Error occurred while creating the request enquiry for ICICI gateway for merchant :"
									+ transaction.getMerchant().getName()
									+ "for"
									+ mtxCTXAttemptType
									+ " : "
									+ transaction.getMerchantTxId(), e);
					enquiryResponse.setRespCode(ERROR_CODE_502);
					enquiryResponse.setRespMsg(e.getMessage());
				}
			}// End of iteration

			// 6. Add response to list
			if (CommonUtil.isNotNull(oPgRespArr)) {
				log.info("ICICI enquiry response:" + oPgRespArr);
				resultList.addAll(oPgRespArr);
			}

			// 7. Fetch all transactions for MTX and merchant
			TransactionDAO transactionDAO = new TransactionDAOImpl();

			List<Transaction> transactions = transactionDAO
					.findByMerchantTxnIdAndGateway(
							transaction.getMerchantTxId(),
							transaction.getMerchant(), paymentGateway.getCode());

			// 8. Prepare response

			// 8.1 No response received form ICICI
			if (!CommonUtil.isNotEmpty(resultList)) {
				return prepareEnqiryResult(transactions, merchantRefundTxId,paymentGateway);
			}

			// 8.2 Response received from ICICI
			for (PGResponse oPgResp : resultList) {
				EnquiryResult bean = new EnquiryResult();
				bean.setRespCode(oPgResp.getRespCode());
				bean.setRespMsg(oPgResp.getRespMessage());
				bean.setTxnId(oPgResp.getTxnId());
				bean.setMerchantTxnId(transaction.getMerchantTxId());
				bean.setPgTxnId(oPgResp.getEpgTxnId());
				bean.setAuthIdCode(oPgResp.getAuthIdCode());
				bean.setRRN(oPgResp.getRRN());
				bean.setTxnType(oPgResp.getTxnType());
				bean.setTxnDateTime(JDateUtil.getPGDate(oPgResp.getTxnDateTime()));
				bean.setCvResponseCode(oPgResp.getCVRespCode());

				if (CommonUtil.isNotNull(oPgResp.getTxnType())
						&& oPgResp.getTxnType().equalsIgnoreCase(
								TransactionType.PREAUTH.toString())) {
					if (CommonUtil.isNotNull(oPgResp.getRespCode())
							&& oPgResp.getRespCode().equalsIgnoreCase("0")) {
						bean.setRespMsg(TXN_APPROVED);
					}
				}

				Transaction txn = null; /* TODO indra matchTransaction(transactions,
						oPgResp.getEpgTxnId(), oPgResp.getRRN());
				*/
				if (CommonUtil.isNull(txn)) {
					txn = transactionDAO.getLastTxnByMtxAndMerchant(
							transaction.getMerchantTxId(),
							transaction.getMerchant());
				}
				
				updateInquiryForCardParamater(bean,txn);
				
				// Update transaction status if mismatch with ICICI transaction
				Transaction updatedTxn = null;//TODO indra updateCPTransactionStatus(oPgResp, txn);
				if (CommonUtil.isNotNull(updatedTxn)) {
					txn = updatedTxn;
					/*
					 * bean.setRespCode((TransactionStatus.SUCCESS_ON_VERIFICATION
					 * .equals(updatedTxn.getStatus())) ? "0" : String
					 * .valueOf(updatedTxn.getStatus().ordinal()));
					 */
				}

				if (CommonUtil.isNotNull(txn)) {
					// Update status code
					bean.setRespCode((TransactionStatus.SUCCESS_ON_VERIFICATION
							.equals(txn.getStatus())) ? "0" : String
							.valueOf(txn.getStatus().ordinal()));

					// Update amount explicitly
					bean.setAmount(txn.getOrderAmount().getAmount().toString());
					bean.setCurrency(txn.getOrderAmount().getCurrency());
				}

				// Update payment detail
				updatePaymentDetailAndAddressDetail(txn, bean,paymentGateway);

				// set merchant refund tx id
				bean.setMerchantRefundTxId(CommonUtil.isNotNull(txn) ? txn
						.getMerchantRefundTxId() : null);
				// set MTX
				bean.setMerchantTxnId(CommonUtil.isNotNull(txn) ? txn
						.getMerchantTxId() : null);
				
				inqueryResult.add(bean);
				log.info("Enquiry status from opus for id:"
						+ transaction.getMerchantTxId() + " code:"
						+ oPgResp.getRespCode() + " msg:"
						+ oPgResp.getRespMessage());
			}// end of iteration

			// 9. Send response

			if (inqueryResult.isEmpty()) {
				enquiryResponse.setRespCode(RESP_CODE_SUCCESS);
				enquiryResponse.setRespMsg(ENQUIRY_NOT_PRESENT);

				
				
			} else {
				enquiryResponse.setRespCode(RESP_CODE_SUCCESS);
				enquiryResponse.setRespMsg(ENQUIRY_SUCCESSFULL);

				
				EnquiryResultList txnEnquiryResponse = fillEnquiryResponse(
						merchantRefundTxId, inqueryResult);
				
				enquiryResponse.setData(txnEnquiryResponse);
			}

			return enquiryResponse;
		} catch (Exception ex) {
			log.error("Error occurred during  enquiry  for  ICICI gateway:"
					+ transaction.getMerchant().getName() + "for id:"
					+ transaction.getMerchantTxId(), ex);
			enquiryResponse.setRespCode(ERROR_CODE_502);
			enquiryResponse.setRespMsg(ex.getMessage());
		}

		
		
		return enquiryResponse;
	}
	
	@SuppressWarnings("unchecked")
	private ArrayList<PGResponse> doInquiry(String iciciMerchantId,
			String mtxOrCtx, String mtxCTXAttemptType) throws Exception {

		// 5. Setup inquiry request
		com.opus.epg.icici.sfa.java.Merchant oMerchant = new com.opus.epg.icici.sfa.java.Merchant();

		PostLib oPostLib = null;
		PGSearchResponse oPgSearchResp = null;

		oMerchant.setMerchantOnlineInquiry(iciciMerchantId, mtxOrCtx);

		log.info("ICICI enquiry request for" + mtxCTXAttemptType + " : "
				+ mtxOrCtx);

		oPostLib = new PostLib();
		oPgSearchResp = oPostLib.postStatusInquiry(oMerchant);

		/** Inquiry response */
		return oPgSearchResp.getPGResponseObjects();
	}



}
