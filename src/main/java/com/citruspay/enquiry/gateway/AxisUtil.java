/*
 * Copyright (c) 2012 CitrusPay. All Rights Reserved.
 *
 * This software is the proprietary information of CitrusPay.
 * Use is subject to license terms.
 */

package com.citruspay.enquiry.gateway;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import com.citruspay.CommonUtil;
import com.citruspay.enquiry.persistence.entity.TransactionStatus;

public class AxisUtil {
	
	/*
	 * This function uses the returned status code retrieved from the Digital
	 * Response and returns an appropriate description for the code
	 * 
	 * @param vResponseCode String containing the vpc_TxnResponseCode
	 * 
	 * @return description String containing the appropriate description
	 */
	public static String getResponseDescription(String vResponseCode) {

		String result = "";

		// check if a single digit response code
		if (vResponseCode.length() == 1) {

			// Java cannot switch on a string so turn everything to a char
			char input = vResponseCode.charAt(0);

			switch (input) {
			case '0':
				result = "Transaction Successful";
				break;
			case '1':
				result = "Unknown Error";
				break;
			case '2':
				result = "Bank Declined Transaction";
				break;
			case '3':
				result = "No Reply from Bank";
				break;
			case '4':
				result = "Expired Card";
				break;
			case '5':
				result = "Insufficient Funds";
				break;
			case '6':
				result = "Error Communicating with Bank";
				break;
			case '7':
				result = "Payment Server System Error";
				break;
			case '8':
				result = "Transaction Type Not Supported";
				break;
			case '9':
				result = "Bank declined transaction (Do not contact Bank)";
				break;
			case 'A':
				result = "Transaction Aborted";
				break;
			case 'C':
				result = "Transaction Cancelled";
				break;
			case 'D':
				result = "Deferred transaction has been received and is awaiting processing";
				break;
			case 'E':
				result = "Transaction declined - Refer to card issuer";
				break;
			case 'F':
				result = "3D Secure Authentication failed";
				break;
			case 'I':
				result = "Card Security Code verification failed";
				break;
			case 'L':
				result = "Shopping Transaction Locked (Please try the transaction again later)";
				break;
			case 'N':
				result = "Cardholder is not enrolled in Authentication Scheme";
				break;
			case 'P':
				result = "Transaction has been received by the Payment Adaptor and is being processed";
				break;
			case 'R':
				result = "Transaction was not processed - Reached limit of retry attempts allowed";
				break;
			case 'S':
				result = "Duplicate SessionID (OrderInfo)";
				break;
			case 'T':
				result = "Address Verification Failed";
				break;
			case 'U':
				result = "Card Security Code Failed";
				break;
			case 'V':
				result = "Address Verification and Card Security Code Failed";
				break;
			case '?':
				result = "Transaction status is unknown";
				break;
			default:
				result = "Unable to be determined";
			}

			return result;
		} else {
			return "No Value Returned";
		}
	} // getResponseDescription()

	public static TransactionStatus getAxisRefundStatus(String respCode,
			boolean isRefund) {
		TransactionStatus txnStatus = TransactionStatus.REFUND_INITIATED;
		if (respCode.length() > 0) {
			char input = respCode.charAt(0);
			switch (input) {
			case '0':
				txnStatus = isRefund ? TransactionStatus.REFUND_SUCCESS
						: TransactionStatus.CAPTURE_SUCCESS;
				break;
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
			case 'A':
			case 'C':
			case 'E':
			case 'F':
			case 'I':
			case 'L':
			case 'N':
			case 'R':
			case 'S':
			case 'T':
			case 'U':
			case 'V':
			case '?':
				txnStatus = isRefund ? TransactionStatus.REFUND_FAILED
						: TransactionStatus.CAPTURE_FAILED;
				break;
			case 'P':
			case 'D':
				txnStatus = isRefund ? TransactionStatus.REFUND_FORWARDED
						: TransactionStatus.CAPTURE_FORWARDED;
				break;
			default:
			}
		}
		return txnStatus;
	}

	/**
	 * This method is for creating a URL POST data string.
	 * 
	 * @param queryString
	 *            is the input String from POST data response
	 * @return is a Hashmap of Post data response inputs
	 */
	@SuppressWarnings({ "deprecation" })
	public static Map<String, String> createMapFromResponse(String queryString) {
		Map<String, String> map = new HashMap<String, String>();
		StringTokenizer st = new StringTokenizer(queryString, "&");
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			int i = token.indexOf('=');
			if (i > 0) {
				try {
					String key = token.substring(0, i);
					String value = URLDecoder.decode(token.substring(i + 1,
							token.length()));
					map.put(key, value);
				} catch (Exception ex) {
				}
			}
		}
		return map;
	}

	/**
	 * This method is for creating a URL POST data string.
	 * 
	 * @param fields
	 *            is the input parameters from the order page
	 * @return is the output String containing POST data key value pairs
	 */
	@SuppressWarnings({ "rawtypes", "deprecation" })
	public static String createPostDataFromMap(Map<String, String> fields) {
		StringBuffer buf = new StringBuffer();

		String ampersand = "";
		for (Iterator i = fields.keySet().iterator(); i.hasNext();) {
			String key = (String) i.next();
			String value = (String) fields.get(key);
			if (CommonUtil.isNotNull(value) && (value.length() > 0)) {
				// append the parameters
				buf.append(ampersand);
				buf.append(URLEncoder.encode(key));
				buf.append('=');
				buf.append(URLEncoder.encode(value));
			}
			ampersand = "&";
		}
		return buf.toString();
	}

	@SuppressWarnings("rawtypes")
	public static String requestParamListForLogging(
			Map<String, String> requestFields, ArrayList<String> fields) {
		String result = "";
		if (CommonUtil.isNotNull(requestFields)) {
			Iterator iterator = requestFields.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry mapEntry = (Map.Entry) iterator.next();
				if (!fields.contains(mapEntry.getKey())) {
					result += mapEntry.getKey() + " = " + mapEntry.getValue()
							+ ", ";
				}
			}
		}
		return result;
	}
}
