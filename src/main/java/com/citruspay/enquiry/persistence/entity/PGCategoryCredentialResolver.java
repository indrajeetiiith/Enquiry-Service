/*
 * Copyright (c) 2014 CitrusPay. All Rights Reserved.
 *
 * This software is the proprietary information of CitrusPay.
 * Use is subject to license terms.
 */

package com.citruspay.enquiry.persistence.entity;


import com.citruspay.enquiry.persistence.entity.Transaction;
import com.citruspay.enquiry.persistence.interfaces.MerchantGatewaySettingDAO;
import com.citruspay.enquiry.persistence.interfaces.PGCatCredentialDAO;
import com.citruspay.enquiry.persistence.entity.PGCatCredential;

public class PGCategoryCredentialResolver {

	private final MerchantGatewaySettingDAO merchantGatewaySettingDAO;
	private final PGCatCredentialDAO pgCatCredentialDAO;

	public PGCategoryCredentialResolver(
			MerchantGatewaySettingDAO merchantGatewaySettingDAO,
			PGCatCredentialDAO pgCatCredentialDAO) {
		this.merchantGatewaySettingDAO = merchantGatewaySettingDAO;
		this.pgCatCredentialDAO = pgCatCredentialDAO;
	}

	public PGCatCredential getPGCategoryCredential(Transaction transaction) {

		Integer id = merchantGatewaySettingDAO
				.findByMerchantAndGatewayId(transaction.getMerchant().getId(),
						transaction.getPgId()).getMerchantPGCategoryCode()
				.getPGCredentialId();
		return pgCatCredentialDAO.findById(id);
	}

}
