package com.citruspay.enquiry.encryption;

import com.citruspay.enquiry.persistence.entity.SysECData;
import com.citruspay.enquiry.persistence.implementation.SysECDataDaoImpl;
import com.citruspay.enquiry.persistence.interfaces.SysECDataDao;

public enum SysECConfig {
	INSTANCE;
	
	private SysECConfig() {
		SysECDataDao dao = new SysECDataDaoImpl();
		sysECData = dao.getSysECData();
	}
	
	private SysECData sysECData;

	public SysECData getSysECData() {
		return sysECData;
	}

	
}
