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

import com.citruspay.enquiry.persistence.entity.PaymentMode;

public class CommonUtil {

	public static final int DECIMAL_PLACES = 2;
	public static final int EIGHT_DECIMAL_PLACES = 8;
	public static final String CARDS_SUPPORTED_BY_MERCHANT = "supported";
	public static final String CARDS_NOT_SUPPORTED_BY_MERCHANT = "unsupported";
	public final static String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final int ONE = 1;


	/**
	 * This function checks if the string is null or empty. returns true if string is not null but empty.
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if (str == null || str.length() <= 0) {
			return true;
		}
		return false;
	}
	/**
	 * This function checks if object is not null
	 * @param object
	 * @return
	 */
	public static boolean isNotNull(Object object) {
		return (object != null) ? Boolean.TRUE : Boolean.FALSE;
	}

	/**
	 * This function checks if object is null
	 * @param object
	 * @return
	 */
	
	public static boolean isNull(Object object) {
		return (object == null) ? Boolean.TRUE : Boolean.FALSE;
	}

	/**
	 * This function checks if the list of the objects is empty or not
	 * @param objList
	 * @return
	 */
	public static <T> boolean isNotEmpty(List<T> objList) {
		return (isNotNull(objList) && (objList.size() > 0));
	}

	/**
	 * This function checks if the list of the objects is empty
	 * @param objList
	 * @return
	 */
	public static <T> boolean isEmpty(List<T> objList) {
		return (isNull(objList) || (objList.size() == 0));
	}

	/**
	 * This function checks if the payment mode ir credit or debit or emi card
	 * @param paymentMode
	 * @return
	 */
	public static boolean isCreditOrDebitCard(PaymentMode paymentMode) {
		if (PaymentMode.CREDIT_CARD.equals(paymentMode)
				|| PaymentMode.DEBIT_CARD.equals(paymentMode) 
				|| PaymentMode.EMI.equals(paymentMode)) {
			return true;
		}
		return false;
	}
	
	/**
	 * This function checks if the txncreated date falls between dailysettlementhr and dailysettlementmin .these two values are taken from properties file.
	 * @param txnCreatedDate
	 * @param dailySettlementHr
	 * @param dailySettlementMin
	 * @return
	 */
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

	/**
	 * This function returns the data string in IST format
	 * @param date
	 * @return
	 */
	public static String getDateStringInIST(Date date) {

		Calendar calendar = Calendar.getInstance();
		TimeZone timeZone = TimeZone.getTimeZone("GMT+05:30");
		calendar.setTime(date);
		SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);
		sdf.setTimeZone(timeZone);
		return sdf.format(calendar.getTime());
	}



}
