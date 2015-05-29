package com.citruspay.enquiry;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;

public class EnquiryRestClient {

	//private static String URI_TEMPLATE = "http://localhost:8080/EnquiryService/service/enquiryService";
	// merchantt txnid:"1413878203327", access key:"5VHM1C4CEUSLOEPO8PH2",
	private static String URI_TEMPLATE = "http://localhost:8080/enquiry-service";

	public static void main(String[] args) {

		callFetchJson(URI_TEMPLATE);
	}
	private static void callFetchJson(String urlCall) {
		try {
			
		/*	EnquiryRestService service = new EnquiryRestService();
			EnquiryRequest enquiryRequest = null;
			System.out.println(" response = "+service.enquiry(enquiryRequest));
		*/	
			URL url = new URL(urlCall+"/service/enquiryService");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
	 
			
			/**
			 * get the JSON file
			 */
	        // Step1: Let's 1st read file from fileSystem
            InputStream fileInputStream = new FileInputStream(
                    "D://JSONFile.txt");
            InputStreamReader inputReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferReader = new BufferedReader(inputReader);
            String line;
            String string="";
            while ((line = bufferReader.readLine()) != null) {
                string += line + "\n";
            }
            bufferReader.close();
            JSONObject jsonObject = new JSONObject(string);
            System.out.println(jsonObject);
			//out.write(jsonObject.toString());
			out.write(jsonObject.toString());
			
            out.close();
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
	 
			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));
	 
			String output;
			System.out.print("Enquiry Response: ");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}
	 
			conn.disconnect();
	 
		  } catch (MalformedURLException e) {
	 
			e.printStackTrace();
	 
		  } catch (IOException e) {
	 
			e.printStackTrace();
	 
		  }
	}

}