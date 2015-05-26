package com.citruspay.enquiry.gateway;

import com.citruspay.enquiry.api.EnquiryResponse;
import com.citruspay.enquiry.persistence.entity.PaymentGateway;
import com.citruspay.enquiry.persistence.entity.Transaction;

public interface GatewayService{
	int ERROR_CODE_502 = 502;
	int RESP_CODE_SUCCESS = 200;
	String ENQUIRY_SUCCESSFULL = "Enquiry successful";
	String ENQUIRY_NOT_PRESENT = "Transaction does not exist";
	String REFUND_NOT_PRESENT = "Transaction does not exist";
	String ENQUIRY_FAILURE = "Enquiry failure";

	public String INQUIRY_ACTION_CODE = "8";


	public EnquiryResponse enquiry(Transaction transaction,
			String merchantRefundTxId,PaymentGateway pg);
}
