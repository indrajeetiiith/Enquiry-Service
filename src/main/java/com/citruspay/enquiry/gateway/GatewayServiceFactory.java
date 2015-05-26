package com.citruspay.enquiry.gateway;

import com.citruspay.CommonUtil;
import com.citruspay.enquiry.type.PGCode;

public class GatewayServiceFactory  {
	public GatewayService getGatewayService(String code) {
		
		if (CommonUtil.isNotNull(code) && code.contains("OPUS")) {
		//	return new OPUSGatewayService();
		} else if (PGCode.HDFC_3D.toString().equals(code)
				|| PGCode.HDFC_CITRUS.toString().equals(code)) {
			return new HDFC3DGatewayService();
		}
		return null;
	}
	
}
