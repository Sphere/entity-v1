package com.sunbird.entity.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.sunbird.common.factory.EsClientFactory;
import org.sunbird.common.inf.ElasticSearchService;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunbird.entity.model.Audit;
import com.sunbird.entity.model.EntityLog;
import com.sunbird.entity.model.dao.EntityDao;
import com.sunbird.entity.service.AuditService;
import com.sunbird.entity.util.Constants;
import com.sunbird.entity.util.DateUtils;
import com.sunbird.entity.util.ServerProperties;

@Repository
public class AuditServiceImpl implements AuditService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuditServiceImpl.class);

	private ObjectMapper mapper = new ObjectMapper();

	private final ElasticSearchService esService = EsClientFactory.getInstance("rest");

	@Autowired
	ServerProperties serverProperties;

	@Override
	public void addEntityAudit(EntityDao oldObj, EntityDao updatedObj) {
		new Thread(() -> {
			try {
				EntityLog entityLog = new EntityLog();
				entityLog.setId(updatedObj.getId());
				entityLog.setType(updatedObj.getType());
				entityLog.setTimestamp(DateUtils.getCurrentTimestamp());
				entityLog.setUpdatedDate(DateUtils.getCurrentDateTime());
				entityLog.setUpdatedBy(updatedObj.getUpdatedBy());
				entityLog.setOldObject(oldObj);
				entityLog.setUpdatedObject(updatedObj);
				Map<String, Audit> changes = findUpdatedField(oldObj, updatedObj);
				if (changes != null && changes.size() > 0) {
					LOGGER.info("Saving the audit changes : " + changes);
					entityLog.setChanges(changes);

					esService.save(serverProperties.getEntityAuditIndex(),
							RandomStringUtils.random(15, Boolean.TRUE, Boolean.TRUE),
							mapper.convertValue(entityLog, Map.class));
				}
			} catch (Exception e) {
				LOGGER.info(String.format(Constants.Exception.EXCEPTION_METHOD, "addEntityAudit", e.getMessage()));
			}

		}).start();
	}

	/**
	 * Track audit changes for entity object
	 * 
	 * @param oldObj
	 *            EntityDao
	 * @param updatedObj
	 *            EntityDao
	 * @return Map<String, Audit>
	 * @throws Exception
	 */
	private Map<String, Audit> findUpdatedField(EntityDao oldObj, EntityDao updatedObj) throws Exception {
		Map<String, Audit> changes = new HashMap<>();
		getAudit(oldObj.getName(), updatedObj.getName(), Constants.Parameters.NAME, changes);
		getAudit(oldObj.getDescription(), updatedObj.getDescription(), Constants.Parameters.DESCRIPTION, changes);
		getAudit(oldObj.getStatus(), updatedObj.getStatus(), Constants.Parameters.STATUS, changes);
		getAudit(oldObj.getSource(), updatedObj.getSource(), Constants.Parameters.SOURCE, changes);
		getAudit(oldObj.getLevel(), updatedObj.getLevel(), Constants.Parameters.LEVEL, changes);
		getMapFields(oldObj.getAdditionalProperties(), updatedObj.getAdditionalProperties(), changes);
		return changes;
	}

	/**
	 * Sets the audit information
	 * 
	 * @param oldObj
	 *            Object
	 * @param updatedObj
	 *            Object
	 * @param field
	 *            String
	 * @param changes
	 *            Map<String, Audit>
	 */
	private void getAudit(Object oldObj, Object updatedObj, String field, Map<String, Audit> changes) {
		Audit audit = new Audit();
		audit.setField(field);

		if (oldObj == null && updatedObj != null) {
			audit.setChangedTo(updatedObj);
			audit.setAction(Constants.Actions.CREATE);
		} else if (oldObj != null && updatedObj != null && oldObj != updatedObj) {
			audit.setChangedFrom(oldObj);
			audit.setChangedTo(updatedObj);
			audit.setAction(Constants.Actions.UPDATE);
		} else if (oldObj != null && updatedObj == null) {
			audit.setChangedFrom(oldObj);
			audit.setAction(Constants.Actions.REMOVE);
		}

		if (StringUtils.isNotBlank(audit.getAction())) {
			changes.put(field, audit);
		}
	}

	/**
	 * Finds the changes in field of type map
	 * 
	 * @param oldObj
	 *            Object
	 * @param updatedObj
	 *            Object
	 * @param changes
	 *            Map<String, Audit>
	 * @throws Exception
	 */
	private void getMapFields(Object oldObj, Object updatedObj, Map<String, Audit> changes) throws Exception {

		Map<String, Object> oldObjMap = mapper.convertValue(oldObj, new TypeReference<Map<String, Object>>() {
		});
		Map<String, Object> updatedObjMap = mapper.convertValue(updatedObj, new TypeReference<Map<String, Object>>() {
		});

		Set<String> keySet = new HashSet<>();
		keySet.addAll(oldObjMap.keySet());
		keySet.addAll(updatedObjMap.keySet());
		for (String key : keySet) {
			getAudit(oldObjMap.get(key), updatedObjMap.get(key), key, changes);
		}

	}

}
