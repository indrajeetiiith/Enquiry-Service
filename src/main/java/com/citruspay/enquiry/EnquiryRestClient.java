package com.citruspay.enquiry;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
 
<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> b856f04f9362059bd510fec0f3a9b4a984435d96


import org.json.JSONObject;

import com.citruspay.enquiry.api.EnquiryRequest;
import com.citruspay.enquiry.api.EnquiryTransactionBase;
<<<<<<< HEAD
=======
=======
import org.json.JSONObject;
>>>>>>> 63cbfe1fb644b0ed860a22788730461b6a199082
>>>>>>> b856f04f9362059bd510fec0f3a9b4a984435d96
 
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
<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> b856f04f9362059bd510fec0f3a9b4a984435d96
            EnquiryRequest enquiryRequest = new EnquiryRequest();
            enquiryRequest.setMerchantAccessKey("27AOYSJCQOR6VZ39V7JV");
            enquiryRequest.setMerchantTxnId("TXN55100");
            System.out.println("calling enquiry");
            
            new EnquiryTransactionBase().enquiry(enquiryRequest);
            JSONObject jsonObject = new JSONObject(string);
            System.out.println(jsonObject);
 
/*            // Step2: Now pass JSON File Data to REST Service, TODO commenting for the time being
<<<<<<< HEAD
=======
=======
 
            JSONObject jsonObject = new JSONObject(string);
            System.out.println(jsonObject);
 
            // Step2: Now pass JSON File Data to REST Service
>>>>>>> 63cbfe1fb644b0ed860a22788730461b6a199082
>>>>>>> b856f04f9362059bd510fec0f3a9b4a984435d96
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
<<<<<<< HEAD
 			       }*/
=======
<<<<<<< HEAD
 			       }*/
=======
>>>>>>> 63cbfe1fb644b0ed860a22788730461b6a199082
>>>>>>> b856f04f9362059bd510fec0f3a9b4a984435d96
 
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
<<<<<<< HEAD
    }
}
=======
<<<<<<< HEAD
    }
}
=======
        }
    }
>>>>>>> 63cbfe1fb644b0ed860a22788730461b6a199082
>>>>>>> b856f04f9362059bd510fec0f3a9b4a984435d96
}