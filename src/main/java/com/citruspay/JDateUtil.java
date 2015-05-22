/*
O* * Copyright (c) 2012 CitrusPay. All Rights Reserved.
 *
 * This software is the proprietary information of CitrusPay.
 * Use is subject to license terms.
 */
package com.citruspay;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDateUtil {

	private static final Logger log = LoggerFactory.getLogger(JDateUtil.class);
	
	public final static String Asia_Kolkata  = "Asia/Kolkata";
	public final static String UTC  = "UTC";


	/***
	 * Returns the start of date of a given date.
	 * 
	 * @param date
	 * @return
	 */
	public static DateTime startOfDay(DateTime date) {
		return date.withMillisOfSecond(0).withSecondOfMinute(0)
				.withMinuteOfHour(0).withHourOfDay(0);
	}
	public static DateTime getDateAndTime(Date date) {
		DateTimeZone ISTTimeZone = DateTimeZone.forID(UTC);
		DateTime dateTime = new DateTime(date.getTime(), ISTTimeZone);
		return dateTime;
	}
	public static int noOfDays(DateTime start, DateTime end) {
		return (int) ((startOfDay(end).getMillis() - startOfDay(start)
				.getMillis()) / (1000 * 60 * 60 * 24));
	}

	
	@SuppressWarnings("deprecation")
	public static DateTime getDateAndTime(String dailySettlementHr,
			String dailySettlementMin) {

		Date date = new Date();
		date.setHours((dailySettlementHr.length() < 2) ? Integer.valueOf("0"
				+ dailySettlementHr) : Integer.valueOf(dailySettlementHr));
		date.setMinutes((dailySettlementMin.length() < 2) ? Integer.valueOf("0"
				+ dailySettlementMin) : Integer.valueOf(dailySettlementMin));
		DateTimeZone ISTTimeZone = DateTimeZone.forID(UTC);
		System.out.println( " isttimezone="+ISTTimeZone);
		DateTime dateTime = new DateTime(date.getTime(), ISTTimeZone);
		System.out.println( " dateTime="+dateTime + "date="+date + "date.getTime()="+date.getTime());
		
		return dateTime;
	}

	public static Interval getISTPreviousDay() {
		DateTimeZone ISTTimeZone = DateTimeZone.forID(Asia_Kolkata);
		DateTime dateTime = new DateTime(ISTTimeZone);
		DateTime from = JDateUtil.startOfDay(dateTime.minusDays(1));
		DateTime to = JDateUtil.startOfDay(dateTime);
		return new Interval(from, to);
	}


}
