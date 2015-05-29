package com.citruspay.enquiry.persistence.interfaces;

import java.io.Serializable;
import java.util.List;

import com.citruspay.BaseDomain;

/**
 * Base DAO that will be extended by all other DAO.
 * Provides basic CRUD and other utility operations methods for DB
 *
 * @param <T extends BaseDomain>
 * @param <ID extends Serializable>
 * 
 * @author Badal
 */
public interface BaseDAO<T extends BaseDomain, ID extends Serializable> { 
	
	/**
    * Get the Class of the entity
    *
    * @return the class
    */
   Class<T> getEntityClass();

   /**
    * Find an entity by its primary key
    *
    * @param id the primary key
    * @return the entity
    */
   T findById(final ID id);

   /**
    * Load all entities
    *
    * @return the list of entities
    */
   List<T> findAll();

   /**
    * save an entity. This can be either a INSERT or UPDATE in the database
    * 
    * @param entity the entity to save
    * 
    * @return the saved entity
    */
   T save(final T entity);

   /**
    * delete an entity from the database
    * 
    * @param entity the entity to delete
    */
   void delete(final T entity);
   }