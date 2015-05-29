package com.citruspay.enquiry.configuration.innerconfig;

import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class holds all properties of application.
 * Instance of this class should be accessed via AppConfigManager.
 * 
 * @author Indrajeet
 *
 */
public class AppConfig {

	private final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);
	
	private Properties properties;
	
	
	public AppConfig() {
		loadPropertiesFile();
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	private void loadPropertiesFile() {
		LOGGER.info("Loading properties");
		
		properties = new Properties();

		try {
			
			ClassLoader classLoader = getClass().getClassLoader();
			InputStream resourceAsStream = classLoader.getResourceAsStream("properties"+File.separatorChar+"enquiry-service.properties");
			properties.load(resourceAsStream);
			
		} catch (Exception e) {
			LOGGER.error("Error while loading properties", e);
		}
	}

	/**
	 * Returns properties with key/value pair for all keys that starts
	 * with <b>prefix</b> and separated by DOT (.). Returned properties contains keys with removed prefix.
	 * 
	 * @param prefix
	 * @return
	 */
	public Properties getPropertiesWithPrefix(String prefix) {
		
		Properties p = new Properties();
		
		Enumeration<Object> keys = properties.keys();
		while (keys.hasMoreElements()) {
			String k = keys.nextElement().toString();
			
			String prefix2 = prefix + ".";
			if(k.startsWith(prefix2)) {
				
				String substring = k.substring(prefix2.length());
				if(substring.length() > 0)
					p.put(substring, properties.get(k));
			}
		}
		
		return p;
	}
	
	
}
