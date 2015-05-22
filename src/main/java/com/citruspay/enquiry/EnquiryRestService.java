package com.citruspay.enquiry;



import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.citruspay.CommonUtil;
import com.citruspay.enquiry.api.EnquiryRequest;
import com.citruspay.enquiry.api.EnquiryResponse;
import com.citruspay.enquiry.api.EnquiryTransactionBase;
 
@Path("/enquiryService")
public class EnquiryRestService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(EnquiryTransactionBase.class);
	private static final int INTERNAL_SERVER_ERROR = 500;
	private static final String INTERNAL_SERVER_ERROR_Msg = "INTERNAL_SERVER_ERROR";
	public static final int BAD_REQUEST = 400;
	private static final String BAD_REQUEST_MSG = "Enquiry Request is null";



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
    		if(CommonUtil.isNull(enquiryRequest)) {
    			LOGGER.error("Bad Request:: received request for Enquiry API is null");
        		EnquiryResponse enquiryResponse = new EnquiryResponse();
        		enquiryResponse.setRespCode(BAD_REQUEST);
        		enquiryResponse.setRespMsg(BAD_REQUEST_MSG);
            	return  enquiryResponse;
    		}

    		else {
    			
    			return new EnquiryTransactionBase().enquiry(enquiryRequest);
    		}
    	} catch(Exception e) {
    		EnquiryResponse enquiryResponse = new EnquiryResponse();
    		enquiryResponse.setRespCode(INTERNAL_SERVER_ERROR);
    		enquiryResponse.setRespMsg(INTERNAL_SERVER_ERROR_Msg);
        	return  enquiryResponse;
    	}

    }
}