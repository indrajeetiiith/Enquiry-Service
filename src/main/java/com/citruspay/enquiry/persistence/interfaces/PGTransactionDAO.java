package com.citruspay.enquiry.persistence.interfaces;

import com.citruspay.enquiry.persistence.entity.PGTransaction;

public interface PGTransactionDAO {

	
	PGTransaction findById(Integer id);

}
