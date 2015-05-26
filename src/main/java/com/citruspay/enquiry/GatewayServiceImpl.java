package com.citruspay.enquiry;


import com.citruspay.CommonUtil;
import com.citruspay.PaymentUtil;
import com.citruspay.enquiry.api.EnquiryResult;
import com.citruspay.enquiry.persistence.entity.Address;
import com.citruspay.enquiry.persistence.entity.ConsumerPaymentDetail;
import com.citruspay.enquiry.persistence.entity.CreditCardPaymentDetail;
import com.citruspay.enquiry.persistence.entity.DebitCardPaymentDetail;
import com.citruspay.enquiry.persistence.entity.ImpsPaymentDetail;
import com.citruspay.enquiry.persistence.entity.NetBankingPaymentDetail;
import com.citruspay.enquiry.persistence.entity.PaymentGateway;
import com.citruspay.enquiry.persistence.entity.Transaction;
import com.citruspay.enquiry.persistence.entity.TransactionStatus;
import com.citruspay.enquiry.type.GatewayType;
public class GatewayServiceImpl {
	
	public void updatePaymentDetailAndAddressDetail(Transaction txn, EnquiryResult bean,PaymentGateway pg) {

		if (CommonUtil.isNotNull(txn) && isRequiredStatus(txn)) {
			if (CommonUtil.isNotNull(txn.getTxnGateway())) {
				String transactionGatewayName = pg.getName().toString();
				bean.setTxnGateway(transactionGatewayName);

				if (CommonUtil.isNotNull(txn.getPaymentDetails())) {

					if (txn.getPaymentDetails() instanceof CreditCardPaymentDetail) {
						CreditCardPaymentDetail paymntDetails = (CreditCardPaymentDetail) txn
								.getPaymentDetails();
						ConsumerPaymentDetail conPaymntDetails = PaymentUtil
								.getPaymentDetailsForResponse(paymntDetails);

						bean.setMaskedCardNumber(conPaymntDetails
								.getMaskedCardNumber());
						bean.setCardType(paymntDetails.getCardType());
					} else if (txn.getPaymentDetails() instanceof DebitCardPaymentDetail) {
						DebitCardPaymentDetail paymntDetails = (DebitCardPaymentDetail) txn
								.getPaymentDetails();

						ConsumerPaymentDetail conPaymntDetails = PaymentUtil
								.getPaymentDetailsForResponse(paymntDetails);

						bean.setMaskedCardNumber(conPaymntDetails
								.getMaskedCardNumber());
						bean.setCardType(paymntDetails.getCardType());
					} else if (txn.getPaymentDetails() instanceof NetBankingPaymentDetail) {
						NetBankingPaymentDetail paymntDetails = (NetBankingPaymentDetail) txn
								.getPaymentDetails();
						bean.setIssuerCode(paymntDetails.getBank().getCode());

					} else if (txn.getPaymentDetails() instanceof ImpsPaymentDetail) {
						ImpsPaymentDetail paymntDetails = (ImpsPaymentDetail) txn
								.getPaymentDetails();

						ConsumerPaymentDetail conPaymntDetails = PaymentUtil
								.getPaymentDetailsForResponse(paymntDetails);

						bean.setImpsMmid(conPaymntDetails.getMmid());
						bean.setImpsMobileNumber(conPaymntDetails
								.getMobileNumber());
					}
					bean.setPaymentMode(txn.getPaymentDetails()
							.getPaymentMode().toString());
				}
			}
		}


		// update currency
		bean.setCurrency(txn.getOrderAmount().getCurrency());

		// update Pricing Transaction History if present
		updatePricingTransactionHistory(txn, bean);
		
		addAddressDetails(bean, txn);

	}
	public void updatePricingTransactionHistory(Transaction txn,
			EnquiryResult bean) {
		if (CommonUtil.isNotNull(txn)
				&& CommonUtil.isNotNull(txn.getPricingTransactionHistory())) {
			bean.setOriginalAmount(CommonUtil.isNotNull(txn
					.getPricingTransactionHistory().getOriginalAmount()) ? txn
					.getPricingTransactionHistory().getOriginalAmount()
					.toString() : null);
			bean.setAdjustment(CommonUtil.isNotNull(txn
					.getPricingTransactionHistory().getAdjustment()) ? txn
					.getPricingTransactionHistory().getAdjustment().toString()
					: null);
			String ruleName = null;
			String couponCode = null;
			String ruleType = null;
			boolean isCitrusSponsored = Boolean.FALSE;
			if (CommonUtil.isNotNull(txn.getPricingTransactionHistory()
					.getPricingRule())) {
				ruleName = txn.getPricingTransactionHistory().getPricingRule()
						.getName();
				couponCode = txn.getPricingTransactionHistory().getPricingRule().getIsCoupon() == 1 ? ruleName : "";
				ruleType = txn.getPricingTransactionHistory().getPricingRule()
						.getOfferType().getDisplayLabel();
				if (CommonUtil.ONE == txn.getPricingTransactionHistory()
						.getPricingRule().getIsCitrusSponsored()) {
					isCitrusSponsored = Boolean.TRUE;
				}
			}
			bean.setRuleName(ruleName);
			//currently the rule name and the coupon code are one and the same
			bean.setCouponCode(couponCode);
			bean.setOfferType(ruleType);

			bean.setTransactionAmount((CommonUtil.isNotNull(txn
					.getPricingTransactionHistory().getTxnAmount())) ? txn
					.getPricingTransactionHistory().getTxnAmount().toString()
					: null);
			bean.setAmount(isCitrusSponsored ? txn
					.getPricingTransactionHistory().getOriginalAmount()
					.toString() : bean.getAmount());
		}
	}



