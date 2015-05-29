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
import com.citruspay.enquiry.persistence.entity.TransactionType;
import com.citruspay.enquiry.persistence.implementation.MerchantGatewaySettingDAOImpl;
import com.citruspay.enquiry.persistence.implementation.TransactionDAOImpl;
import com.citruspay.enquiry.persistence.interfaces.MerchantGatewaySettingDAO;
import com.citruspay.enquiry.persistence.interfaces.TransactionDAO;
import com.ecs.epg.bob.sfa.java.PGResponse;
import com.ecs.epg.bob.sfa.java.PGSearchResponse;
import com.ecs.epg.bob.sfa.java.PostLib;


public class BOBGatewayService extends GatewayServiceImpl implements
		GatewayService {

	private static final Logger log = LoggerFactory
			.getLogger(BOBGatewayService.class);

	@Override
	public EnquiryResponse enquiry(Transaction transaction,
			String merchantRefundTxId, PaymentGateway paymentGateway) {
		// TODO Auto-generated method stub
		
		EnquiryResponse enquiryResponse = new EnquiryResponse();

		// 1. If transaction is null return
		if (CommonUtil.isNull(transaction)) {
			enquiryResponse.setRespCode(RESP_CODE_SUCCESS);
			enquiryResponse.setRespMsg(ENQUIRY_NOT_PRESENT);
			return enquiryResponse;

		}

		log.info("BOB enquiry request for transaction: "
				+ transaction.toString());

		// 2. Setup
		List<EnquiryResult> inqueryResult = new ArrayList<EnquiryResult>();
		ArrayList<PGResponse> resultList = new ArrayList<PGResponse>();

		try {

			// Get PG detail

			// 3. Get PG setting

			MerchantGatewaySettingDAO gatewaySettingDAO = new MerchantGatewaySettingDAOImpl();

			MerchantGatewaySetting pgSettings = gatewaySettingDAO.findByMerchantAndGatewayCode(transaction.getMerchant().getId(),
					paymentGateway.getCode());

			String bobMerchantId = pgSettings != null ? pgSettings
					.getMerchantUsrid() : null;

			/* Prepare request */

			ArrayList<PGResponse> oPgRespArr = null;

			/*
			 * First attempt is made with MTX, if no response is received then
			 * second attempt is made with CTX
			 */
			for (int attemp = 0; attemp < 2 && CommonUtil.isNull(oPgRespArr); attemp++) {
				try {
					oPgRespArr = doInquiry(bobMerchantId,
							(attemp == 0) ? transaction.getMerchantTxId()
									: transaction.getTxId());

				} catch (Exception e) {
					log.error(
							"Error occurred while creating the request enquiry for BOB gateway for transaction :"
									+ transaction.toString(), e);
					enquiryResponse.setRespCode(ERROR_CODE_502);
					enquiryResponse.setRespMsg(e.getMessage());

				}

			}// End of iteration

			// 4. Add inquiry response to list

			if (oPgRespArr != null) {
				log.info("BOB enquiry response for transaction :"
						+ transaction.toString() + ": " + oPgRespArr);
				resultList.addAll(oPgRespArr);
			}

			// 5. Fetch transactions from citrus DB
			TransactionDAO transactionDAO = new TransactionDAOImpl();

			List<Transaction> transactions = transactionDAO
					.findByMerchantTxnIdAndGateway(
							transaction.getMerchantTxId(),
							transaction.getMerchant(), paymentGateway.getCode());

			/* Response not received from BOB, process local data */
			if (!CommonUtil.isNotEmpty(resultList)) {
				return prepareEnqiryResult(transactions, merchantRefundTxId,paymentGateway);
			}

			/*
			 * Response received from BOB PG. Format BOB inquiry response
			 */
			if (CommonUtil.isNotEmpty(resultList)) {
				for (PGResponse oPgResp : resultList) {
					EnquiryResult bean = new EnquiryResult();
					bean.setRespCode(oPgResp.getRespCode());
					bean.setRespMsg((CommonUtil.isNotNull(oPgResp.getTxnType())
							&& oPgResp.getTxnType().equalsIgnoreCase(
									TransactionType.PREAUTH.toString())
							&& CommonUtil.isNotNull(oPgResp.getRespCode()) && oPgResp
							.getRespCode().equalsIgnoreCase("0")) ? TXN_APPROVED
							: oPgResp.getRespMessage());

					bean.setTxnId(oPgResp.getTxnId());
					bean.setPgTxnId(oPgResp.getEpgTxnId());
					bean.setAuthIdCode(oPgResp.getAuthIdCode());
					bean.setRRN(oPgResp.getRRN());
					bean.setTxnType(oPgResp.getTxnType());
					bean.setTxnDateTime(JDateUtil.getPGDate(oPgResp
							.getTxnDateTime()));
					//TODO indra to understand matchTransaction and matchTransactionForAmount and have to write the code accordingly
					Transaction txn = null;/*//TODO indra matchTransaction(transactions,
							oPgResp.getEpgTxnId(), oPgResp.getRRN());
					*/
					// Update amount explicitly
					Transaction matchingTxn = null;/* TODO indra matchTransactionForAmount(
							transactions, oPgResp.getEpgTxnId(),
							oPgResp.getRRN());
					if (CommonUtil.isNotNull(matchingTxn)) {
						bean.setAmount(matchingTxn.getOrderAmount().getAmount()
								.toString());
					}

					if (CommonUtil.isNull(txn)) {
						txn = transactionDAO.getLastTxnByMtxAndMerchant(
								transaction.getMerchantTxId(),
								transaction.getMerchant());
					}
*/					updateInquiryForCardParamater(bean,txn);
					// Update payment detail
					updatePaymentDetailAndAddressDetail(txn, bean,paymentGateway);

					/**
					 * Update transaction status if mismatch with BOB
					 * transaction
					 */
					//TODO indra updatetransaction to db	
					
					// set merchant refund tx id
					bean.setMerchantRefundTxId(CommonUtil.isNotNull(txn) ? txn
							.getMerchantRefundTxId() : null);
					
					// set MTX
					bean.setMerchantTxnId(CommonUtil.isNotNull(txn) ? txn
							.getMerchantTxId() : null);

					inqueryResult.add(bean);
					log.info("Enquiry status from BOB for transaction :"
							+ transaction.toString() + " code:"
							+ oPgResp.getRespCode() + " msg:"
							+ oPgResp.getRespMessage());
				}
			}


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
			log.error(
					"Error occurred during  enquiry  for  BOB gateway for transaction :"
							+ transaction.toString(), ex);
			enquiryResponse.setRespMsg(ex.getMessage());
			enquiryResponse.setRespCode(ERROR_CODE_502);

		}


		
		
		return enquiryResponse;
	}
	@SuppressWarnings("unchecked")
	private ArrayList<PGResponse> doInquiry(String bobMerchantId,
			String mtxOrCtx) throws Exception {

		com.ecs.epg.bob.sfa.java.Merchant oMerchant = new com.ecs.epg.bob.sfa.java.Merchant();
		PostLib oPostLib = null;
		PGSearchResponse oPgSearchResp = null;
		oMerchant.setMerchantOnlineInquiry(bobMerchantId, mtxOrCtx);
		oPostLib = new PostLib("BOB");
		oPgSearchResp = oPostLib.postStatusInquiry(oMerchant);
		return oPgSearchResp.getPGResponseObjects();
	}

	
}
