package com.citruspay;

import com.citruspay.enquiry.persistence.entity.ConsumerPaymentDetail;
import com.citruspay.enquiry.persistence.entity.CreditCardPaymentDetail;
import com.citruspay.enquiry.persistence.entity.DebitCardPaymentDetail;
import com.citruspay.enquiry.persistence.entity.ImpsPaymentDetail;
import com.citruspay.enquiry.persistence.entity.NetBankingPaymentDetail;
import com.citruspay.enquiry.persistence.entity.PaymentDetail;
import com.citruspay.enquiry.persistence.entity.PaymentMode;
import com.citruspay.CommonUtil;

public class PaymentUtil {

	public static ConsumerPaymentDetail getPaymentDetailsForResponse(
			PaymentDetail paymentDetail) {
		ConsumerPaymentDetail consumerPaymentDetail = new ConsumerPaymentDetail();
		if (paymentDetail instanceof NetBankingPaymentDetail) {
			NetBankingPaymentDetail netBankingPaymentDetail = (NetBankingPaymentDetail) paymentDetail;
			consumerPaymentDetail.setBank(netBankingPaymentDetail.getBank());
			consumerPaymentDetail.setPaymentMode(PaymentMode.NET_BANKING);
		}
		if (paymentDetail instanceof CreditCardPaymentDetail) {
			CreditCardPaymentDetail creditCardPaymentDetail = (CreditCardPaymentDetail) paymentDetail;
			consumerPaymentDetail.setPaymentMode(PaymentMode.CREDIT_CARD);
			if (CommonUtil.isNotNull(creditCardPaymentDetail.getCardNumber())) {
				consumerPaymentDetail
						.setMaskedCardNumber(creditCardPaymentDetail
								.getCardNumber().replaceAll(" ", ""));
			} else {
				consumerPaymentDetail.setMaskedCardNumber("");
			}
			consumerPaymentDetail.setCardType(creditCardPaymentDetail
					.getCardType());
		}
		if (paymentDetail instanceof DebitCardPaymentDetail) {
			DebitCardPaymentDetail debitCardPaymentDetail = (DebitCardPaymentDetail) paymentDetail;
			consumerPaymentDetail.setPaymentMode(PaymentMode.DEBIT_CARD);
			if (CommonUtil.isNotNull(debitCardPaymentDetail.getCardNumber())) {
				consumerPaymentDetail
						.setMaskedCardNumber(debitCardPaymentDetail
								.getCardNumber().replace(" ", ""));
			} else {
				consumerPaymentDetail.setMaskedCardNumber("");
			}
			consumerPaymentDetail.setCardType(debitCardPaymentDetail
					.getCardType());

		}
/*TODO indra		if (paymentDetail instanceof PrepaidPaymentDetail) {
			PrepaidPaymentDetail prepaidPaymentDetail = (PrepaidPaymentDetail) paymentDetail;
			consumerPaymentDetail.setPaymentMode(PaymentMode.PREPAID_CARD);
			if (CommonUtil.isNotNull(prepaidPaymentDetail.getCardNumber())) {
				consumerPaymentDetail.setMaskedCardNumber(prepaidPaymentDetail
						.getCardNumber().replace(" ", ""));
			} else {
				consumerPaymentDetail.setMaskedCardNumber("");
			}
			consumerPaymentDetail.setCardType(prepaidPaymentDetail
					.getCardType());
		}
*/		if (paymentDetail instanceof ImpsPaymentDetail) {
			ImpsPaymentDetail impsPaymentDetail = (ImpsPaymentDetail) paymentDetail;
			consumerPaymentDetail.setPaymentMode(PaymentMode.IMPS);
			if (CommonUtil.isNotNull(impsPaymentDetail.getCardNumber())) {
				consumerPaymentDetail.setMaskedCardNumber(impsPaymentDetail
						.getCardNumber().replaceAll(" ", ""));
			} else {
				consumerPaymentDetail.setMaskedCardNumber("");
			}
			consumerPaymentDetail.setMobileNumber(impsPaymentDetail
					.getMobileNumber());
		}
		return consumerPaymentDetail;
	}


}
