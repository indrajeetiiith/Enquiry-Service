package com.citruspay.enquiry.persistence;


import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public enum PersistenceManager {

	INSTANCE;

	private final Logger LOGGER = LoggerFactory.getLogger(PersistenceManager.class);

	private EntityManagerFactory emFactory;

	private PersistenceManager() {
		LOGGER.info("Initializing EntityManagerFactory Singleton");
		// "dynamic-pricing" was the value of the name attribute of the
		// persistence-unit element.
		Properties properties = new Properties();
		properties.setProperty("javax.persistence.jdbc.url", "jdbc:mysql://localhost:3306/citruspay");
		properties.setProperty("javax.persistence.jdbc.user", "root");
		properties.setProperty("javax.persistence.jdbc.password", "root");

		emFactory = Persistence.createEntityManagerFactory("enquiry-service",properties);
	}

	public EntityManager getEntityManager() {
		EntityManager entityManager = null;
		try {
			entityManager = emFactory.createEntityManager();
			if(!entityManager.isOpen())
				LOGGER.warn("Entity Manager was not created");

		} catch (Exception e) {
			LOGGER.error("Problem in graceful closing of Entity manager factory", e);
			throw new RuntimeException("Entity manager factory has been closed or NOT initialized properly " + e.getMessage());
		}
		return entityManager;
	}

	public void close() {
		try {
			LOGGER.info("Closing EntityManagerFactory");
			emFactory.close();
		} catch (Exception e) {
			LOGGER.error("Problem in graceful closing of Entity manager factory");
		}
	}
}