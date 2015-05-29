/*
 * Copyright (c) 2014 CitrusPay. All Rights Reserved.
 *
 * This software is the proprietary information of CitrusPay.
 * Use is subject to license terms.
 */

package com.citruspay.enquiry.gateway;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

public class SocketCreater {

	protected Socket getSocket(String vpcHost, int vpc_Port) throws IOException {
		return getSSLSocketFactoryObject().createSocket(vpcHost, vpc_Port);
	}

	@SuppressWarnings("restriction")
	private static SSLSocketFactory getSSLSocketFactoryObject() {
		X509TrustManager s_x509TrustManager = null;
		SSLSocketFactory s_sslSocketFactory = null;

		s_x509TrustManager = new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[] {};
			}

			@Override
			public void checkClientTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
				// TODO Auto-generated method stub
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
				// TODO Auto-generated method stub
			}
		};

		java.security.Security
				.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		try {
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, new X509TrustManager[] { s_x509TrustManager },
					null);
			s_sslSocketFactory = context.getSocketFactory();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		return s_sslSocketFactory;
	}

	protected String prepareCompleteQueryString(String fileName,
			String queryParameters) {
		return new StringBuilder("POST ").append(fileName)
				.append(" HTTP/1.0\r\n").append("User-Agent: HTTP Client\r\n")
				.append("Content-Type: application/x-www-form-urlencoded\r\n")
				.append("Content-Length: ").append(queryParameters.length())
				.append("\r\n\r\n").append(queryParameters).toString();
	}

	protected byte[] readAll(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		while (true) {
			int len = is.read(buf);
			if (len < 0) {
				break;
			}
			baos.write(buf, 0, len);
		}
		return baos.toByteArray();
	}
}
