/*
 * Copyright (c) 2012 CitrusPay. All Rights Reserved.
 *
 * This software is the proprietary information of CitrusPay.
 * Use is subject to license terms.
 */
package com.citruspay;

import java.util.List;
import java.util.Set;

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

}
