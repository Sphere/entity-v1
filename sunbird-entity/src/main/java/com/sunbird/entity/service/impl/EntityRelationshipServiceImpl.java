package com.sunbird.entity.service.impl;

import com.sunbird.entity.model.ActivityCompetencyLevelMapping;
import com.sunbird.entity.model.CompetencyLevelMapping;
import com.sunbird.entity.model.PositionRoleMapping;
import com.sunbird.entity.model.RoleActivityMapping;
import com.sunbird.entity.model.dao.Entity;
import com.sunbird.entity.model.dao.EntityDao;
import com.sunbird.entity.model.requestDTO.ActivityCompetencyLevelRequest;
import com.sunbird.entity.model.requestDTO.CompetencyLevelRequest;
import com.sunbird.entity.model.requestDTO.PositionRoleRequest;
import com.sunbird.entity.model.requestDTO.RoleActivityRequest;
import com.sunbird.entity.model.requestDTO.RoleActivityRequest;
import com.sunbird.entity.repository.elasticsearch.ActivityCompetencyLevelRepository;
import com.sunbird.entity.repository.elasticsearch.CompetencyLevelRepository;
import com.sunbird.entity.repository.elasticsearch.PositionRoleRepository;
import com.sunbird.entity.repository.elasticsearch.RoleActivityRepository;
import com.sunbird.entity.repository.jpa.EntitiesRepository;
import com.sunbird.entity.repository.jpa.EntityRepository;
import com.sunbird.entity.service.EntityRelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EntityRelationshipServiceImpl implements EntityRelationshipService {

    @Autowired
    private RoleActivityRepository roleActivityRepository;

    @Autowired
    private ElasticsearchTemplate esTemplate;

    @Autowired
    private PositionRoleRepository positionRoleRepository;


    @Autowired
    private ActivityCompetencyLevelRepository activityCompetencyLevelRepository;

    @Autowired
    private CompetencyLevelRepository competencyLevelRepository;

    @Autowired
    private EntitiesRepository entityRepository;

    @Override
    public void savePositionRoleMappings(PositionRoleRequest request) {
        for (Integer roleId : request.getRoleIds()) {
            String id = request.getPositionId() + ":" + roleId;
            PositionRoleMapping mapping = new PositionRoleMapping(id, request.getPositionId(), roleId);
            mapping.setId(request.getPositionId() + ":" + roleId);
            positionRoleRepository.save(mapping);
        }
    }

    @Override
    public void saveRoleActivityMappings(RoleActivityRequest request) {
        RoleActivityMapping mapping = new RoleActivityMapping(
                request.getRoleId(),
                request.getActivityIds()
        );
        roleActivityRepository.save(mapping);
    }

    @Override
    public void saveCompetencyLevelMapping(CompetencyLevelRequest request) {
        String id = "compLevel:" + request.getCompetencyId();
        CompetencyLevelMapping mapping = new CompetencyLevelMapping(id, request.getCompetencyId(), request.getLevelIds());
        competencyLevelRepository.save(mapping);
    }

    public void saveActivityCompetencyLevelMapping(ActivityCompetencyLevelRequest request) {
        if (request.getCompetencyLevelsMap() == null || request.getCompetencyLevelsMap().isEmpty()) {
            throw new IllegalArgumentException("competencyLevelsMap cannot be null or empty");
        }

        for (Integer compId : request.getCompetencyLevelsMap().keySet()) {
            if (compId == null) {
                throw new IllegalArgumentException("competencyId cannot be null");
            }
        }

        Optional<ActivityCompetencyLevelMapping> existingOpt =
                activityCompetencyLevelRepository.findById("actCompLevel:" + request.getActivityId());

        ActivityCompetencyLevelMapping mapping;
        if (existingOpt.isPresent()) {
            mapping = existingOpt.get();
            if (mapping.getCompetencyLevelsMap() == null) {
                mapping.setCompetencyLevelsMap(new HashMap<>());
            }
            mapping.getCompetencyLevelsMap().putAll(request.getCompetencyLevelsMap());
        } else {
            mapping = new ActivityCompetencyLevelMapping(
                    "actCompLevel:" + request.getActivityId(),
                    request.getActivityId(),
                    request.getCompetencyLevelsMap()
            );
        }

        activityCompetencyLevelRepository.save(mapping);
    }

    @Override
    public Map<String, Object> getFullHierarchy(Integer positionId) {
        Map<String, Object> result = new HashMap<>();

        // Position
        Optional<Entity> positionOpt = entityRepository.findById(positionId);
        if (!positionOpt.isPresent()) return result;

        Map<String, Object> positionMap = entityToMap(positionOpt.get());

        // Roles under position
        List<PositionRoleMapping> roleMappings = positionRoleRepository.findByPositionId(positionId);
        List<Map<String, Object>> rolesList = new ArrayList<>();

        for (PositionRoleMapping prMapping : roleMappings) {
            Integer roleId = prMapping.getRoleId();
            Optional<Entity> roleOpt = entityRepository.findById(roleId);
            if (!roleOpt.isPresent()) continue;

            Map<String, Object> roleMap = new HashMap<>();
            roleMap.put("role", entityToMap(roleOpt.get()));

            // Activities under role
            List<RoleActivityMapping> roleActivityMappings = roleActivityRepository.findByRoleId(roleId);
            List<Map<String, Object>> activitiesList = new ArrayList<>();

            for (RoleActivityMapping raMapping : roleActivityMappings) {
                for (Integer activityId : raMapping.getActivityIds()) {
                    Optional<Entity> activityOpt = entityRepository.findById(activityId);
                    if (!activityOpt.isPresent()) continue;

                    Map<String, Object> activityMap = new HashMap<>();
                    activityMap.put("activity", entityToMap(activityOpt.get()));

                    // Fetch the mapping which has competency -> list of levels
                    Optional<ActivityCompetencyLevelMapping> compMappingOpt =
                            activityCompetencyLevelRepository.findById("actCompLevel:" + activityId);

                    List<Map<String, Object>> competenciesList = new ArrayList<>();

                    if (compMappingOpt.isPresent()) {
                        ActivityCompetencyLevelMapping compMapping = compMappingOpt.get();
                        Map<Integer, List<Integer>> competencyLevelsMap = compMapping.getCompetencyLevelsMap();

                        if (competencyLevelsMap != null) {
                            for (Map.Entry<Integer, List<Integer>> entry : competencyLevelsMap.entrySet()) {
                                Integer compId = entry.getKey();
                                List<Integer> levelIds = entry.getValue();

                                Optional<Entity> compOpt = entityRepository.findById(compId);
                                if (!compOpt.isPresent()) continue;

                                Map<String, Object> compMap = new HashMap<>();
                                compMap.put("competency", entityToMap(compOpt.get()));

                                List<Map<String, Object>> levelsList = new ArrayList<>();
                                for (Integer levelId : levelIds) {
                                    Optional<Entity> levelOpt = entityRepository.findById(levelId);
                                    levelOpt.ifPresent(level -> levelsList.add(entityToMap(level)));
                                }

                                compMap.put("levels", levelsList);
                                competenciesList.add(compMap);
                            }
                        }
                    }

                    // attach competencies under activity
                    activityMap.put("children", competenciesList);
                    activitiesList.add(activityMap);
                }
            }

            // attach activities under role
            roleMap.put("children", activitiesList);
            rolesList.add(roleMap);
        }

        // attach roles under position
        positionMap.put("children", rolesList);
        result.put("position", positionMap);

        return result;
    }



    private Map<String, Object> entityToMap(Entity entity) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", entity.getId());
        map.put("type", entity.getType());
        map.put("name", entity.getName());
        map.put("description", entity.getDescription());
        map.put("additionalProperties", entity.getAdditionalProperties());
        map.put("status", entity.getStatus());
        map.put("source", entity.getSource());
        map.put("level", entity.getLevel());
        map.put("levelId", entity.getLevelId());
        map.put("isActive", entity.getIsActive());
        map.put("createdDate", entity.getCreatedDate());
        map.put("createdBy", entity.getCreatedBy());
        map.put("updatedDate", entity.getUpdatedDate());
        map.put("updatedBy", entity.getUpdatedBy());
        map.put("reviewedDate", entity.getReviewedDate());
        map.put("reviewedBy", entity.getReviewedBy());
        map.put("children", entity.getChildren());
        return map;
    }

    @Override
    public List<Map<String, Object>> getActivitiesDetailsForRole(Integer roleId) {
        List<RoleActivityMapping> mappings = roleActivityRepository.findByRoleId(roleId);
        List<Map<String, Object>> output = new ArrayList<>();

        for (RoleActivityMapping mapping : mappings) {
            for (Integer activityId : mapping.getActivityIds()) {
                Optional<Entity> actOpt = entityRepository.findById(activityId);
                if (!actOpt.isPresent()) continue;

                Map<String, Object> actMap = new HashMap<>();
                actMap.put("activity", actOpt.get());

                // Fetch the competency -> levels mapping
                Optional<ActivityCompetencyLevelMapping> compMappingOpt =
                        activityCompetencyLevelRepository.findById("actCompLevel:" + activityId);

                if (compMappingOpt.isPresent()) {
                    ActivityCompetencyLevelMapping compMapping = compMappingOpt.get();
                    Map<Integer, List<Integer>> competencyLevelsMap = compMapping.getCompetencyLevelsMap();
                    List<Map<String, Object>> competenciesList = new ArrayList<>();

                    if (competencyLevelsMap != null) {
                        for (Map.Entry<Integer, List<Integer>> entry : competencyLevelsMap.entrySet()) {
                            Integer compId = entry.getKey();
                            List<Integer> levelIds = entry.getValue();

                            Optional<Entity> compOpt = entityRepository.findById(compId);
                            if (!compOpt.isPresent()) continue;

                            Map<String, Object> compMap = new HashMap<>();
                            compMap.put("competency", compOpt.get());

                            List<Map<String, Object>> levelsList = new ArrayList<>();
                            for (Integer levelId : levelIds) {
                                Optional<Entity> levelOpt = entityRepository.findById(levelId);
                                levelOpt.ifPresent(level -> levelsList.add(Map.of("level", level)));
                            }

                            compMap.put("levels", levelsList);
                            competenciesList.add(compMap);
                        }
                    }

                    actMap.put("competencies", competenciesList);
                }

                output.add(actMap);
            }
        }

        return output;
    }

    @Override
    public List<Map<String, Object>> searchCompetencies(String keyword) {
        List<Entity> allComps = entityRepository.findByType("Competency");
        if (keyword != null && !keyword.trim().isEmpty()){
            allComps = allComps.stream()
                    .filter(c -> c.getName().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Entity comp : allComps) {
            Map<String, Object> compMap = new HashMap<>();
            compMap.put("competency", comp);

            Optional<CompetencyLevelMapping> levelMapping = competencyLevelRepository.findById("compLevel:" + comp.getId());
            levelMapping.ifPresent(lm -> {
                List<Map<String, Object>> levels = new ArrayList<>();
                for (Integer levelId : lm.getLevelIds()) {
                    Optional<Entity> levelEntity = entityRepository.findById(levelId);
                    levelEntity.ifPresent(l -> levels.add(Map.of("level", l)));
                }
                compMap.put("levels", levels);
            });

            result.add(compMap);
        }
        return result;
    }
}
