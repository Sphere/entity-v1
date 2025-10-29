package com.sunbird.entity.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunbird.entity.model.*;
import com.sunbird.entity.model.dao.Entity;
import com.sunbird.entity.model.requestDTO.*;
import com.sunbird.entity.repository.elasticsearch.*;
import com.sunbird.entity.repository.jpa.EntitiesRepository;
import com.sunbird.entity.repository.elasticsearch.EntityESRepository;
import com.sunbird.entity.service.EntityRelationshipService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EntityRelationshipServiceImpl implements EntityRelationshipService {


    @Autowired
    private ElasticsearchTemplate esTemplate;

    @Autowired
    private EntitiesRepository entityRepository;

    @Autowired
    private EntityESRepository entityESRepository;

    @Autowired
    private EntityRelationshipRepository entityRelationshipRepository;


    @Override
    public Map<String, Object> getFullHierarchy(Integer positionId) {
        return null;
//        Map<String, Object> result = new HashMap<>();
//
//        // Position
//        Optional<Entity> positionOpt = entityRepository.findById(positionId);
//        if (!positionOpt.isPresent()) return result;
//
//        Map<String, Object> positionMap = entityToMap(positionOpt.get());
//
//        // Roles under position
//        List<PositionRoleMapping> roleMappings = positionRoleRepository.findByPositionId(positionId);
//        List<Map<String, Object>> rolesList = new ArrayList<>();
//
//        for (PositionRoleMapping prMapping : roleMappings) {
//            Integer roleId = prMapping.getRoleId();
//            Optional<Entity> roleOpt = entityRepository.findById(roleId);
//            if (!roleOpt.isPresent()) continue;
//
//            Map<String, Object> roleMap = new HashMap<>();
//            roleMap.put("role", entityToMap(roleOpt.get()));
//
//            // Activities under role
//            List<RoleActivityMapping> roleActivityMappings = roleActivityRepository.findByRoleId(roleId);
//            List<Map<String, Object>> activitiesList = new ArrayList<>();
//
//            for (RoleActivityMapping raMapping : roleActivityMappings) {
//                for (Integer activityId : raMapping.getActivityIds()) {
//                    Optional<Entity> activityOpt = entityRepository.findById(activityId);
//                    if (!activityOpt.isPresent()) continue;
//
//                    Map<String, Object> activityMap = new HashMap<>();
//                    activityMap.put("activity", entityToMap(activityOpt.get()));
//
//                    // Fetch the mapping which has competency -> list of levels
//                    Optional<ActivityCompetencyLevelMapping> compMappingOpt =
//                            activityCompetencyLevelRepository.findById("actCompLevel:" + activityId);
//
//                    List<Map<String, Object>> competenciesList = new ArrayList<>();
//
//                    if (compMappingOpt.isPresent()) {
//                        ActivityCompetencyLevelMapping compMapping = compMappingOpt.get();
//                        Map<Integer, List<Integer>> competencyLevelsMap = compMapping.getCompetencyLevelsMap();
//
//                        if (competencyLevelsMap != null) {
//                            for (Map.Entry<Integer, List<Integer>> entry : competencyLevelsMap.entrySet()) {
//                                Integer compId = entry.getKey();
//                                List<Integer> levelIds = entry.getValue();
//
//                                Optional<Entity> compOpt = entityRepository.findById(compId);
//                                if (!compOpt.isPresent()) continue;
//
//                                Map<String, Object> compMap = new HashMap<>();
//                                compMap.put("competency", entityToMap(compOpt.get()));
//
//                                List<Map<String, Object>> levelsList = new ArrayList<>();
//                                for (Integer levelId : levelIds) {
//                                    Optional<Entity> levelOpt = entityRepository.findById(levelId);
//                                    levelOpt.ifPresent(level -> levelsList.add(entityToMap(level)));
//                                }
//
//                                compMap.put("levels", levelsList);
//                                competenciesList.add(compMap);
//                            }
//                        }
//                    }
//
//                    // attach competencies under activity
//                    activityMap.put("children", competenciesList);
//                    activitiesList.add(activityMap);
//                }
//            }
//
//            // attach activities under role
//            roleMap.put("children", activitiesList);
//            rolesList.add(roleMap);
//        }
//
//        // attach roles under position
//        positionMap.put("children", rolesList);
//        result.put("position", positionMap);
//
//        return result;
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
    public List<Map<String, Object>> getDetailsForSpecifiedEntityType(String type, Integer typeId) {
//        if(type.equalsIgnoreCase("role")){
//            List<RoleActivityMapping> mappings = roleActivityRepository.findByRoleId(typeId);
//            List<Map<String, Object>> output = new ArrayList<>();
//
//            for (RoleActivityMapping mapping : mappings) {
//                for (Integer activityId : mapping.getActivityIds()) {
//                    Optional<Entity> actOpt = entityRepository.findById(activityId);
//                    if (!actOpt.isPresent()) continue;
//
//                    Map<String, Object> actMap = new HashMap<>();
//                    actMap.put("activity", actOpt.get());
//
//                    // Fetch the competency -> levels mapping
//                    Optional<ActivityCompetencyLevelMapping> compMappingOpt =
//                            activityCompetencyLevelRepository.findById("actCompLevel:" + activityId);
//
//                    if (compMappingOpt.isPresent()) {
//                        ActivityCompetencyLevelMapping compMapping = compMappingOpt.get();
//                        Map<Integer, List<Integer>> competencyLevelsMap = compMapping.getCompetencyLevelsMap();
//                        List<Map<String, Object>> competenciesList = new ArrayList<>();
//
//                        if (competencyLevelsMap != null) {
//                            for (Map.Entry<Integer, List<Integer>> entry : competencyLevelsMap.entrySet()) {
//                                Integer compId = entry.getKey();
//                                List<Integer> levelIds = entry.getValue();
//
//                                Optional<Entity> compOpt = entityRepository.findById(compId);
//                                if (!compOpt.isPresent()) continue;
//
//                                Map<String, Object> compMap = new HashMap<>();
//                                compMap.put("competency", compOpt.get());
//
//                                List<Map<String, Object>> levelsList = new ArrayList<>();
//                                for (Integer levelId : levelIds) {
//                                    Optional<Entity> levelOpt = entityRepository.findById(levelId);
//                                    levelOpt.ifPresent(level -> levelsList.add(Map.of("level", level)));
//                                }
//
//                                compMap.put("levels", levelsList);
//                                competenciesList.add(compMap);
//                            }
//                        }
//
//                        actMap.put("competencies", competenciesList);
//                    }
//
//                    output.add(actMap);
//                }
//            }
//
//            return output;
//        }
//        else if(type.equalsIgnoreCase("")){
//
//        }
      return null;
    }


    @Override
    public List<Entity> parseCsv(MultipartFile file) {
        List<Entity> entities = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVParser csvParser = new CSVParser(reader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim())) {

            for (CSVRecord record : csvParser) {
                String type = record.get("type");
                String name = record.get("name");
                String description = record.get("description");

                String generatedCode = record.isMapped("code") && !record.get("code").isBlank()
                        ? record.get("code")
                        : type.substring(0, 1).toUpperCase() + System.currentTimeMillis(); // fallback

                Entity entity = new Entity();
                entity.setType(type);
                entity.setName(name);
                entity.setDescription(description);
                entity.setCode(generatedCode);
                entity.setStatus("Active");
                entity.setLevel(generatedCode);
                entity.setLevelId(0);
                entity.setCreatedBy(record.get("created_by"));
                entity.setUpdatedBy(record.get("updated_by"));
                entity.setReviewedBy(record.get("reviewed_by"));

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if (record.isMapped("created_date") && !record.get("created_date").isBlank()) {
                    entity.setCreatedDate(df.parse(record.get("created_date")));
                }
                if (record.isMapped("updated_date") && !record.get("updated_date").isBlank()) {
                    entity.setUpdatedDate(df.parse(record.get("updated_date")));
                }
                if (record.isMapped("reviewed_date") && !record.get("reviewed_date").isBlank()) {
                    entity.setReviewedDate(df.parse(record.get("reviewed_date")));
                }

                Map<String, Object> props = new HashMap<>();
                if (record.isMapped("additional_properties") && !record.get("additional_properties").isBlank()) {
                    ObjectMapper mapper = new ObjectMapper();
                    props.putAll(mapper.readValue(record.get("additional_properties"), new TypeReference<Map<String, Object>>() {}));
                }
                entity.setAdditionalProperties(props);

                if ("competency".equalsIgnoreCase(type)) {
                    // Save parent competency first
                    Entity savedEntity = entityRepository.save(entity);

                    List<Map<String, Object>> children = new ArrayList<>();
                    List<String> levelIds = new ArrayList<>();
                    int levelCounter = 1;

                    // Assuming max 5 levels
                    for (int i = 1; i <= 5; i++) {
                        String levelNameCol = "Competency Level " + i + " Label";
                        String levelDescCol = "Competency Level " + i + " Description";
                        if (record.isMapped(levelNameCol) && !record.get(levelNameCol).isBlank()) {
                            Entity levelEntity = new Entity();
                            levelEntity.setType("level");
                            levelEntity.setName(record.get(levelNameCol));
                            levelEntity.setDescription(record.get(levelDescCol));
                            levelEntity.setStatus("Active");
                            levelEntity.setLevel("L" + levelCounter);
                            levelEntity.setLevelId(levelCounter);
                            levelEntity.setCode(savedEntity.getCode() + "_L" + levelCounter);

                            Map<String, Object> levelProps = new HashMap<>();
                            levelProps.put("parentCompetency", savedEntity.getName());
                            levelEntity.setAdditionalProperties(levelProps);

                            Entity savedLevel = entityRepository.save(levelEntity);
                            levelIds.add(String.valueOf(savedLevel.getCode()));
                            levelCounter++;

                            // Convert savedLevel to Map<String,Object> for children
                            Map<String, Object> childMap = new HashMap<>();
                            childMap.put("id", savedLevel.getId());
                            childMap.put("type", savedLevel.getType());
                            childMap.put("name", savedLevel.getName());
                            childMap.put("description", savedLevel.getDescription());
                            childMap.put("level", savedLevel.getLevel());
                            childMap.put("levelId", savedLevel.getLevelId());
                            childMap.put("code", savedLevel.getCode());
                            childMap.put("status", savedLevel.getStatus());
                            childMap.put("additionalProperties", savedLevel.getAdditionalProperties());

                            children.add(childMap);
                        }
                    }

                    // Save mapping between competency and levels
                    String id = "COMPETENCY_LEVEL" + ":" + savedEntity.getCode();

                    Optional<EntityRelationship> existingOpt = entityRelationshipRepository.findById(String.valueOf(savedEntity.getId()));

                    EntityRelationship mapping = existingOpt.orElseGet(EntityRelationship::new);
                    mapping.setId(id);
                    mapping.setType("COMPETENCY_LEVEL");
                    mapping.setParentId(String.valueOf(savedEntity.getCode()));

                    // Merge or assign childIds
                    if (levelIds != null && !levelIds.isEmpty()) {
                        List<String> newChildren = new ArrayList<>(existingOpt.map(EntityRelationship::getChildIds).orElse(List.of()));
                        newChildren.addAll(levelIds);
                        mapping.setChildIds(new ArrayList<>(newChildren.stream()
                                .distinct()
                                .collect(Collectors.toList())));
                    }

                    entityRelationshipRepository.save(mapping);

                    // Attach children for response
                    savedEntity.setChildren(children);

                    entities.add(savedEntity); // add parent competency

                } else {

                    if (entity.getCode() == null || entity.getCode().isBlank()) {
                        entity.setCode(type.substring(0, 1).toUpperCase() + System.currentTimeMillis());
                    }

                    entity.setLevel(entity.getCode());
                    entity.setLevelId(0);
                    entity.setStatus("Active");

                    entityRepository.save(entity);
                    entities.add(entity);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error parsing CSV", e);
        }

        return entities;
    }



    @Override
    public Entity createEntity(Entity entity) {
        Entity savedEntity = entityRepository.save(entity);
        entityESRepository.save(savedEntity);

        if ("competency".equalsIgnoreCase(entity.getType()) && entity.getChildren() != null && !entity.getChildren().isEmpty()) {

            List<String> levelIds = new ArrayList<>();

            int levelCounter = 1;
            for (Map<String, Object> childData : entity.getChildren()) {
                Entity childEntity = new Entity();

                // Copy provided info (if any)
                childEntity.setName((String) childData.get("name"));
                childEntity.setDescription((String) childData.get("description"));
                childEntity.setType("level"); // fixed for children

                // Generate additional fields
                childEntity.setLevel("L" + levelCounter + "_"+ savedEntity.getCode());
                childEntity.setLevelId(levelCounter);
                childEntity.setStatus("Active");
                childEntity.setSource("child_of_" + savedEntity.getCode());
                childEntity.setCreatedBy(entity.getCreatedBy());
                childEntity.setCreatedDate(new Date());
                childEntity.setCode(childEntity.getLevel());

                // Add code or other generated properties
                Map<String, Object> props = new HashMap<>();
                props.put("parentCompetency", savedEntity.getName());
                if (childData.containsKey("additionalProperties")) {
                    props.putAll((Map<String, Object>) childData.get("additionalProperties"));
                }
                childEntity.setAdditionalProperties(props);

                Entity savedChild = entityRepository.save(childEntity);
                entityESRepository.save(savedChild);
                levelIds.add(String.valueOf(savedChild.getCode()));
                levelCounter++;
            }



            String id = "COMPETENCY_LEVEL" + ":" + savedEntity.getCode();

            Optional<EntityRelationship> existingOpt = entityRelationshipRepository.findById(String.valueOf(savedEntity.getId()));

            EntityRelationship mapping = existingOpt.orElseGet(EntityRelationship::new);
            mapping.setId(id);
            mapping.setType("COMPETENCY_LEVEL");
            mapping.setParentId(String.valueOf(savedEntity.getCode()));

            // Merge or assign childIds
            if (levelIds != null && !levelIds.isEmpty()) {
                List<String> newChildren = new ArrayList<>(existingOpt.map(EntityRelationship::getChildIds).orElse(List.of()));
                newChildren.addAll(levelIds);
                mapping.setChildIds(new ArrayList<>(newChildren.stream()
                        .distinct()
                        .collect(Collectors.toList())));
            }

            entityRelationshipRepository.save(mapping);
        }
        return savedEntity;
    }

    @Override
    public void saveGenericRelationship(RelationshipRequest request) {
        if (request.getParentId() == null) {
            throw new IllegalArgumentException("parentId cannot be null");
        }

        String id = request.getType() + ":" + request.getParentId();

        Optional<EntityRelationship> existingOpt = entityRelationshipRepository.findById(request.getParentId());

        EntityRelationship mapping = existingOpt.orElseGet(EntityRelationship::new);
        mapping.setId(id);
        mapping.setType(request.getType());
        mapping.setParentId(request.getParentId());

        if (request.getChildIds() != null) {
            List<String> newChildren = new ArrayList<>(existingOpt.map(EntityRelationship::getChildIds).orElse(List.of()));
            newChildren.addAll(request.getChildIds());
            mapping.setChildIds(newChildren.stream()
                    .distinct()
                    .collect(Collectors.toList()));
        }

        if (request.getChildMap() != null) {
            Map<String, List<String>> existingMap = existingOpt.map(EntityRelationship::getChildMap).orElse(new HashMap<>());
            existingMap.putAll(request.getChildMap());
            mapping.setChildMap(existingMap);
        }

        entityRelationshipRepository.save(mapping);

    }

    @Override
    public List<Map<String, Object>> searchEntities(String type, String keyword) {
        // 1️⃣ Fetch all parent entities of the given type from ES
        List<Entity> parents = entityESRepository.findByType(type);

        if (keyword != null && !keyword.trim().isEmpty()) {
            parents = parents.stream()
                    .filter(p -> p.getName() != null &&
                            p.getName().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
        }

        List<Map<String, Object>> result = new ArrayList<>();

        for (Entity parent : parents) {
            Map<String, Object> parentMap = entityToMap(parent);

            // 2️⃣ Fetch child IDs from relationship mapping
            String mappingId = type.toUpperCase() + ":" + parent.getId();
            Optional<EntityRelationship> relationshipOpt = entityRelationshipRepository.findById(mappingId);

            if (relationshipOpt.isPresent()) {
                EntityRelationship relationship = relationshipOpt.get();
                List<String> childIds = relationship.getChildIds(); // direct children
                Map<String, List<String>> childMap = relationship.getChildMap(); // nested children

                List<Map<String, Object>> childrenList = new ArrayList<>();

                // 3️⃣ Fetch direct children
                if (childIds != null) {
                    for (String childId : childIds) {
                        entityESRepository.findById(Integer.valueOf(childId)).ifPresent(child -> childrenList.add(entityToMap(child)));
                    }
                }

                // 4️⃣ Fetch nested children from childMap (e.g., competency -> levels)
                if (childMap != null) {
                    for (Map.Entry<String, List<String>> entry : childMap.entrySet()) {
                        String childParentId = entry.getKey();
                        List<String> grandChildIds = entry.getValue();

                        entityESRepository.findById(Integer.valueOf(childParentId)).ifPresent(childParent -> {
                            Map<String, Object> childParentMap = entityToMap(childParent);

                            List<Map<String, Object>> grandChildrenList = new ArrayList<>();
                            for (String grandChildId : grandChildIds) {
                                entityESRepository.findById(Integer.valueOf(grandChildId)).ifPresent(grandChild ->
                                        grandChildrenList.add(entityToMap(grandChild))
                                );
                            }

                            if (!grandChildrenList.isEmpty()) {
                                childParentMap.put("children", grandChildrenList);
                            }

                            childrenList.add(childParentMap);
                        });
                    }
                }

                if (!childrenList.isEmpty()) {
                    parentMap.put("children", childrenList);
                }
            }

            result.add(parentMap);
        }

        return result;
    }



}
