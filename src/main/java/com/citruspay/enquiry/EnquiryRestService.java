package com.citruspay.enquiry;



import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.citruspay.enquiry.api.EnquiryRequest;
import com.citruspay.enquiry.api.EnquiryResponse;
import com.citruspay.enquiry.api.EnquiryTransactionBase;
 
@Path("/enquiryService")
public class EnquiryRestService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(EnquiryTransactionBase.class);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * This is the rest service entry point.
     * @param enquiryRequest
     * @return
     */
    public EnquiryResponse enquiry(EnquiryRequest enquiryRequest) {
		LOGGER.info("received request for Enquiry API");

    	try {
    		return new EnquiryTransactionBase().enquiry(enquiryRequest);
    	} catch(Exception e) {
        	return  null;
    	}

    }
}