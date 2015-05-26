package com.citruspay.enquiry.configuration.innerconfig;

import java.util.Properties;

/**
 * This class holds all JDBC related properties required by application.
 * Instance of this class should be accessed via AppConfigManager.
 * 
 * @author piyush
 *
 */
public class FreemarkerTemplateConfig {
	
	private final Properties templateFiles;


	public FreemarkerTemplateConfig(AppConfig appConfig) {

		templateFiles = appConfig.getPropertiesWithPrefix("freemarker.template");
		
	}

	public Properties getTemplateFiles() {
		return templateFiles;
	}

}
