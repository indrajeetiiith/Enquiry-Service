/*
 * Copyright (c) 2012 CitrusPay. All Rights Reserved.
 *
 * This software is the proprietary information of CitrusPay.
 * Use is subject to license terms.
 */
package com.citruspay;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.citruspay.JDateUtil;
import com.citruspay.enquiry.persistence.entity.PaymentMode;

public class CommonUtil {

	public static final int DECIMAL_PLACES = 2;
	public static final int EIGHT_DECIMAL_PLACES = 8;

	public static boolean isEmpty(String str) {
		if (str == null || str.length() <= 0) {
			return true;
		}
		return false;
	}
	public static boolean isNotNull(Object object) {
		return (object != null) ? Boolean.TRUE : Boolean.FALSE;
	}

	public static boolean isNull(Object object) {
		return (object == null) ? Boolean.TRUE : Boolean.FALSE;
	}

	public static boolean validateEmptyParams(String... args) {
		for (String s : args) {
			if (s == null || (s != null && s.equalsIgnoreCase(""))) {
				return false;
			}
		}
		return true;
	}


	public static <T> boolean isNotEmpty(List<T> objList) {
		return (isNotNull(objList) && (objList.size() > 0));
	}

	public static <T> boolean isNotEmpty(Set<T> objList) {
		return (isNotNull(objList) && (objList.size() > 0));
	}

	public static <T> boolean isEmpty(List<T> objList) {
		return (isNull(objList) || (objList.size() == 0));
	}

	public static String handleNull(String str) {
		if (str == null) {
			str = "";
		}
		return str;
	}
	public static boolean isCreditOrDebitCard(PaymentMode paymentMode) {
		if (PaymentMode.CREDIT_CARD.equals(paymentMode)
				|| PaymentMode.DEBIT_CARD.equals(paymentMode) 
				|| PaymentMode.EMI.equals(paymentMode)) {
			return true;
		}
		return false;
	}
	public static boolean validateDateTime(Date txnCreatedDate,
			String dailySettlementHr, String dailySettlementMin) {

		Date currentDate = new Date();
		DateTime currentDateTime = JDateUtil.getDateAndTime(currentDate);
		DateTime txnCreatedDateDateTime = JDateUtil
				.getDateAndTime(txnCreatedDate);
		DateTime settlementnDateTime = JDateUtil.getDateAndTime(
				dailySettlementHr, dailySettlementMin);

		// If execution time is more than scheduled time
		if (txnCreatedDateDateTime.compareTo(settlementnDateTime) > 0) {
			return Boolean.FALSE;
		}

		// Is previous day transaction

		Interval lastDay = JDateUtil.getISTPreviousDay();
		if ((currentDateTime.compareTo(settlementnDateTime) <= 0
				&& txnCreatedDateDateTime.compareTo(lastDay.getStart()) >= 0 && txnCreatedDateDateTime
				.compareTo(lastDay.getEnd()) <= 0)
				|| (txnCreatedDateDateTime.compareTo(lastDay.getEnd()) >= 0 && txnCreatedDateDateTime
						.compareTo(settlementnDateTime) <= 0)) {
			return Boolean.FALSE;
		}

		return Boolean.TRUE;

	}


}
