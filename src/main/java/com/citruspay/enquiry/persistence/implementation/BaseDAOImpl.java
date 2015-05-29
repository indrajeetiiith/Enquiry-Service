package com.citruspay.enquiry.persistence.implementation;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;

import com.citruspay.BaseDomain;
import com.citruspay.CommonUtil;
import com.citruspay.enquiry.persistence.interfaces.BaseDAO;

/**
 * Base DAO that will be extended by all other DAO. Provides basic CRUD and
 * other utility operations methods for DB Entity manager associated is for AD
 * BD
 * 
 * @param <T extends BaseDomain>
 * @param <ID extends Serializable>
 * 
 * @author Badal
 */
public class BaseDAOImpl<T extends BaseDomain, ID extends Serializable>
		implements BaseDAO<T, ID> {

	private final Class<T> persistentClass;

	@PersistenceContext
	protected EntityManager entityManager;

	@SuppressWarnings("unchecked")
	public BaseDAOImpl() {
		this.persistentClass = (Class<T>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public BaseDAOImpl(final Class<T> persistentClass) {
		super();
		this.persistentClass = persistentClass;
	}

	@SuppressWarnings("unchecked")
	public List<T> findAll() {
		Session session = (Session) getEntityManager().getDelegate();
		Criteria criteria = session.createCriteria(getEntityClass());
		final List<T> result = criteria.list();
		return result;
	}

	public T findById(final ID id) {
		final T result = getEntityManager().find(persistentClass, id);
		return result;
	}

	public Class<T> getEntityClass() {
		return persistentClass;
	}

	/**
	 * set the JPA entity manager to use.
	 * 
	 * @param entityManager
	 */
	/*
	 * @Required
	 * 
	 * @PersistenceContext public void setEntityManager(final EntityManager
	 * entityManager) { this.entityManager = entityManager; }
	 */

	public EntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * save an entity
	 */
	public T save(T entity) {
		final T savedEntity = getEntityManager().merge(entity);
		return savedEntity;
	}

	/**
	 * delete an entity
	 */

	public void delete(T entity) {
		getEntityManager().remove(entity);
	}

	/**
	 * @param sqlQuery
	 * @param merchantIds
	 * @return
	 */
	public StringBuilder addMerchantList(StringBuilder sqlQuery,
			List<Integer> merchantIds) {

		StringBuilder merchantList = new StringBuilder();
		if (CommonUtil.isNotEmpty(merchantIds)) {

			for (Integer merchantId : merchantIds) {
				if (merchantIds.indexOf(merchantId) == merchantIds.size() - 1) {
					merchantList.append(merchantId);
				} else {
					merchantList.append(merchantId + ",");
				}

			}

		}

		while (sqlQuery.indexOf(":merchantId") > 0) {
			sqlQuery.replace(sqlQuery.indexOf(":merchantId"),
					sqlQuery.indexOf(":merchantId") + ":merchantId".length(),
					merchantList.toString());
		}

		return sqlQuery;

	}

}
