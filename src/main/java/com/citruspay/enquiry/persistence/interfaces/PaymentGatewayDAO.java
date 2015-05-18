/*
 * Copyright (c) 2012 CitrusPay. All Rights Reserved.
 *
 * This software is the proprietary information of CitrusPay.
 * Use is subject to license terms.
 */
package com.citruspay.enquiry.persistence.interfaces;

import com.citruspay.enquiry.persistence.entity.PaymentGateway;

public interface PaymentGatewayDAO {

	public PaymentGateway findById(Integer id);

}
