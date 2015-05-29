package com.citruspay.enquiry.gateway;

import java.util.HashMap;
import java.util.Map;

import com.citruspay.enquiry.api.EnquiryResponse;
import com.citruspay.enquiry.persistence.entity.PaymentGateway;
import com.citruspay.enquiry.persistence.entity.Transaction;

public interface GatewayService{
	int BAD_REQUEST = 400;
	int ERROR_CODE_502 = 502;
	int RESP_CODE_SUCCESS = 200;
	String STR_REFUND = "Refund";

	String ENQUIRY_SUCCESSFULL = "Enquiry successful";
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
	String MTX_STRING = "MTX";
	String CTX_STRING = "CTX";
	String TXN_APPROVED = "Transaction Approved";
	String ENQUIRY_NOT_PRESENT = "Transaction does not exist";
	String ERROR_REGEX = "!ERROR!";
	String ENQ_ACTION = "8";
	boolean TRUE = true;
	boolean FALSE = false;

	String VPC_VERSION = "1";

	String VPC_ENQUIRY_COMMAND = "queryDR";

	String VPC_REFUND_COMMAND = "refund";

	String VPC_CAPTURE_COMMAND = "capture";


	String INQUIRY_ACTION_CODE = "8";

	String AXIS_PG_WRONG_CREDENTIALS_RETURN_CODE = "7";
	String NO_VALUE_RETURNED = "No Value Returned";
	String MIGS_PG_WRONG_CREDENTIALS_RETURN_CODE = "7";

	String TIME_ZONE_UTC = "UTC";
	public static final String CURRENCY_CODE_INR = "356";// 356 is for INR

	public static final Map<String, String> CURRENCY_CODE = new HashMap<String, String>() {
		{
			put("INR", "356");
			put("USD", "840");
			put("EUR", "978");
			put("GBP", "826");
			put("LKR", "144");
			put("MYR", "458");
			put("SGD", "702");
			put("CAD", "124");
			put("SAR", "682");
			put("KWD", "414");
			put("HKD", "344");
		}
	};

	
	public EnquiryResponse enquiry(Transaction transaction,
			String merchantRefundTxId,PaymentGateway pg);
}
