package com.citruspay.enquiry.gateway;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.citruspay.CommonUtil;
import com.citruspay.enquiry.persistence.entity.TransactionStatus;

public class MIGSUtil {
	
	/**
	 * Logger to log MIGSUtil method calls.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(MIGSUtil.class);

	private MIGSUtil(){
		
	}
	/**
	    * This function uses the returned status code retrieved from the Digital
	    * Response and returns an appropriate description for the code
	    *
	    * @param vResponseCode String containing the vpc_TxnResponseCode
	    * @return description String containing the appropriate description
	    */ 
		public static String getResponseDescription(String vResponseCode) {

			LOGGER.info("Entry into getResponseDescription method of MIGSUtil");
			
		        String result = "No Value Returned";

		        // check if a single digit response code
		        if (vResponseCode.length() == 1) {
		        
		            // Java cannot switch on a string so turn everything to a char
		            char input = vResponseCode.charAt(0);

		            switch (input){
		                case '0' : result = "Transaction Successful"; break;
		                case '1' : result = "Unknown Error"; break;
		                case '2' : result = "Bank Declined Transaction"; break;
		                case '3' : result = "No Reply from Bank"; break;
		                case '4' : result = "Expired Card"; break;
		                case '5' : result = "Insufficient Funds"; break;
		                case '6' : result = "Error Communicating with Bank"; break;
		                case '7' : result = "Payment Server System Error"; break;
		                case '8' : result = "Transaction Type Not Supported"; break;
		                case '9' : result = "Bank declined transaction (Do not contact Bank)"; break;
		                case 'A' : result = "Transaction Aborted"; break;
		                case 'C' : result = "Transaction Cancelled"; break;
		                case 'D' : result = "Deferred transaction has been received and is awaiting processing"; break;
		                case 'E' : result = "Transaction declined - Refer to card issuer"; break;
		                case 'F' : result = "3D Secure Authentication failed"; break;
		                case 'I' : result = "Card Security Code verification failed"; break;
		                case 'L' : result = "Shopping Transaction Locked (Please try the transaction again later)"; break;
		                case 'N' : result = "Cardholder is not enrolled in Authentication Scheme"; break;
		                case 'P' : result = "Transaction has been received by the Payment Adaptor and is being processed"; break;
		                case 'R' : result = "Transaction was not processed - Reached limit of retry attempts allowed"; break;
		                case 'S' : result = "Duplicate SessionID (OrderInfo)"; break;
		                case 'T' : result = "Address Verification Failed"; break;
		                case 'U' : result = "Card Security Code Failed"; break;
		                case 'V' : result = "Address Verification and Card Security Code Failed"; break;
		                case '?' : result = "Transaction status is unknown"; break;
		                default  : result = "Unable to be determined";
		            }
		            
		        }
		        
		        LOGGER.info("MIGS PG : getResponseDescription method END of MIGSUtil");
		        
		        return result;
		        
		    } // getResponseDescription()	
		    
		/**
		 * Returns transaction status retrieved from the Digital Response.
		 * @param in respCode containing response code.
		 * @param in isRefund containing boolean value whether response is Refund or not.
		 * @return TransactionStatus object.
		 */
			public static TransactionStatus getMigsRefundStatus(String respCode, boolean isRefund) {
				
				LOGGER.info("Entry into getMigsRefundStatus method of MIGSUtil");
				
				TransactionStatus txnStatus = TransactionStatus.REFUND_INITIATED;
				if (respCode.length() > 0) {
					char input = respCode.charAt(0);
					switch (input) {
					case '0':
						txnStatus = isRefund ? TransactionStatus.REFUND_SUCCESS : TransactionStatus.CAPTURE_SUCCESS;
						break;
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
					case '8':
					case '9':
					case 'A':
					case 'C':
					case 'E':	
					case 'F':
					case 'I':
					case 'L':
					case 'N':
					case 'R':
					case 'S':
					case 'T':
					case 'U':
					case 'V':
					case '?':
						txnStatus = isRefund ? TransactionStatus.REFUND_FAILED : TransactionStatus.CAPTURE_FAILED;
						break;
					case 'P':
					case 'D':
						txnStatus = isRefund ? TransactionStatus.REFUND_FORWARDED : TransactionStatus.CAPTURE_FORWARDED;
						break;
					default:
					}
				}
				
				LOGGER.info("MIGS PG : getMigsRefundStatus method END of MIGSUtil");
				
				return txnStatus;
			}
			
