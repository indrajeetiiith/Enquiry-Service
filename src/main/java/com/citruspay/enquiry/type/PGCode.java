/*
 * Copyright (c) 2012 CitrusPay. All Rights Reserved.
 *
 * This software is the proprietary information of CitrusPay.
 * Use is subject to license terms.
 */
package com.citruspay.enquiry.type;

public enum PGCode {
	
	HDFC_3D("hdfc3D"), OPUS_PG("opusPG"), CC_AVENUE("ccavenueNetBanking"), EBS_GATEWAY("ebsGateway"),
		ICICI_PG("iciciPayseal"), CITRUS_PG("citrusPG"), AMEX_PG("amexPG"), HDFC_CITRUS("hfdcCitrus"),
			ICICI_CITRUS("iciciCitrus"), UBI_CITRUS("ubiCitrus"), UBI_PG("ubiPG"), BOB_PG("bobPG"),
				BOB_CITRUS("bobPG"), AMEX_EXT_PG("amexPG"), AXIS_PG("axisPG"), AXIS_EXT_PG("axisPG"),
					ICICI_WALLET_EXT_PG("iciciWallet"), ICICI_IMPS_PG("iciciImps"), ICICI_IMPS_EXT_PG("iciciImps"), 
						PREPAID_PG("prepaidPG"), IDBI_PG("idbiPG"), PAYZEN_PG_TEST("payzenPGTest"), PAYZEN_PG_PROD("payzenPGProd"),
							MIGS_PG("migsPG"), MIGS_EXT_PG("migsPG"),
						PNB_ATM_CITRUS("pnbAtmCitrus"), PNB_ATM_PG("pnbAtmPG"),BILLDESK("billDesk");

	private final String desc;

	private PGCode(String desc) {
		this.desc = desc;

	}

	public String getDesc() {
		return desc;
	}

}
