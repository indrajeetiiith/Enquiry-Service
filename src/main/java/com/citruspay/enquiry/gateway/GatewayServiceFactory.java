package com.citruspay.enquiry.gateway;

import com.citruspay.enquiry.type.PGCode;

public class GatewayServiceFactory  {
	public GatewayService getGatewayService(String code) {
		
		if (PGCode.HDFC_3D.toString().equals(code)
				|| PGCode.HDFC_CITRUS.toString().equals(code)) {
			return new HDFC3DGatewayService();
		} else if (PGCode.ICICI_PG.toString().equals(code)
				|| PGCode.ICICI_CITRUS.toString().equals(code)) {
			return new ICICIGatewayService();
		} else if (PGCode.BOB_PG.toString().equals(code)
				|| PGCode.BOB_CITRUS.toString().equals(code)) {
			return new BOBGatewayService();
		}else if (PGCode.UBI_PG.toString().equals(code)
					|| PGCode.UBI_CITRUS.toString().equals(code)) {
				return new UBIGatewayService();
		}else if (PGCode.AXIS_PG.toString().equals(code)
					|| PGCode.AXIS_EXT_PG.toString().equals(code)) {
				return new AXISGatewayService();
		}else if (PGCode.MIGS_PG.toString().equals(code)
				|| PGCode.MIGS_EXT_PG.toString().equals(code)) {
			return new MIGSGatewayService();
		}else if (PGCode.PNB_ATM_CITRUS.toString().equals(code) || PGCode.PNB_ATM_PG.toString().equals(code)) {
			return new PNBATMGatewayService();
		}else if (PGCode.AMEX_PG.toString().equals(code)
			|| PGCode.AMEX_EXT_PG.toString().equals(code)) {
			return new AMEXGatewayService();
		}else if (PGCode.BILLDESK.toString().equals(code)) {
			return new BillDeskGatewayService();
		}

		return null;
	}
	
	
}
