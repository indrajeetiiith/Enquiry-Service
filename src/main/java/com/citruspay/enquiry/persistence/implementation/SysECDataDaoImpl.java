package com.citruspay.enquiry.persistence.implementation;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.citruspay.enquiry.persistence.PersistenceManager;
import com.citruspay.enquiry.persistence.entity.SysECData;
import com.citruspay.enquiry.persistence.interfaces.SysECDataDao;

public class SysECDataDaoImpl implements SysECDataDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(SysECDataDaoImpl.class);
	
	public SysECData getSysECData() {

		SysECData data = null;
		EntityManager em = null;
		try {
			em = PersistenceManager.INSTANCE.getEntityManager();

			TypedQuery<SysECData> query = em.createQuery(
					"SELECT data from SysECData data", SysECData.class);

			data = query.getSingleResult();
			
		} catch (NoResultException e) {
		} catch (Exception e) {
			LOGGER.error("Error while fetch sys EC data", e);		
		}
		finally {
			try {
				if(em.isOpen())
				em.close();
			} catch (Exception e) {
				
			}
		}
		return data;
	}
}
