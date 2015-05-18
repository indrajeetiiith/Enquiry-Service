

package com.citruspay.enquiry;




import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.json.JSONObject;

public class EnquiryRestClient {

	//private static String URI_TEMPLATE = "http://localhost:8080/EnquiryService/service/enquiryService";
	private static String URI_TEMPLATE = "http://localhost:8080/EnquiryService";
	/*
	 * Use mvn tomcat:run to start the server
	 * 
	 */
	public static void main(String[] args) {

//		ClientConfig config = new ClientConfig();

//		Client client = ClientBuilder.newClient(config);

//		WebTarget target = client.target(getBaseURI());
//
//		System.out.println("Output 1: " + target.path("rest").path("hello").path("tests").request().accept(MediaType.TEXT_PLAIN).get(Response.class).toString());
//		System.out.println("Output 7: " + target.path("rest").path("todo").request().accept(MediaType.APPLICATION_JSON).get(String.class));

		callFetchJson(URI_TEMPLATE);
	}

	private static URI getBaseURI() {
		return UriBuilder.fromUri(URI_TEMPLATE).build();
	}
	
	private static void callFetchJson(String urlCall) {
		try {
			 
			//URL url = new URL(urlCall+"/rest/todo");
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
            InputStream crunchifyInputStream = new FileInputStream(
                    "D://JSONFile.txt");
            InputStreamReader crunchifyReader = new InputStreamReader(crunchifyInputStream);
            BufferedReader bufferReader = new BufferedReader(crunchifyReader);
            String line;
            String string="";
            while ((line = bufferReader.readLine()) != null) {
                string += line + "\n";
            }
 
            JSONObject jsonObject = new JSONObject(string);
            System.out.println(jsonObject);
			out.write(jsonObject.toString());
            out.close();
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
	 
			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));
	 
			String output;
			System.out.print("Output 8: ");
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