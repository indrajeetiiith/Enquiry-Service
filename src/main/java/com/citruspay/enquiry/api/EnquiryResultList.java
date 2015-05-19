/*
* Copyright (c) 2012 CitrusPay. All Rights Reserved.
*
* This software is the proprietary information of CitrusPay.
* Use is subject to license terms.
*/
package com.citruspay.enquiry.api;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class EnquiryResultList {
	
	public EnquiryResultList() {
	}

	public EnquiryResultList(List<EnquiryResult> enquiryResultResult) {
		super();
		this.enquiryResultList = enquiryResultResult;
	}

	List<EnquiryResult> enquiryResultList;

	public List<EnquiryResult> getEnquiryResultList() {
		return enquiryResultList;
	}

	public void setEnquiryResultList(List<EnquiryResult> enquiryResultList) {
		this.enquiryResultList = enquiryResultList;
	}
	
	
	
}