		/**
		 * This method is for creating a URL POST data string.
		 * 
		 * @param queryString
		 *            is the input String from POST data response
		 * @return is a Hashmap of Post data response inputs
		 */
		@SuppressWarnings({ "deprecation" })
		public static Map<String, String> createMapFromResponse(String queryString) {
			
			LOGGER.info("Entry into createMapFromResponse method of MIGSUtil");
			
			Map<String, String> map = new HashMap<String, String>();
			StringTokenizer st = new StringTokenizer(queryString, "&");
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				int indexOfEqualSign = token.indexOf('=');
				if (indexOfEqualSign > 0) {
					try {
						String key = token.substring(0, indexOfEqualSign);
						String value = URLDecoder.decode(token.substring(indexOfEqualSign + 1,token.length()));
						map.put(key, value);
					} catch (Exception ex) {
						// Do Nothing and keep looping through data
						LOGGER.info(ex.toString());
					}
				}
			}
			LOGGER.info("MIGS PG : createMapFromResponse method END of MIGSUtil");
			
			return map;
		} 	
		      
		/**
		 * This method is for creating a URL POST data string.
		 * 
		 * @param fields
		 *            is the input parameters from the order page
		 * @return is the output String containing POST data key value pairs
		 */
		@SuppressWarnings({ "rawtypes", "deprecation" })
		public static String createPostDataFromMap(Map<String, String> fields) {
			
			LOGGER.info("Entry into createPostDataFromMap method of MIGSUtil");
			
			StringBuilder builder = new StringBuilder();

			// append all fields in a data string
		     for (Iterator mapIterator = fields.keySet().iterator(); mapIterator.hasNext();) {

				String key = (String) mapIterator.next();
				String value = fields.get(key);

				if (CommonUtil.isNotNull(value) && (value.length() > 0)) {
					// append the parameters				
					builder.append(URLEncoder.encode(key));
					builder.append('=');
					builder.append(URLEncoder.encode(value));
					if(mapIterator.hasNext()){
						builder.append('&');
					}
				}
				
			}

		     LOGGER.info("MIGS PG : createPostDataFromMap method END of MIGSUtil");
		     
			// return string
			return builder.toString();
		}	 
		       
		public static String doPost(String vpcHost, String data) throws IOException {
			
			LOGGER.info("Entry into doPost method of MIGSUtil");

			InputStream is;
			OutputStream os;
			int vpc_Port = 443;
			String fileName = "";
			boolean useSSL = false;

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

			if (useSSL) {
				Socket s = getSSLSocketFactoryObject().createSocket(vpcHost, vpc_Port);
				os = s.getOutputStream();
				is = s.getInputStream();
				// use next block of code if NOT using SSL encryption
			} else {
				Socket s = new Socket(vpcHost, vpc_Port);
				os = s.getOutputStream();
				is = s.getInputStream();
			}

			String req = "POST " + fileName + " HTTP/1.0\r\n"
					+ "User-Agent: HTTP Client\r\n"
					+ "Content-Type: application/x-www-form-urlencoded\r\n"
					+ "Content-Length: " + data.length() + "\r\n\r\n" + data;

			os.write(req.getBytes());
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
			
			LOGGER.info("MIGS PG : doPost method END of MIGSUtil");
			
			return body;
		}

		@SuppressWarnings("restriction")
		private static SSLSocketFactory getSSLSocketFactoryObject() {
			
			LOGGER.info("Entry into getSSLSocketFactoryObject method of MIGSUtil");
			
			X509TrustManager s_x509TrustManager = null;
			SSLSocketFactory s_sslSocketFactory = null;

			s_x509TrustManager = new X509TrustManager() {
				@Override
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
			
			LOGGER.info("MIGS PG : getSSLSocketFactoryObject method END of MIGSUtil");
			
			return s_sslSocketFactory;
		}

		/**
		 * This method is for creating a byte array from input stream data.
		 * 
		 * @param is
		 *            - the input stream containing the data
		 * @return is the byte array of the input stream data
		 */
		private static byte[] readAll(InputStream is) throws IOException {
			
			LOGGER.info("Entry into readAll method of MIGSUtil");

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];

			while (true) {
				int len = is.read(buf);
				if (len < 0) {
					break;
				}
				baos.write(buf, 0, len);
			}
			
			LOGGER.info("MIGS PG : readAll method END of MIGSUtil");
			
			return baos.toByteArray();
		}
		
		@SuppressWarnings("rawtypes")
		public static String requestParamListForLogging(Map<String, String> requestFields, List<String> fields){
			
			LOGGER.info("Entry into requestParamListForLogging method of MIGSUtil");
			
			StringBuilder stringBuilder = new StringBuilder("");
			
			if(CommonUtil.isNotNull(requestFields)){
				Iterator iterator = requestFields.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry mapEntry = (Map.Entry) iterator.next();
					if(!fields.contains(mapEntry.getKey())){
						stringBuilder.append(mapEntry.getKey()+" = "+mapEntry.getValue()+", ");
					}
				}			
			}
			
			LOGGER.info("MIGS PG : requestParamListForLogging method END of MIGSUtil");
			
			return stringBuilder.toString();
		}		

}
