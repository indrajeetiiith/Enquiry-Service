/*
 * Copyright (c) 2014 CitrusPay. All Rights Reserved.
 *
 * This software is the proprietary information of CitrusPay.
 * Use is subject to license terms.
 */

package com.citruspay.enquiry.gateway;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class HDFCStatusCode {

	public static Map<String, String> statusMap = new ImmutableMap.Builder<String, String>()
			.put("CAPTURED", "Transaction successful")
			.put("APPROVED", "Transaction approved")
			.put("NOT CAPTURED", "Transaction failed")
			.put("NOT APPROVED", "Transaction failed")
			.put("DENIED BY RISK", "Risk denied the transaction processing")
			.put("HOST TIMEOUT",
					"The authorization system did not respond within the Time out limit")
			.put("SUCCESS", "The transaction is successful")
			.put("FAILURE(NOT CAPTURED)", "The transaction is failed")
			.put("FAILURE(SUSPECT)",
					"The transaction data is not matching, and hence failed.")
			.build();

	public final static String ERROR_REGEX = "!ERROR!";
}
