/*
 * Copyright (c) 2012 CitrusPay. All Rights Reserved.
 *
 * This software is the proprietary information of CitrusPay.
 * Use is subject to license terms.
 */

package com.citruspay.enquiry.persistence.interfaces;

import java.util.List;

import com.citruspay.enquiry.persistence.entity.Merchant;
import com.citruspay.enquiry.persistence.entity.MerchantKey;

public interface MerchantKeyDAO {

	public List<MerchantKey> findMerchantKeys(Merchant merchant);

	public MerchantKey findById(Integer id);

}