	private void addAddressDetails(EnquiryResult bean,
			Transaction transaction) {
		if (CommonUtil.isNotNull(transaction)
				&& CommonUtil.isNotNull(transaction.getConsumerDetail())) {
			Address addr = transaction.getConsumerDetail().getContactAddress();
			if (CommonUtil.isNotNull(addr)) {
				bean.setAddress(addr.getAddressStreet1(),
						addr.getAddressStreet2(), addr.getAddressCity(),
						addr.getAddressState(), addr.getAddressCountry(),
						addr.getAddressZip());
			}
		}
	}

	public boolean isRequiredStatus(Transaction txn) {
		Boolean isRequiredStatus = Boolean.TRUE;
		if (CommonUtil.isNotNull(txn.getStatus())) {
			if (TransactionStatus.CANCELED.equals(txn.getStatus())
					|| TransactionStatus.SESSION_EXPIRED
							.equals(txn.getStatus())) {
				isRequiredStatus = Boolean.FALSE;
			}
		}
		return isRequiredStatus;
	}

	public boolean isExternalGateway(PaymentGateway paymentGateway) {

		return (CommonUtil.isNotNull(paymentGateway) && (GatewayType.EXTERNAl
				.toString()).equals(paymentGateway.getGatewayType().toString())) ? Boolean.TRUE
				: Boolean.FALSE;
	}

/*	private TransactionHistory updatePaymentResponseToTransactionHistory(
			InquiryBean bean, Transaction transaction,
			TransactionHistory transactionHistoryOld, boolean inqServerCommSts) {
		TransactionHistory transactionHistory = transactionHistoryService
				.findById(transactionHistoryOld.getId());

		PGTransaction pgTransaction = transactionHistory.getPgTxResp() != null ? transactionHistory
				.getPgTxResp() : new PGTransaction();

		String responseCode = bean.getRespCode();
		String responseMessage = bean.getRespMsg();
		String authIdCode = bean.getAuthIdCode();
		String rrn = bean.getRRN();
		String txnId = bean.getTxnId();
		String pgTxnId = bean.getPgTxnId();
		String cvRespCode = bean.getCvResponseCode();

		// if not available will be updated by null
		if (responseCode != null) {
			pgTransaction.setResponseCode(responseCode);
		}
		if (responseMessage != null) {
			pgTransaction.setMessage(responseMessage);
		}
		if (authIdCode != null) {
			pgTransaction.setAuthIdCode(authIdCode);
		}
		if (rrn != null) {
			pgTransaction.setIssuerRefNo(rrn);
		}

		if (pgTxnId != null) {
			pgTransaction.setPgTxnId(pgTxnId);
		}

		if (cvRespCode != null) {
			pgTransaction.setCvRespCode(cvRespCode);
		}

		if (!CommonUtil.isEmpty(txnId)) {
			pgTransaction.setTxnId(txnId);
		} else {
			pgTransaction.setTxnId(transactionHistory.getTxId());
		}

		if (responseCode != null) {
			if ("0".equalsIgnoreCase(responseCode)) {
				pgTransaction.setErrorMessage(null);
			}
		}

		transactionHistory.setPgTxResp(pgTransaction);

		int pgResponseCode = CommonUtil.getInteger(responseCode, -1);

		if (pgResponseCode != 0) {
			transactionHistory.setStatus(TransactionStatus.FAIL);
		} else {
			transactionHistory
					.setStatus(TransactionStatus.SUCCESS_ON_VERIFICATION);
			if (CommonUtil.isEmpty(responseMessage)) {
				pgTransaction.setMessage("Transaction Successful");
			}
		}

		transactionHistoryService.saveOrUpdate(transactionHistory);

		if (transactionHistory.getTxId().equals(transaction.getTxId())
				&& (CommonUtil.isNotNull(transaction) && transaction
						.getStatus().ordinal() != pgResponseCode)
				&& !(transaction.getStatus()
						.equals(TransactionStatus.FORWARDED) && pgResponseCode != 0)) {

			transaction = transactionService.findById(transaction.getId());
			if (pgResponseCode != 0) {
				transaction.setStatus(TransactionStatus.FAIL);
			} else {
				transaction
						.setStatus(TransactionStatus.SUCCESS_ON_VERIFICATION);
			}
			transaction.setPgTxResp(transactionHistory.getPgTxResp());
			transactionService.saveOrUpdate(transaction);
		}
		return transactionHistory;
	}

*/	
}



