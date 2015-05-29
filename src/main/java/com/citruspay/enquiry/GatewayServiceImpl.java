package com.citruspay.enquiry;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.citruspay.CommonUtil;
import com.citruspay.PaymentUtil;
import com.citruspay.enquiry.api.EnquiryResponse;
import com.citruspay.enquiry.api.EnquiryResult;
import com.citruspay.enquiry.api.EnquiryResultList;
import com.citruspay.enquiry.gateway.GatewayService;
import com.citruspay.enquiry.persistence.entity.ATMCardPaymentDetails;
import com.citruspay.enquiry.persistence.entity.Address;
import com.citruspay.enquiry.persistence.entity.ConsumerPaymentDetail;
import com.citruspay.enquiry.persistence.entity.CreditCardPaymentDetail;
import com.citruspay.enquiry.persistence.entity.DebitCardPaymentDetail;
import com.citruspay.enquiry.persistence.entity.ImpsPaymentDetail;
import com.citruspay.enquiry.persistence.entity.NetBankingPaymentDetail;
import com.citruspay.enquiry.persistence.entity.PGTransaction;
import com.citruspay.enquiry.persistence.entity.PaymentGateway;
import com.citruspay.enquiry.persistence.entity.PrepaidPaymentDetail;
import com.citruspay.enquiry.persistence.entity.Transaction;
import com.citruspay.enquiry.persistence.entity.TransactionHistory;
import com.citruspay.enquiry.persistence.entity.TransactionStatus;
import com.citruspay.enquiry.persistence.entity.TransactionType;
import com.citruspay.enquiry.type.GatewayType;
public abstract class GatewayServiceImpl implements GatewayService{
	
//TODO indra	@Value("${merchant.custom.response.param.service.enabled.list}")
	private String merchantIds;

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

						
					} else if (txn.getPaymentDetails() instanceof PrepaidPaymentDetail) {
						PrepaidPaymentDetail paymntDetails = (PrepaidPaymentDetail) txn
								.getPaymentDetails();

						ConsumerPaymentDetail conPaymntDetails = PaymentUtil
								.getPaymentDetailsForResponse(paymntDetails);

						bean.setMaskedCardNumber(conPaymntDetails
								.getMaskedCardNumber());

					} else if (txn.getPaymentDetails() instanceof ImpsPaymentDetail) {
						ImpsPaymentDetail paymntDetails = (ImpsPaymentDetail) txn
								.getPaymentDetails();

						ConsumerPaymentDetail conPaymntDetails = PaymentUtil
								.getPaymentDetailsForResponse(paymntDetails);

						bean.setImpsMmid(conPaymntDetails.getMmid());
						bean.setImpsMobileNumber(conPaymntDetails
								.getMobileNumber());
					}
					 else if (txn.getPaymentDetails() instanceof ATMCardPaymentDetails) {
							ATMCardPaymentDetails atmCardDetails = (ATMCardPaymentDetails) txn
									.getPaymentDetails();
							
							bean.setCardType(atmCardDetails.getCardType());
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
	protected EnquiryResponse prepareEnqiryResult(List<Transaction> transactions, String merchantRefundTxId,PaymentGateway pg) {
		List<EnquiryResult> enqiryResult = new ArrayList<EnquiryResult>();
		if (CommonUtil.isNotEmpty(transactions)) {
			for (Transaction tx : transactions) {
				EnquiryResult bean = createInquiryBean(tx, null,pg);
				enqiryResult.add(bean);
			}
		}

		EnquiryResponse responseBean = new EnquiryResponse();
		responseBean.setRespCode(RESP_CODE_SUCCESS);
		responseBean.setRespMsg(ENQUIRY_SUCCESSFULL);
		EnquiryResultList txnEnquiryResponse = fillEnquiryResponse(
				merchantRefundTxId, enqiryResult);
		responseBean.setData(txnEnquiryResponse);
		return responseBean;
	}
	public EnquiryResultList fillEnquiryResponse(String merchantRefundTxId,
			List<EnquiryResult> enquiryResultList) {
		EnquiryResultList txnEnquiryResponse = null;
		if (!CommonUtil.isEmpty(merchantRefundTxId)) {
			List<EnquiryResult> finalEnquiryResultList = new ArrayList<EnquiryResult>();
			for (EnquiryResult enquiryResult : enquiryResultList) {
				if (enquiryResult.getTxnType().equalsIgnoreCase(
						TransactionType.REFUND.toString())
						&& CommonUtil.isNotNull(enquiryResult.getMerchantRefundTxId())
						&& enquiryResult.getMerchantRefundTxId().equalsIgnoreCase(
								merchantRefundTxId)) {
					finalEnquiryResultList.add(enquiryResult);
				}
			}
			txnEnquiryResponse = new EnquiryResultList(finalEnquiryResultList);
		} else {
			txnEnquiryResponse = new EnquiryResultList(enquiryResultList);
		}
		return txnEnquiryResponse;
	}

	
	protected EnquiryResult createInquiryBean(Transaction tx,
			TransactionHistory txnHistory,PaymentGateway pg) {
		EnquiryResult bean = new EnquiryResult();

		String respCode = "0";
		if (!TransactionStatus.SUCCESS_ON_VERIFICATION.equals(tx.getStatus())) {
			respCode = String.valueOf(tx.getStatus().ordinal());
		}
		String respMsg = null;

		if (tx.getStatus().equals(TransactionStatus.SUCCESS)
				|| tx.getStatus().equals(
						TransactionStatus.SUCCESS_ON_VERIFICATION)) {

			if (CommonUtil.isNotNull(tx.getTransactionType())
					&& tx.getTransactionType()
							.toString()
							.equalsIgnoreCase(
									TransactionType.PREAUTH.toString())) {
				respMsg = TXN_SEARCH_APPROVED;
			} else {
				respMsg = tx.getStatus().getDisplayMsg();
			}
		} else {
			respMsg = tx.getStatus().getDisplayMsg();
		}

		bean.setRespCode(respCode);
		bean.setRespMsg(respMsg);
		bean.setTxnId(tx.getTxId());
		bean.setTxnDateTime(CommonUtil.getDateStringInIST(tx.getCreated()));
		bean.setAmount(tx.getOrderAmount().getAmount().toString());
		bean.setCurrency(tx.getOrderAmount().getCurrency());
		bean.setTxnType(tx.getTransactionType().name());
		//set merchantRefundTxId from transaction
		bean.setMerchantRefundTxId(tx.getMerchantRefundTxId());
		// set MTX
		bean.setMerchantTxnId(tx.getMerchantTxId());
		
		// Update payment detail
		updatePaymentDetailAndAddressDetail(tx, bean,pg);
		PGTransaction pgTxn = tx.getPgTxResp();
		if (pgTxn != null) {
			bean.setPgTxnId(pgTxn.getPgTxnId());
			bean.setAuthIdCode(pgTxn.getAuthIdCode());
			bean.setRRN(pgTxn.getIssuerRefNo());
			
			updateInquiryForCardParamater(bean,tx);
		}
		if (PaymentUtil.isStatusCanclSessionExpireInProgressCOD(tx.getStatus())) {
			bean.setPgTxnId(tx.getTxId());
		}
		updatePricingTransactionHistory(tx, bean);

		return bean;
	}
	private boolean isMerchantCardInfoServiceEnabled(int merchantId){
		if (!StringUtils.isEmpty(merchantIds)) {
			StringTokenizer st = new StringTokenizer(merchantIds, ",");
			while (st.hasMoreElements()) {
				if (Integer.parseInt((String) st.nextElement()) == merchantId) {
					return true;
				}
			}
		}
		return false;
	}

public void updateInquiryForCardParamater(EnquiryResult inquiryBean, Transaction transaction){
		
		boolean cardInfoServiceEnabled = isMerchantCardInfoServiceEnabled(transaction.getMerchant().getId().intValue());
		if (transaction.getPaymentDetails() instanceof CreditCardPaymentDetail) {
			CreditCardPaymentDetail ccPaymentDetails = (CreditCardPaymentDetail) transaction.getPaymentDetails();
			if(ccPaymentDetails!=null){
				inquiryBean.setCardHolderName(ccPaymentDetails.getCardName());
			}			
			if(ccPaymentDetails!=null && cardInfoServiceEnabled){
					//inquiryBean.setEncryptedCardNumber("");
					inquiryBean.setCardExpiryMonth(ccPaymentDetails.getExpiryMonth());
					inquiryBean.setCardExpiryYear(ccPaymentDetails.getExpiryYear());
					//inquiryBean.setCardIssuingBank("");
			}
		}else if (transaction.getPaymentDetails() instanceof DebitCardPaymentDetail) {
			DebitCardPaymentDetail dcPaymentDetails = (DebitCardPaymentDetail) transaction	.getPaymentDetails();
			if(dcPaymentDetails!=null){
				inquiryBean.setCardHolderName(dcPaymentDetails.getCardName());
			}
			if(dcPaymentDetails!=null && cardInfoServiceEnabled){
					//inquiryBean.setEncryptedCardNumber("");
					inquiryBean.setCardExpiryMonth(dcPaymentDetails.getExpiryMonth());
					inquiryBean.setCardExpiryYear(dcPaymentDetails.getExpiryYear());
					//inquiryBean.setCardIssuingBank("");
				}
		}	
		//inquiryBean.setCardInfoServiceEnabled(cardInfoServiceEnabled);
		if(CommonUtil.isNotNull(transaction.getPgTxResp())){
			inquiryBean.setEciValue(transaction.getPgTxResp().getEciValue());
			inquiryBean.setThreeDsecure(transaction.getPgTxResp().getThreeDSecure());
		}
		TransactionType type = transaction.getTransactionType();
		inquiryBean.setTxnType(CommonUtil.isNotNull(type) ? type.toString() : "");
		
		return;
	}

	

	public boolean isRequiredStatus(TransactionHistory txnHistory) {
		Boolean isRequiredStatus = Boolean.TRUE;
		if (CommonUtil.isNotNull(txnHistory.getStatus())) {
			if (TransactionStatus.CANCELED.equals(txnHistory.getStatus())
					|| TransactionStatus.SESSION_EXPIRED.equals(txnHistory
							.getStatus())) {
				isRequiredStatus = Boolean.FALSE;
			}
		}
		return isRequiredStatus;
	}

	protected boolean isRefundableTransaction(Transaction transaction) {

		return (CommonUtil.isNotNull(transaction) && (transaction
				.getTransactionType().toString()
				.equals(TransactionType.SALE.toString())
				|| transaction.getTransactionType().toString()
						.equals(TransactionType.PREAUTH.toString())
				|| transaction.getTransactionType().toString()
						.equals(TransactionType.LOAD.toString()) || transaction
				.getTransactionType().toString()
				.equals(TransactionType.RELOAD.toString()))) ? Boolean.TRUE
				: Boolean.FALSE;
	}

	protected String null2unknown(String in, Map<String, String> responseFields) {
		if (CommonUtil.isNull(in) || in.length() == 0
				|| CommonUtil.isNull((String) responseFields.get(in))) {
			return "No Value Returned";
		} else {
			return (String) responseFields.get(in);
		}
	}


	/**
	 * This function tells whether there is error in the response string
	 * @param input
	 * @return
	 */
	protected boolean isErrorInResponse(String input) {
		if (StringUtils.isEmpty(input)) {
			return Boolean.TRUE;
		}
		Pattern pattern = Pattern.compile(ERROR_REGEX);
		Matcher matcher = pattern.matcher(input);
		return (matcher.find()) ? Boolean.TRUE : Boolean.FALSE;
	}

	
	
}



