package com.sunbird.entity.service;

import com.sunbird.entity.model.dao.EntityDao;

public interface AuditService {

	public void addEntityAudit(EntityDao oldObj, EntityDao updatedObj);

}
