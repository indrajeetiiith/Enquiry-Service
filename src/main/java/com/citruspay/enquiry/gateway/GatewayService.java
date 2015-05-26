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
	String RESP_CODE_2 = "2";
	String RESP_CODE_0 = "0";
	String RESP_CODE_1 = "1";
	String BANK_NAME = "ABC";
	String ADDR_COUNTRY = "INDIA";
	String GATEWAY_OPUS = "OPUS";
	String INVOICE_EMAIL_SENT_SUCCESS = "Invoice email sent successfully";
	String TXN_INVOICE_SUCCESSFULL = "Invoice transaction successful";
	String SIMPLE_DATE_FORMAT = "dd/mm/yyyy";
	String REFUND_FLAG = "R";
	String CURRENCY_INR = "356";
	String STATUS_SUCCESS = "SUCCESS";
	String STATUS_CAPTURED = "CAPTURED";
	String STATUS_APPROVED = "APPROVED";
	String TYPE_SALE = "Sale";
	String TYPE_CAPTURE = "Sale";
	String STATUS_AMOUNT_ERROR_CODE = "GW00181";
	String TXN_FAILURE = "Transaction failure";
	String TXN_SEARCH_APPROVED = "Transaction approved";


	public String INQUIRY_ACTION_CODE = "8";


	public EnquiryResponse enquiry(Transaction transaction,
			String merchantRefundTxId,PaymentGateway pg);
}
