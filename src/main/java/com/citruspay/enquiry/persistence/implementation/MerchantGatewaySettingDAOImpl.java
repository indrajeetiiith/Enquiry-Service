/*
 * Copyright (c) 2012 CitrusPay. All Rights Reserved.
 *
 * This software is the proprietary information of CitrusPay.
 * Use is subject to license terms.
 */
package com.citruspay.enquiry.persistence.implementation;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;




import com.citruspay.enquiry.persistence.PersistenceManager;
import com.citruspay.enquiry.persistence.entity.MerchantGatewaySetting;
import com.citruspay.enquiry.persistence.interfaces.MerchantGatewaySettingDAO;

public class MerchantGatewaySettingDAOImpl implements MerchantGatewaySettingDAO {

	@Override
	public MerchantGatewaySetting findByMerchantAndGatewayCode(
			Integer merchantId, String gatewayCode) {
		EntityManager entityManager = null;
		
		entityManager = PersistenceManager.INSTANCE.getEntityManager();

		List<MerchantGatewaySetting> resultList = null;
		TypedQuery<MerchantGatewaySetting> query = entityManager
				.createQuery(
						"from MerchantGatewaySetting mg where mg.merchant.id=?1 and mg.paymentGateway.code =?2",
						MerchantGatewaySetting.class);
		query.setParameter(1, merchantId);
		query.setParameter(2, gatewayCode);
		resultList = query.getResultList();
		if (resultList != null && !resultList.isEmpty()) {
			return resultList.get(0);
		}
		return null;
	}

}
