package com.sunbird.entity.service;

import com.sunbird.entity.model.requestDTO.*;

import java.util.List;
import java.util.Map;

public interface EntityRelationshipService {

    void savePositionRoleMappings(PositionRoleRequest request);

    void saveRoleActivityMappings(RoleActivityRequest request);

    void saveCompetencyLevelMapping(CompetencyLevelRequest request);

    void saveActivityCompetencyLevelMapping(ActivityCompetencyLevelRequest request);

    Map<String, Object> getFullHierarchy(Integer positionId);

    List<Map<String, Object>> getActivitiesDetailsForRole(Integer roleId);

    List<Map<String, Object>> searchCompetencies(String keyword);
}