package com.sunbird.entity.repository;

import java.util.List;
import java.util.Map;

/**
 * Interface to handle the customized query
 * 
 * @author nivetha
 *
 * @param <T>
 */
public interface CustomRepository<T> {

	/**
	 * Returns list of objects
	 * 
	 * @param query
	 *            String - query to be executed
	 * @param params
	 *            Map<String, Object> - values to be binded to the query
	 * @return List - rows fetched
	 */
	List<T> customFindAll(String query, Map<String, Object> params);

	/**
	 * Returns object
	 * 
	 * @param query
	 *            String - query to be executed
	 * @param params
	 *            Map<String, Object> - values to be binded to the query
	 * @return Object - row fecthed
	 */
	T customFindOne(String query, Map<String, Object> params);
}
