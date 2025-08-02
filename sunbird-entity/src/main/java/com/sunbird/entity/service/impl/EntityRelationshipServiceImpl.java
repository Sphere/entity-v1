package com.sunbird.entity.service.impl;

import com.sunbird.entity.model.ActivityCompetencyLevelMapping;
import com.sunbird.entity.model.CompetencyLevelMapping;
import com.sunbird.entity.model.PositionRoleMapping;
import com.sunbird.entity.model.RoleCompetencyMapping;
import com.sunbird.entity.model.dao.Entity;
import com.sunbird.entity.model.dao.EntityDao;
import com.sunbird.entity.model.requestDTO.ActivityCompetencyLevelRequest;
import com.sunbird.entity.model.requestDTO.CompetencyLevelRequest;
import com.sunbird.entity.model.requestDTO.PositionRoleRequest;
import com.sunbird.entity.model.requestDTO.RoleCompetencyRequest;
import com.sunbird.entity.repository.elasticsearch.ActivityCompetencyLevelRepository;
import com.sunbird.entity.repository.elasticsearch.CompetencyLevelRepository;
import com.sunbird.entity.repository.elasticsearch.PositionRoleRepository;
import com.sunbird.entity.repository.elasticsearch.RoleCompetencyRepository;
import com.sunbird.entity.repository.jpa.EntitiesRepository;
import com.sunbird.entity.repository.jpa.EntityRepository;
import com.sunbird.entity.service.EntityRelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EntityRelationshipServiceImpl implements EntityRelationshipService {

    @Autowired
    private RoleCompetencyRepository roleCompetencyRepository;

    @Autowired
    private ElasticsearchRestTemplate esTemplate;

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
    public void saveRoleCompetencyMappings(RoleCompetencyRequest request) {
        for (Integer compId : request.getCompetencyIds()) {
            String id = request.getRoleId() + ":" + compId;
            RoleCompetencyMapping mapping = new RoleCompetencyMapping(id, request.getRoleId(), compId);
            roleCompetencyRepository.save(mapping);
        }
    }

    @Override
    public void saveCompetencyLevelMapping(CompetencyLevelRequest request) {
        String id = "compLevel:" + request.getCompetencyId();
        CompetencyLevelMapping mapping = new CompetencyLevelMapping(id, request.getCompetencyId(), request.getLevelIds());
        competencyLevelRepository.save(mapping);
    }

    @Override
    public void saveActivityCompetencyLevelMapping(ActivityCompetencyLevelRequest request) {
        String id = "actCompLevel:" + request.getActivityId() + ":" + request.getCompetencyId();
        ActivityCompetencyLevelMapping mapping = new ActivityCompetencyLevelMapping(id, request.getActivityId(), request.getCompetencyId(), request.getLevelIds());
        activityCompetencyLevelRepository.save(mapping);
    }

    @Override
    public Map<String, Object> getFullHierarchy(Integer positionId) {
        Map<String, Object> result = new HashMap<>();

        Optional<Entity> positionOpt = entityRepository.findById(positionId);
        if (!positionOpt.isPresent()) return result;

        Map<String, Object> positionMap = entityToMap(positionOpt.get());

        List<PositionRoleMapping> roleMappings = positionRoleRepository.findByPositionId(positionId);
        List<Map<String, Object>> rolesList = new ArrayList<>();

        for (PositionRoleMapping prMapping : roleMappings) {
            Integer roleId = prMapping.getRoleId();
            Optional<Entity> roleOpt = entityRepository.findById(roleId);
            if (!roleOpt.isPresent()) continue;

            Map<String, Object> roleMap = new HashMap<>();
            roleMap.put("role", entityToMap(roleOpt.get()));

            // Instead of "competencies" key, put competencies inside "children"
            List<RoleCompetencyMapping> compMappings = roleCompetencyRepository.findByRoleId(roleId);
            List<Map<String, Object>> competenciesList = new ArrayList<>();

            for (RoleCompetencyMapping rcMapping : compMappings) {
                Integer compId = rcMapping.getCompetencyId();
                Optional<Entity> compOpt = entityRepository.findById(compId);
                if (!compOpt.isPresent()) continue;

                Map<String, Object> compMap = new HashMap<>();
                compMap.put("competency", entityToMap(compOpt.get()));

                Optional<CompetencyLevelMapping> levelMappingOpt = competencyLevelRepository.findById("compLevel:" + compId);
                if (levelMappingOpt.isPresent()) {
                    List<Map<String, Object>> levels = new ArrayList<>();
                    for (Integer levelId : levelMappingOpt.get().getLevelIds()) {
                        Optional<Entity> levelOpt = entityRepository.findById(levelId);
                        levelOpt.ifPresent(level -> levels.add(entityToMap(level)));
                    }
                    compMap.put("levels", levels);
                } else {
                    compMap.put("levels", new ArrayList<>());
                }

                List<ActivityCompetencyLevelMapping> activityMappings = activityCompetencyLevelRepository.findByCompetencyId(compId);
                List<Map<String, Object>> activities = new ArrayList<>();
                for (ActivityCompetencyLevelMapping activityMapping : activityMappings) {
                    Optional<Entity> activityOpt = entityRepository.findById(activityMapping.getActivityId());
                    if (activityOpt.isPresent()) {
                        Map<String, Object> activityMap = new HashMap<>();
                        activityMap.put("activity", entityToMap(activityOpt.get()));
                        activityMap.put("levelIds", activityMapping.getLevelIds());
                        activities.add(activityMap);
                    }
                }
                compMap.put("activities", activities);

                competenciesList.add(compMap);
            }

            // Put competencies inside "children" of roleMap
            roleMap.put("children", competenciesList);

            rolesList.add(roleMap);
        }

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
    public List<Map<String, Object>> getCompetencyDetailsForRole(Integer roleId) {
        List<RoleCompetencyMapping> compMappings = roleCompetencyRepository.findByRoleId(roleId);
        List<Map<String, Object>> output = new ArrayList<>();

        for (RoleCompetencyMapping mapping : compMappings) {
            Integer compId = mapping.getCompetencyId();
            Optional<Entity> compOpt = entityRepository.findById(compId);
            if (!compOpt.isPresent()) continue;

            Map<String, Object> compMap = new HashMap<>();
            compMap.put("competency", compOpt.get());

            // Add levels
            Optional<CompetencyLevelMapping> levelMapping = competencyLevelRepository.findById("compLevel:" + compId);
            levelMapping.ifPresent(lm -> {
                List<Map<String, Object>> levels = new ArrayList<>();
                for (Integer levelId : lm.getLevelIds()) {
                    Optional<Entity> levelEntity = entityRepository.findById(levelId);
                    levelEntity.ifPresent(l -> levels.add(Map.of("level", l)));
                }
                compMap.put("levels", levels);
            });

            // Add activities
            List<ActivityCompetencyLevelMapping> actMappings = activityCompetencyLevelRepository.findByCompetencyId(compId);
            List<Map<String, Object>> activities = new ArrayList<>();
            for (ActivityCompetencyLevelMapping act : actMappings) {
                Optional<Entity> actEntity = entityRepository.findById(act.getActivityId());
                actEntity.ifPresent(a -> activities.add(Map.of("activity", a, "levelIds", act.getLevelIds())));
            }
            compMap.put("activities", activities);

            output.add(compMap);
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
