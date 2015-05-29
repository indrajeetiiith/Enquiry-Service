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
import com.opus.epg.ubi.sfa.java.PGResponse;
import com.opus.epg.ubi.sfa.java.PGSearchResponse;
import com.opus.epg.ubi.sfa.java.PostLib;

public class UBIGatewayService extends GatewayServiceImpl implements
		GatewayService {

	private static final Logger log = LoggerFactory
			.getLogger(UBIGatewayService.class);

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

		log.info("UBI enquiry request for mtx: " + transaction.toString());
		// 2. Setup
		MerchantGatewaySetting pgSeettings = null;
		List<EnquiryResult> inqueryResult = new ArrayList<EnquiryResult>();
		ArrayList<PGResponse> resultList = new ArrayList<PGResponse>();

		try {



			// 3. Get PG setting
			MerchantGatewaySettingDAO gatewaySettingDAO = new MerchantGatewaySettingDAOImpl();


			pgSeettings = gatewaySettingDAO.findByMerchantAndGatewayCode(transaction.getMerchant().getId(),
					paymentGateway.getCode());

			String ubiMerchantId = pgSeettings != null ? pgSeettings
					.getMerchantUsrid() : null;

			ArrayList<PGResponse> oPgRespArr = null;
			/*
			 * First attempt is made with MTX, if no response is received then
			 * second attempt is made with CTX
			 */
			for (int attempt = 0; attempt < 2 && CommonUtil.isNull(oPgRespArr); attempt++) {

				String txnRefNo = (attempt == 0) ? transaction
						.getMerchantTxId() : transaction.getTxId();

				try {
					oPgRespArr = doInquiry(ubiMerchantId, txnRefNo);
				} catch (Exception e) {
					log.error(
							"Error occurred while creating the request enquiry for UBI gateway:"
									+ transaction.toString(), e);
					enquiryResponse.setRespCode(ERROR_CODE_502);
					enquiryResponse.setRespMsg(e.getMessage());

				}

			}// End of iteration

			if (oPgRespArr != null) {
				log.info("UBI enquiry response:" + oPgRespArr);
				resultList.addAll(oPgRespArr);
			}

			// 4. Fetch transactions from citrus DB
			TransactionDAO transactionDAO = new TransactionDAOImpl();

			List<Transaction> transactions = transactionDAO
					.findByMerchantTxnIdAndGateway(
							transaction.getMerchantTxId(),
							transaction.getMerchant(), paymentGateway.getCode());

			/* Response not received from UBI, process local data */
			if (!CommonUtil.isNotEmpty(resultList)) {
				return prepareEnqiryResult(transactions, merchantRefundTxId,paymentGateway);
			}

			/*
			 * Response received from UBI PG. Format UBI enquiry response
			 */
			if (resultList != null && !resultList.isEmpty()) {
				for (PGResponse oPgResp : resultList) {
					EnquiryResult bean = new EnquiryResult();
					bean.setRespCode(oPgResp.getRespCode());
					bean.setRespMsg(oPgResp.getRespMessage());
					bean.setTxnId(oPgResp.getTxnId());
					bean.setPgTxnId(oPgResp.getEpgTxnId());
					bean.setAuthIdCode(oPgResp.getAuthIdCode());
					bean.setRRN(oPgResp.getRRN());
					bean.setTxnType(oPgResp.getTxnType());
					if (CommonUtil.isNotNull(oPgResp.getTxnType())
							&& oPgResp.getTxnType().equalsIgnoreCase(
									TransactionType.PREAUTH.toString())) {
						if (CommonUtil.isNotNull(oPgResp.getRespCode())
								&& oPgResp.getRespCode().equalsIgnoreCase("0")) {
							bean.setRespMsg("Transaction Approved");
						}
					}
					bean.setTxnDateTime(JDateUtil.getPGDate(oPgResp
							.getTxnDateTime()));

					Transaction txn = null;/* TODO indra matchTransaction(transactions,
							oPgResp.getEpgTxnId(), oPgResp.getRRN());

					// Update status code
					if (CommonUtil.isNotNull(txn)) {

						bean.setRespCode((TransactionStatus.SUCCESS_ON_VERIFICATION
								.equals(txn.getStatus())) ? "0" : String
								.valueOf(txn.getStatus().ordinal()));
					}
*/
					/**
					 * Update transaction status if mismatch with UBI
					 * transaction
					 */
					Transaction updatedTransaction = null;/* TODO indra updateCPTransactionStatus(
							oPgResp, txn);
					if (CommonUtil.isNotNull(updatedTransaction)) {

						bean.setRespCode((TransactionStatus.SUCCESS_ON_VERIFICATION
								.equals(txn.getStatus())) ? "0" : String
								.valueOf(txn.getStatus().ordinal()));
						bean.setAmount(updatedTransaction.getOrderAmount()
								.getAmount().toString());
					}
*/
					// Update amount explicitly
					Transaction matchingTxn = null; /*TODO indra matchTransactionForAmount(
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
*/					
					updateInquiryForCardParamater(bean,txn);
					
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
			log.error("Error occurred during  enquiry  for  UBI gateway:"
					+ transaction.getMerchant().getName() + "for id:"
					+ transaction.getMerchantTxId(), ex);
			enquiryResponse.setRespCode(ERROR_CODE_502);
			enquiryResponse.setRespMsg(ex.getMessage());

		}

		
		return enquiryResponse;
	}
	@SuppressWarnings("unchecked")
	private ArrayList<PGResponse> doInquiry(String ubiMerchantId,
			String mtxOrCtx) throws Exception {

		com.opus.epg.ubi.sfa.java.Merchant oMerchant = new com.opus.epg.ubi.sfa.java.Merchant();
		PostLib oPostLib = null;
		PGSearchResponse oPgSearchResp = null;
		oMerchant.setMerchantOnlineInquiry(ubiMerchantId, mtxOrCtx);
		oPostLib = new PostLib();

		oPgSearchResp = oPostLib.postStatusInquiry(oMerchant);

		return oPgSearchResp.getPGResponseObjects();
	}


}
