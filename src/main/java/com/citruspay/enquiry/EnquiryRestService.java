package com.citruspay.enquiry;

 
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
 




import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.citruspay.enquiry.api.EnquiryRequest;
import com.citruspay.enquiry.api.EnquiryResponse;
import com.citruspay.enquiry.api.EnquiryTransactionBase;
 
@Path("/enquiryService")
public class EnquiryRestService {
    @POST
//    @Path("/enquiryService")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public EnquiryResponse enquiry(EnquiryRequest enquiryRequest) {
    	System.out.println(" +++++++ HELLLO world +++++++++ request received "+enquiryRequest.getMerchantAccessKey()+":"+enquiryRequest.getMerchantTxnId());
    //	return null;
    	//enquiryRequest = new EnquiryRequest();
    	//enquiryRequest.setMerchantAccessKey("27AOYSJCQOR6VZ39V7JV");
    	//enquiryRequest.setMerchantTxnId("TXN55100");
    	return new EnquiryTransactionBase().enquiry(enquiryRequest);
    }
}