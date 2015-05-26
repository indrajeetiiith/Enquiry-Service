package com.citruspay.enquiry.configuration.innerconfig;

import java.util.Properties;

/**
 * This class holds all JDBC related properties required by application.
 * Instance of this class should be accessed via AppConfigManager.
 * 
 * @author piyush
 *
 */
public class JdbcConfig {
	
	private final String urlKey;
	private final String urlValue;
	
	private final String userKey;
	private final String userValue;

	private final String passwordKey;
	private final String passwordValue;


	public JdbcConfig(AppConfig appConfig) {

		Properties p = null;
		
		p = appConfig.getPropertiesWithPrefix("jdbc.url");
		urlKey = p.getProperty("key");
		urlValue = p.getProperty("value");
		
		p = appConfig.getPropertiesWithPrefix("jdbc.user");
		userKey = p.getProperty("key");
		userValue = p.getProperty("value");
				
		p = appConfig.getPropertiesWithPrefix("jdbc.password");
		passwordKey = p.getProperty("key");
		passwordValue = p.getProperty("value");
		
	}

	public String getUrlKey() {
		return urlKey;
	}

	public String getUrlValue() {
		return urlValue;
	}

	public String getUserKey() {
		return userKey;
	}

	public String getUserValue() {
		return userValue;
	}

	public String getPasswordKey() {
		return passwordKey;
	}

	public String getPasswordValue() {
		return passwordValue;
	}

}
