package com.citruspay.enquiry;



import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

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
    	try {
    		return new EnquiryTransactionBase().enquiry(enquiryRequest);
    	} catch(Exception e) {
        	return  null;
    	}

    }
}