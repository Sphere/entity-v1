package com.sunbird.entity.service;

import java.util.List;
import java.util.Map;

import com.sunbird.entity.model.Bookmark;
import com.sunbird.entity.model.EntityRelation;
import com.sunbird.entity.model.EntityVerification;
import com.sunbird.entity.model.SearchObject;
import com.sunbird.entity.model.UserProfile;
import com.sunbird.entity.model.dao.EntityDao;

public interface EntityService {

	public EntityDao addUpdateEntity(EntityDao entityDao, String userId);

	public EntityDao getEntityById(Integer id, SearchObject searchObject);

	public Boolean addEntityRelation(EntityRelation entityRelation);

	Boolean bookmarkEntity(Bookmark bookmarkEntityNode, String userId);

	public void addEntities(List<EntityDao> entityList);

	public List<EntityDao> getAllDataNodes(SearchObject searchObject);

	Boolean deleteEntity(Integer id);

	Boolean addFeedback(Map<String, Object> feedbackDocument);

	public Boolean reviewEntity(EntityVerification entityVerification, UserProfile userProfile);

}
