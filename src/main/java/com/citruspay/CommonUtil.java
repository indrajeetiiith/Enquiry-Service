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
import java.util.Map;
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
	public static final String JPQL_LIKE = "%";
	public static final long HUNDRED = 100;
	public static final String ZERO_STRING = "0";
	public static final int INT_FOUR = 4;
	public static final int INT_ZERO = 0;
	public static final int CONST_ONE = 1;




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
	public static boolean validateDateTime(Date txnCreatedDate) {

		
		Date currentDate = new Date();
		DateTime currentDateTime = JDateUtil.getDateAndTime(currentDate);
		DateTime txnCreatedDateDateTime = JDateUtil
				.getDateAndTime(txnCreatedDate);

		// If transaction created time is more than a day old then take the record from DB.
		Interval lastDay = JDateUtil.getISTPreviousDay();

		if (txnCreatedDateDateTime.compareTo(lastDay.getStart()) < 0) {
			return true;
		}

		// if it's same day transaction then return false and call corresponding PG's enquiry call else take the data from our DB
		if(JDateUtil.noOfDays(txnCreatedDateDateTime, currentDateTime) > 1)
		{
			return true;
		}
		else
			return false;

		// Is previous day transaction

/*NO NEED to solve		if ((txnCreatedDateDateTime.compareTo(lastDay.getStart()) >= 0 && txnCreatedDateDateTime.compareTo(lastDay.getEnd()) <= 0)) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
*/
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

	public static String prepareXMLRequest(Map<String, String> parameterMap) {

		StringBuffer sb = new StringBuffer();
		Set<String> keys = parameterMap.keySet();
		for (String key : keys) {
			sb.append("<" + key + ">");
			sb.append(parameterMap.get(key));
			sb.append("</" + key + ">");
		}
		return sb.toString();
	}

	public static Integer getInteger(String str, int defaultValue) {
		try {
			int intVal = Integer.parseInt(str);
			return intVal;
		} catch (Exception ex) {
			return defaultValue;
		}
	}

	

}
