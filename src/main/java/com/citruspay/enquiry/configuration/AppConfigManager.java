package com.citruspay.enquiry.configuration;

import com.citruspay.enquiry.configuration.innerconfig.AppConfig;
import com.citruspay.enquiry.configuration.innerconfig.FreemarkerTemplateConfig;
import com.citruspay.enquiry.configuration.innerconfig.JdbcConfig;

public enum AppConfigManager {
	INSTANCE;
	
	private AppConfig appConfig;
	private JdbcConfig jdbcConfig;
	private FreemarkerTemplateConfig freemarkerTemplateConfig;
	
	private AppConfigManager() {
		appConfig = new AppConfig();
		jdbcConfig = new JdbcConfig(appConfig);
		freemarkerTemplateConfig = new FreemarkerTemplateConfig(appConfig);
	}

	public AppConfig getAppConfig() {
		return appConfig;
	}

	public JdbcConfig getJdbcConfig() {
		return jdbcConfig;
	}

	public FreemarkerTemplateConfig getFreemarkerTemplateConfig() {
		return freemarkerTemplateConfig;
	}

}
