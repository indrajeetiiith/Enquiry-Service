package com.citruspay.enquiry.gateway;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BillDeskGatewayConnector extends SocketCreater{

	private static final Logger LOGGER = LoggerFactory
			.getLogger(BillDeskGatewayConnector.class);
	
	private static final int vpc_Port = 443;

	// if method is changed then change RefundCaptureLoggingAspect
	// SupportRequest is added for loggers and it has null value when called
	// from enquiry method
	public String connect(String vpcHost, String queryParameters) {

		boolean useSSL = false;
		String fileName = "";
		
		// determine if SSL encryption is being used
		if ("HTTPS://".equalsIgnoreCase(vpcHost.substring(0, 8))) {
			useSSL = true;
			// remove 'HTTPS://' from host URL
			vpcHost = vpcHost.substring(8);
			// get the filename from the last section of vpc_URL
			fileName = vpcHost.substring(vpcHost.indexOf("/"), vpcHost.length());
			// get the IP address of the VPC machine
			vpcHost = vpcHost.substring(0, vpcHost.indexOf("/"));
		}

		try {
			Socket socket = null;
			if (useSSL) {
				socket = getSocket(vpcHost, vpc_Port);
				// use next block of code if NOT using SSL encryption
			} else {
				socket = new Socket(vpcHost, vpc_Port);
			}
			
			OutputStream os = socket.getOutputStream();
			InputStream is = socket.getInputStream();

			os.write(prepareCompleteQueryString(fileName, queryParameters)
					.getBytes());
			String res = new String(readAll(is));

			// check if a successful connection
			if (res.indexOf("200") < 0) {
				throw new IOException("Connection Refused - " + res);
			}

			if (res.indexOf("404 Not Found") > 0) {
				throw new IOException("File Not Found Error - " + res);
			}

			int resIndex = res.indexOf("\r\n\r\n");
			String body = res.substring(resIndex + 4, res.length());
			
			return body;

		} catch (IOException ioe) {
			LOGGER.info(ioe.toString());
		}
		return StringUtils.EMPTY;
	}
}
