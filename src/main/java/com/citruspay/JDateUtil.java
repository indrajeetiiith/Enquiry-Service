/*
O* * Copyright (c) 2012 CitrusPay. All Rights Reserved.
 *
 * This software is the proprietary information of CitrusPay.
 * Use is subject to license terms.
 */
package com.citruspay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDateUtil {

	private static final Logger log = LoggerFactory.getLogger(JDateUtil.class);
	
	public final static String Asia_Kolkata  = "Asia/Kolkata";
	public final static String UTC  = "UTC";
	public final static String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	@SuppressWarnings("serial")
	private final static List<SimpleDateFormat> dateFormats = new ArrayList<SimpleDateFormat>() {
		{
			add(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"));
			add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		}
	};



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

	public static String getPGDate(String dateString) {
		SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);
		String formatedDate = null;
		Date date = null;
		for (SimpleDateFormat simpleDateFormat : dateFormats) {
			try {
				date = simpleDateFormat.parse(dateString);
			} catch (ParseException ex) {
				log.error("Parsing exception,continue with another format");
			}
			if (CommonUtil.isNotNull(date)) {
				formatedDate = sdf.format(date);
				break;
			}
		}

		return formatedDate;

	}
	public static String getDateStringInIST(Date date) {

		Calendar calendar = Calendar.getInstance();
		TimeZone timeZone = TimeZone.getTimeZone("GMT+05:30");
		calendar.setTime(date);
		SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);
		sdf.setTimeZone(timeZone);
		return sdf.format(calendar.getTime());
	}
	public static String getDateString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);
		return sdf.format(date);
	}



}
