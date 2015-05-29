package com.citruspay.enquiry.gateway;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.citruspay.enquiry.persistence.entity.ConsumerDetail;
import com.citruspay.enquiry.persistence.entity.Transaction;
import com.citruspay.enquiry.persistence.entity.PGCatCredential;
import com.citruspay.enquiry.persistence.entity.PaymentMode;
import com.citruspay.enquiry.persistence.entity.TransactionStatus;
import com.citruspay.CommonUtil;
import com.billdesk.pgidsk.PGIUtil;


/**
 * Utility class for BillDesk Payment Gateway
 * @author Admin
 *
 */
public class BillDeskUtil {

	private static final String NOT_APPLICABLE = "NA";
	private static final String TYPE_FIELD1 = "R";
	private static final String TYPE_FIELD2 = "F";
	private static final String WEB_PORTAL = "WEB";
	private String PIPE = "|";
	private static final String secretKey = "S2H9GXDOJnuy";
	private static final String REFUND_REQUEST_TYPE = "0400";
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(BillDeskUtil.class);

	/**
	 * This method is for creating a URL POST data string.
	 * 
	 * @param queryString is the input String from POST data response
	 * @return is a Hashmap of Post data response inputs
	 */
	public Map<String, String> createMapFromResponse(String queryString) {
		Map<String, String> map = new HashMap<String, String>();
		String pgRespCode = null;
		String pgMessage = null;
		String[] tokens = queryString.split("\\|");

		map.put("requestType", tokens[0]);
		map.put("merchantId", tokens[1]);
		map.put("merchantTxId", tokens[2]);
		map.put("PGTxnRefNo", tokens[3]);
		map.put("IssuerRefNo", tokens[4]);
		map.put("AuthIdCode", tokens[4]);
		map.put("txnAmount", tokens[5]);
		map.put("AuthStatus", tokens[15]);
		map.put("refundStatus", tokens[27]);
		map.put("totalRefundAmount", tokens[28]);
		map.put("lastRefundDate", tokens[29]);
		map.put("lastRefundRefNo", tokens[30]);
		map.put("queryStatus", tokens[31]);

		String checksum = tokens[32];

		int lastIndex = queryString.lastIndexOf("|");
		String messageWithoutChecksum = queryString.substring(0, lastIndex);
		Boolean verifyChecksum = verifyChecksum(messageWithoutChecksum,
				checksum);

		if (verifyChecksum) {
			pgRespCode = "0";
			pgMessage = "Transaction Successful";
		} else {
			// Error transaction case
			pgRespCode = "1";
			pgMessage = "Transaction Failure";
		}
		map.put("pgRespCode", pgRespCode);
		map.put("pgMessage", pgMessage);

		return map;
	}


	/**
	 * creates delimeter seperated request string for Inquiry request 
	 * @param transaction
	 * @param pgCatCredential
	 * @return
	 */
	public Map<String, String> getEnquiryRequestParameterList(
			Transaction transaction, PGCatCredential pgCatCredential) {
		Map<String, String> fields = new HashMap<String, String>();
		StringBuilder enquiryMsg = new StringBuilder();
		enquiryMsg.append(pgCatCredential.getAccessCode())
				.append(PIPE).append(pgCatCredential.getMid()).append(PIPE)
				.append(transaction.getMerchantTxId()).append(PIPE)
				.append(getDate());
		
		String queryChecksum = generateChecksum(enquiryMsg.toString());
		
		enquiryMsg.append(PIPE).append(queryChecksum);

		fields.put("msg", enquiryMsg.toString());

		return fields;
	}

	private String getDate() {
		String formattedDate;
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		formattedDate = format.format(date);
		return formattedDate;
	}
	/**
	 * generates checksum of passed message string
	 * @param message
	 * @return generated checksum
	 */
	public String generateChecksum(String message) {
		
		String checksum = PGIUtil.doDigest(message, secretKey);
		LOGGER.info("Cheksum generated: {}" , checksum);
		return checksum;
	}
	/**
	 * verifies checksum comes in BillDesk response.
	 * @param message response parameters except checksum.
	 * @param checksum checksum comes in response.
	 * @return
	 */
	public Boolean verifyChecksum(String message, String checksum) {
		
		String newChecksum = PGIUtil.doDigest(message, secretKey);
		
		return (newChecksum.equals(checksum)) ? true : false;
	}



	

}
