package com.sunbird.entity.repository;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

public class CustomRepositoryImpl<T> implements CustomRepository<T> {

	@PersistenceContext
	EntityManager entityManager;

	@Override
	public List<T> customFindAll(String query, Map params) {
		return (List<T>) getQueryStatement(query, params).getResultList();
	}

	@Override
	public T customFindOne(String query, Map params) {
		List<Object> result = getQueryStatement(query, params).getResultList();
		if (result != null && result.size() > 0) {
			return (T) result.get(0);
		}
		return null;
	}

	/**
	 * Binds the parameter values to the query to be executed
	 * 
	 * @param query
	 *            String - query to be executed
	 * @param params
	 *            Map<String, Object> - Values to bind in the query
	 * @return Query - query statement
	 */
	private Query getQueryStatement(String query, Map<String, Object> params) {
		Query queryObj = entityManager.createQuery(query);
		for (Map.Entry<String, Object> paramsEntry : params.entrySet()) {
			queryObj.setParameter(paramsEntry.getKey(), paramsEntry.getValue());
		}
		return queryObj;
	}

}
