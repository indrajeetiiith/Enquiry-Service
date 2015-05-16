/*
* Copyright (c) 2012 CitrusPay. All Rights Reserved.
*
* This software is the proprietary information of CitrusPay.
* Use is subject to license terms.
*/
package com.citruspay.enquiry;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.PropertyNamingStrategy;
import org.codehaus.jackson.map.SerializationConfig.Feature;

import com.fasterxml.jackson.module.hibernate.HibernateModule;

public class HibernateAwareObjectMapper extends ObjectMapper {

	public HibernateAwareObjectMapper() {
		super();
		setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
		HibernateModule hm = new HibernateModule();
		registerModule(hm);
		configure(Feature.FAIL_ON_EMPTY_BEANS, false);
	}

	public void setPrettyPrint(boolean prettyPrint) {
		configure(Feature.INDENT_OUTPUT, prettyPrint);
	}
}