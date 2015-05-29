package com.citruspay.enquiry.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PayZenPGDataEncryptor{
	private static final Logger	logger = LoggerFactory.getLogger(PayZenPGDataEncryptor.class);

	public String encryptString(String data){
		StringBuilder stringBuilder = null;
		String wsSignature = null;

		try{
			stringBuilder = new StringBuilder(data);

			byte[] ourMpiRespSingatureDigest = null;
			java.security.MessageDigest messageDigest;

			messageDigest = java.security.MessageDigest.getInstance("SHA-1");
			ourMpiRespSingatureDigest = messageDigest.digest(stringBuilder
					.toString().getBytes("UTF-8"));
			wsSignature = toString(ourMpiRespSingatureDigest);
		} catch (Exception ex){
			logger.error("PAYZEN PG | Exception while generating " +
					"signature : ",ex);
		}
		return wsSignature;
	}

	public static String toString(byte buffer[]){
		// Local Variables
		StringBuilder builder;
		if (buffer == null){
			return null;
		}
		builder = new StringBuilder(2 * buffer.length);
		for (final byte b : buffer){
			// Converting byte to Hexadecimal
			builder.append(String.format("%02x", b));
		}
		return builder.toString();
	}
}
