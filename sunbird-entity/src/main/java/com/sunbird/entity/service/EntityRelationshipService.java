package com.sunbird.entity.service;

import com.sunbird.entity.model.dao.Entity;
import com.sunbird.entity.model.DTO.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface EntityRelationshipService {

    List<Entity> parseCsv(MultipartFile file);

    Entity createEntity(Entity entity);

    List<Map<String, Object>> searchEntities(String type, String keyword);

    List<MappingResultDTO> saveGenericRelationshipList(List<RelationshipRequest> requests);

    Entity updateEntity(Entity entity);

    List<Map<String, Object>> getFullHierarchy(String type, String parentCode);
}