package com.sunbird.entity.service;

import com.sunbird.entity.model.dao.Entity;
import com.sunbird.entity.model.requestDTO.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface EntityRelationshipService {

    Map<String, Object> getFullHierarchy(Integer positionId);

    List<Map<String, Object>> getDetailsForSpecifiedEntityType(String type,Integer typeId);

    List<Entity> parseCsv(MultipartFile file);

    Entity createEntity(Entity entity);

    void saveGenericRelationship(RelationshipRequest request);

    List<Map<String, Object>> searchEntities(String type, String keyword);
}