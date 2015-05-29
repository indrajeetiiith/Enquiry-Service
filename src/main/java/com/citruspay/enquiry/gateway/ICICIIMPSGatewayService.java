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
import com.opus.epg.idebit.sfa.java.PGResponse;
import com.opus.epg.idebit.sfa.java.PGSearchResponse;
import com.opus.epg.idebit.sfa.java.PostLib;


public class ICICIIMPSGatewayService extends GatewayServiceImpl implements
		GatewayService {

	private static final Logger log = LoggerFactory.getLogger(PNBATMGatewayService.class);

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

		log.info("ICICI IMPS PG enquiry API called for transaction: "
				+ transaction.toString());

		List<EnquiryResult> inqueryResult = new ArrayList<EnquiryResult>();
		ArrayList<PGResponse> resultList = new ArrayList<PGResponse>();

		// Get PG detail

		// 2. Setup
		MerchantGatewaySettingDAO gatewaySettingDAO = new MerchantGatewaySettingDAOImpl();

		MerchantGatewaySetting pgSetting = gatewaySettingDAO.findByMerchantAndGatewayCode(transaction.getMerchant().getId(),
				paymentGateway.getCode());

		/* Find transaction from citrus system */
		TransactionDAO transactionDAO = new TransactionDAOImpl();

		List<Transaction> transactions = transactionDAO
				.findByMerchantTxnIdAndGateway(transaction.getMerchantTxId(),
						transaction.getMerchant(), paymentGateway.getCode());

		if (CommonUtil.isNotEmpty(transactions)) {

			for (Transaction txn : transactions) {

				if (!TransactionType.REFUND.equals(txn.getTransactionType())) {

					try {
						// 4. Get ICICI IMPS merchant ID
						String iciciMerchantId = pgSetting != null ? pgSetting
								.getMerchantUsrid() : null;

						// 5. Setup inquiry request

						String mtxCTXAttemptType = MTX_STRING;
						ArrayList<PGResponse> oPgRespArr = null;

						/*
						 * First attempt is made with MTX, if no response is
						 * received then second attempt is made with CTX
						 */
						for (int attemp = 0; attemp < 2
								&& CommonUtil.isNull(oPgRespArr); attemp++) {
							if (attemp > 0) {
								mtxCTXAttemptType = CTX_STRING;
							}

							try {
								/** Inquiry response */
								oPgRespArr = doInquiry(
										iciciMerchantId,
										(attemp == 0) ? transaction
												.getMerchantTxId()
												: transaction.getTxId(),
										mtxCTXAttemptType);

							} catch (Exception e) {
								log.error(
										"Error occurred while creating the request enquiry for ICICI IMPS gateway for merchant :"
												+ transaction.getMerchant()
														.getName()
												+ "for"
												+ mtxCTXAttemptType
												+ " : "
												+ transaction.getMerchantTxId(),
										e);
								enquiryResponse.setRespCode(ERROR_CODE_502);
								enquiryResponse.setRespMsg(e.getMessage());
								return enquiryResponse;

							}
						}// End of iteration

						// 6. Add response to list
						if (CommonUtil.isNotNull(oPgRespArr)) {
							log.info("ICICI IMPS enquiry response:"
									+ oPgRespArr);
							resultList.addAll(oPgRespArr);
						}

					} catch (Exception ex) {
						log.error(
								"Error occurred during  enquiry  for  ICICI IMPS gateway:"
										+ transaction.getMerchant().getName()
										+ "for id:"
										+ transaction.getMerchantTxId(), ex);
						enquiryResponse.setRespCode(ERROR_CODE_502);
						enquiryResponse.setRespMsg(ex.getMessage());
						return enquiryResponse;
					}

				} else {
					EnquiryResult bean = createInquiryBean(transaction, null,paymentGateway);
					inqueryResult.add(bean);
				}
			}

		}

		// 8. Prepare response

		// 8.1 No response received form ICICI IMPS
		if (!CommonUtil.isNotEmpty(resultList)) {
			return prepareEnqiryResult(transactions, merchantRefundTxId,paymentGateway);
		}

		// 8.2 Response received from ICICI IMPS
		for (PGResponse oPgResp : resultList) {
			EnquiryResult bean = new EnquiryResult();
			bean.setRespCode(oPgResp.getRespCode());
			bean.setRespMsg(oPgResp.getRespMessage());
			bean.setTxnId(oPgResp.getTxnId());
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

			Transaction txn1 = null;/* TODO indra matchTransaction(transactions,
					oPgResp.getEpgTxnId(), oPgResp.getRRN());
*/
			if (CommonUtil.isNull(txn1)) {
				txn1 = transactionDAO.getLastTxnByMtxAndMerchant(
						transaction.getMerchantTxId(),
						transaction.getMerchant());
			}

			if (CommonUtil.isNotNull(txn1)) {
				// Update status code
				bean.setRespCode((TransactionStatus.SUCCESS_ON_VERIFICATION
						.equals(txn1.getStatus())) ? "0" : String.valueOf(txn1
						.getStatus().ordinal()));

				// Update amount explicitly
				bean.setAmount(txn1.getOrderAmount().getAmount().toString());
				bean.setCurrency(txn1.getOrderAmount().getCurrency());
			}

			// Update payment detail
			updatePaymentDetailAndAddressDetail(txn1, bean,paymentGateway);

			// Update transaction status if mismatch with ICICI IMPS transaction
			Transaction updatedTxn = null;/*TODO indra updateCPTransactionStatus(oPgResp, txn1);
			if (CommonUtil.isNotNull(updatedTxn)) {
				bean.setRespCode((TransactionStatus.SUCCESS_ON_VERIFICATION
						.equals(updatedTxn.getStatus())) ? "0" : String
						.valueOf(updatedTxn.getStatus().ordinal()));
			}*/

			// set merchant refund tx id
			bean.setMerchantRefundTxId(CommonUtil.isNotNull(txn1) ? txn1
					.getMerchantRefundTxId() : null);
			// set MTX
			bean.setMerchantTxnId(CommonUtil.isNotNull(txn1) ? txn1
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


	
	
	}
	/**This function does the enquiry call
	 * @param iciciMerchantId
	 * @param mtxOrCtx
	 * @param mtxCTXAttemptType
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<PGResponse> doInquiry(String iciciMerchantId,
			String mtxOrCtx, String mtxCTXAttemptType) throws Exception {

		// 5. Setup inquiry request
		com.opus.epg.idebit.sfa.java.Merchant oMerchant = new com.opus.epg.idebit.sfa.java.Merchant();

		PostLib oPostLib = null;
		PGSearchResponse oPgSearchResp = null;

		oMerchant.setMerchantOnlineInquiry(iciciMerchantId, mtxOrCtx);

		log.info("ICICI IMPS enquiry request for" + mtxCTXAttemptType + " : "
				+ mtxOrCtx);

		oPostLib = new PostLib();
		oPgSearchResp = oPostLib.postStatusInquiry(oMerchant);

		/** Inquiry response */
		return oPgSearchResp.getPGResponseObjects();
	}


}
