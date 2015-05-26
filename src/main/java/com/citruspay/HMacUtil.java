package com.citruspay;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.citruspay.enquiry.exceptions.EncryptionException;

public class HMacUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HMacUtil.class);

	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	private static final String SHA1PRNG_ALGORITHM="SHA1PRNG";
	private static final String SHA1_ALGORITHM="SHA-1";

	public static String generateHMAC(String data, String hexEncodedKey) throws EncryptionException {
		
		LOGGER.info("Generating encryption key");
		
		String result = "";
		try {
			
			byte[] keyBytes = hexEncodedKey.getBytes();
			SecretKeySpec signingKey = new SecretKeySpec(keyBytes,
					HMAC_SHA1_ALGORITHM);
			Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			mac.init(signingKey);
			byte[] rawHmac = mac.doFinal(data.getBytes());
			
			byte[] hexBytes = new Hex().encode(rawHmac);
			result = new String(hexBytes, "UTF-8");
			
		} catch (Exception e) {
			LOGGER.error("Problem generating encryption key ", e);
			throw new EncryptionException(e.getMessage());
		}
		return result;
	}
	
	public static byte[] convertHexToBytes(String hex) {

		byte[] bArray = new BigInteger("10" + hex, 16).toByteArray();

		byte[] ret = new byte[bArray.length - 1];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = bArray[i + 1];
		}
		return ret;
	}

	public static String generateSecretKey() throws EncryptionException {
		
		LOGGER.info("Generating secret key");
		
		try {
			
			SecureRandom secureRandom = SecureRandom.getInstance(SHA1PRNG_ALGORITHM);
			String randomNumberString = new Integer(secureRandom.nextInt()).toString();
			
			MessageDigest messageDigest = MessageDigest.getInstance(SHA1_ALGORITHM);
			
			byte[] result = messageDigest.digest(randomNumberString.getBytes());
			return convertBytesToHex(result);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("Problem generating secret key ", e);
			throw new EncryptionException(e.getMessage());
			
		} catch (Exception e) {
			LOGGER.error("Problem generating secret key ", e);
			throw new EncryptionException(e.getMessage());
			
		}
	}

	public static String convertBytesToHex(byte[] bytes) {
		
		StringBuilder result = new StringBuilder();
		
		char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		for (int idx = 0; idx < bytes.length; ++idx) {
			byte b = bytes[idx];
			result.append(digits[(b & 0xf0) >> 4]);
			result.append(digits[b & 0x0f]);
		}
		return result.toString();
	}

}
