package com.citruspay.enquiry;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
 


import org.json.JSONObject;

import com.citruspay.enquiry.api.EnquiryRequest;
import com.citruspay.enquiry.api.EnquiryTransactionBase;
 
/**
 * @author Crunchify.com
 * 
 */
 
public class EnquiryRestClient {
    public static void main(String[] args) {
        String string = "";
        try {
 
            // Step1: Let's 1st read file from fileSystem
            InputStream crunchifyInputStream = new FileInputStream(
                    "D://JSONFile.txt");
            InputStreamReader crunchifyReader = new InputStreamReader(crunchifyInputStream);
            BufferedReader br = new BufferedReader(crunchifyReader);
            String line;
            while ((line = br.readLine()) != null) {
                string += line + "\n";
            }
            EnquiryRequest enquiryRequest = new EnquiryRequest();
            enquiryRequest.setMerchantAccessKey("27AOYSJCQOR6VZ39V7JV");
            enquiryRequest.setMerchantTxnId("TXN55100");
            System.out.println("calling enquiry");
            
            new EnquiryTransactionBase().enquiry(enquiryRequest);
            JSONObject jsonObject = new JSONObject(string);
            System.out.println(jsonObject);
 
            // Step2: Now pass JSON File Data to REST Service, TODO commenting for the time being
            try {
            	System.out.println("line 39");
                URL url = new URL("http://localhost:8080/EnquiryService/service/enquiryService");
                URLConnection connection = url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                out.write(jsonObject.toString());
                
                out.close();
                System.out.println("line 49");
                
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
 
                while (in.readLine() != null) {
                }
                System.out.println("\nREST Service Invoked Successfully..");
                in.close();
            } catch (Exception e) {
                System.out.println("\nError while calling REST Service");
                System.out.println(e);
            }
 		
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
    }
}
}