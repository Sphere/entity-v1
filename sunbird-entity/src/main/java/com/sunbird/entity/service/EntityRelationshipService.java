package com.sunbird.entity.service;

import com.sunbird.entity.model.requestDTO.*;

import java.util.List;
import java.util.Map;

public interface EntityRelationshipService {

    void savePositionRoleMappings(PositionRoleRequest request);

    void saveRoleCompetencyMappings(RoleCompetencyRequest request);

    void saveCompetencyLevelMapping(CompetencyLevelRequest request);

    void saveActivityCompetencyLevelMapping(ActivityCompetencyLevelRequest request);

    Map<String, Object> getFullHierarchy(Integer positionId);

    List<Map<String, Object>> getCompetencyDetailsForRole(Integer roleId);

    List<Map<String, Object>> searchCompetencies(String keyword);
}