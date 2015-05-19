/*
 * Copyright (c) 2012 CitrusPay. All Rights Reserved.
 *
 * This software is the proprietary information of CitrusPay.
 * Use is subject to license terms.
 */
package com.citruspay;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.citruspay.JDateUtil;
import com.citruspay.enquiry.persistence.entity.PaymentMode;

public class CommonUtil {

	public static final int DECIMAL_PLACES = 2;
	public static final int EIGHT_DECIMAL_PLACES = 8;
	public static final String CARDS_SUPPORTED_BY_MERCHANT = "supported";
	public static final String CARDS_NOT_SUPPORTED_BY_MERCHANT = "unsupported";
	public final static String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

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

		System.out.println(" txnCreatedDateDateTime="+txnCreatedDateDateTime + " settlementnDateTime="+settlementnDateTime);
		// If execution time is more than scheduled time
		if (txnCreatedDateDateTime.compareTo(settlementnDateTime) > 0) {
			System.out.println(" line 87 txnCreatedDateDateTime="+txnCreatedDateDateTime + " settlementnDateTime="+settlementnDateTime);

			return Boolean.FALSE;
		}

		// Is previous day transaction

		Interval lastDay = JDateUtil.getISTPreviousDay();
		System.out.println( " currentDateTime="+currentDateTime+"lastday="+lastDay+ " lastDay.getStart()="+lastDay.getStart()+" lastDay.getEnd()="+lastDay.getEnd());
		if ((currentDateTime.compareTo(settlementnDateTime) <= 0
				&& txnCreatedDateDateTime.compareTo(lastDay.getStart()) >= 0 && txnCreatedDateDateTime
				.compareTo(lastDay.getEnd()) <= 0)
				|| (txnCreatedDateDateTime.compareTo(lastDay.getEnd()) >= 0 && txnCreatedDateDateTime
						.compareTo(settlementnDateTime) <= 0)) {
			System.out.println(" lline 98");
			return Boolean.FALSE;
		}
		System.out.println(" line 103 currentDateTime="+currentDateTime+"txnCreatedDateDateTime="+txnCreatedDateDateTime + " settlementnDateTime="+settlementnDateTime+ " currentDateTime.compareTo(settlementnDateTime)="+currentDateTime.compareTo(settlementnDateTime));

		return Boolean.TRUE;

	}

	public static String getDateStringInIST(Date date) {

		Calendar calendar = Calendar.getInstance();
		TimeZone timeZone = TimeZone.getTimeZone("GMT+05:30");
		calendar.setTime(date);
		SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);
		sdf.setTimeZone(timeZone);
		return sdf.format(calendar.getTime());
	}



}
