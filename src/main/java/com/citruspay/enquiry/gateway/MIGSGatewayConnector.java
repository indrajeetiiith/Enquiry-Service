package com.citruspay.enquiry.gateway;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MIGSGatewayConnector extends SocketCreater{

	private static final Logger LOGGER = LoggerFactory.getLogger(MIGSGatewayConnector.class);
	
	private final int vpc_Port = 443;

	// if method is changed then change RefundCaptureLoggingAspect
	// SupportRequest is added for loggers and it has null value when called
	// from enquiry method
	public String connect(String vpcHost, String queryParameters) {

		LOGGER.info("MIGS PG : Entering MIGSGatewayConnector.connect() method.");
		
		boolean useSSL = false;
		String fileName = "";
		String body = StringUtils.EMPTY;

		// determine if SSL encryption is being used
		if ("HTTPS://".equalsIgnoreCase(vpcHost.substring(0, 8))) {
			useSSL = true;
			// remove 'HTTPS://' from host URL
			vpcHost = vpcHost.substring(8);
			// get the filename from the last section of vpc_URL
			fileName = vpcHost.substring(vpcHost.lastIndexOf("/"));
			// get the IP address of the VPC machine
			vpcHost = vpcHost.substring(0, vpcHost.lastIndexOf("/"));

		}


		try {
			System.out.println("line 44 queryParameters="+queryParameters);

			Socket socket = null;
			if (useSSL) {
				System.out.println("line 48 queryParameters="+queryParameters);

				socket = getSocket(vpcHost, vpc_Port);
				System.out.println("line 51 queryParameters="+queryParameters);

				// use next block of code if NOT using SSL encryption
			} else {
				System.out.println("line 55 queryParameters="+queryParameters);

				socket = new Socket(vpcHost, vpc_Port);
			}
			System.out.println("line 59 queryParameters="+queryParameters);

			final OutputStream os = socket.getOutputStream();
			final InputStream inputStream = socket.getInputStream();

			os.write(prepareCompleteQueryString(fileName, queryParameters).getBytes());
			final String res = new String(readAll(inputStream));
			System.out.println("line 66 queryParameters="+queryParameters);
			// check if a successful connection
			if (res.indexOf("200") < 0) {
				throw new IOException("Connection Refused - " + res);
			}

			if (res.indexOf("404 Not Found") > 0) {
				throw new IOException("File Not Found Error - " + res);
			}

			final int resIndex = res.indexOf("\r\n\r\n");
			body = res.substring(resIndex + 4, res.length());
			

		} catch (IOException ioe) {
			LOGGER.info(ioe.toString());
		}
		return body;
	}
	
}
