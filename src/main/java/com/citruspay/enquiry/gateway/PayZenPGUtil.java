package com.citruspay.enquiry.gateway;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.citruspay.enquiry.configuration.AppConfigManager;
import com.lyra.vads.ws.v4.Standard;
import com.lyra.vads.ws.v4.StandardWS;

public class PayZenPGUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PayZenPGUtil.class);
	
	private String payzenPgProdWsdlLocation=AppConfigManager.INSTANCE.getAppConfig().getPropertiesWithPrefix("payzen.pg.prod").getProperty("wsdlLocation");
	
	private String namespaceUri = AppConfigManager.INSTANCE.getAppConfig().getPropertiesWithPrefix("payzen.pg.prod.namespace.uri").getProperty("uri");;
	
	public static Standard payzenStandard = null;
	
	/**
	 * Loading Payzen WSDL on class load, so that 
	 * can be accessible as and when required.
	 * This is to improve performance of transaction
	 * processing.
	 */
	public Standard prepareStandard() {
		if (payzenStandard != null) {
			return payzenStandard;
		} else {
			try {
				URL url = new URL(payzenPgProdWsdlLocation);
				QName qname = new QName(namespaceUri, "StandardWS");
				payzenStandard = new StandardWS(url, qname).getStandardBeanPort();
			} catch (MalformedURLException e) {
				LOGGER.error("Failed to create URL for the wsdl Location: "+payzenPgProdWsdlLocation, e);
			}
		}
		return payzenStandard;
	}
	
	/**
	 * Will return Port
	 * @return
	 */
	/*public static Standard getPazenClient() {
		return payzenStandard;
	}*/
	
	/**
	 * For getting date in XMLGregorianCalendar format
	 * 
	 * @param createDate
	 * @param timeZone
	 * @return
	 */
	public static XMLGregorianCalendar getGregorianDate(Date createDate,
			String timeZone) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(createDate);
		gc.setTimeZone(TimeZone.getTimeZone(timeZone));
		
		XMLGregorianCalendar xmlGregorianCalendar;
		try {
			xmlGregorianCalendar = DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(gc);
		} catch(DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
		return xmlGregorianCalendar;
	}
	
	/**
	 * for getting currency code
	 * @param currency
	 * @return
	 */
	public static String getCurrencyCode(String currency) {
		String currencyCode = null;
		currencyCode = GatewayService.CURRENCY_CODE.get(currency);
		if (currencyCode == null) {
			currencyCode = GatewayService.CURRENCY_CODE_INR;// default INR
		}
		return currencyCode;
	}
	
}