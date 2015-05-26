package com.citruspay.enquiry.encryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.citruspay.enquiry.configuration.AppConfigManager;
import com.citruspay.enquiry.persistence.entity.SysECData;
import com.citruspay.HMacUtil;

public class AESEncryptionDecryption {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AESEncryptionDecryption.class);

	private static final String AES_ALORITHM = "AES";

	private SecretKeySpec keySpec;

	private SecretKeySpec encDecKeySpec;

	private SecretKeySpec getKeySpec() throws GeneralSecurityException {
		
		
		if (encDecKeySpec == null) {
			if (keySpec == null) {
				keySpec = getDataEncKeySpec();
			}
			SysECData data = SysECConfig.INSTANCE.getSysECData();
			String encKey = decryptDataEncKey(data.getData(), keySpec);
			byte[] bytes = new byte[16];
			bytes = HMacUtil.convertHexToBytes(encKey);

			encDecKeySpec = new SecretKeySpec(bytes, AES_ALORITHM);
		}
		return encDecKeySpec;
	}

	private SecretKeySpec getDataEncKeySpec() throws GeneralSecurityException {
		if (keySpec == null) {
			try {
				byte[] bytes = new byte[16];
				File f = new File(AppConfigManager.INSTANCE.getAppConfig()
						.getProperties().getProperty("key.path").trim());
				if (f.exists()) {
					new FileInputStream(f).read(bytes);
				}
				keySpec = new SecretKeySpec(bytes, AES_ALORITHM);
			} catch (IOException ex) {
				throw new GeneralSecurityException(ex.getMessage());
			}
		}
		return keySpec;
	}

	public String encryptDataEncKey(String text)
			throws GeneralSecurityException {
		Cipher cipher = Cipher.getInstance(AES_ALORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, getDataEncKeySpec());
		byte[] encodedText = cipher.doFinal(text.getBytes());
		return new String(Hex.encodeHexString(encodedText));
	}

	public String decryptDataEncKey(String encodedText, SecretKeySpec keySpec)
			throws GeneralSecurityException {
		Cipher cipher = Cipher.getInstance(AES_ALORITHM);
		cipher.init(Cipher.DECRYPT_MODE, keySpec);
		byte[] decodedText = cipher.doFinal(HMacUtil
				.convertHexToBytes(encodedText));
		return new String(decodedText);
	}

	public String encrypt(String text) throws GeneralSecurityException {
		
		LOGGER.info("Encrypting data");
		
		Cipher cipher = Cipher.getInstance(AES_ALORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, getKeySpec());
		byte[] encodedText = cipher.doFinal(text.getBytes());
		return new String(Hex.encodeHexString(encodedText));
	}

	public String decrypt(String encodedText) throws GeneralSecurityException {
		
		LOGGER.info("Decrypting data");
		
		Cipher cipher = Cipher.getInstance(AES_ALORITHM);
		cipher.init(Cipher.DECRYPT_MODE, getKeySpec());
		byte[] decodedText = cipher.doFinal(HMacUtil
				.convertHexToBytes(encodedText));
		return new String(decodedText);
	}
}
