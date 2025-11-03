package com.sunbird.entity.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunbird.entity.model.*;
import com.sunbird.entity.model.dao.Entity;
import com.sunbird.entity.model.DTO.*;
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
        entity.setStatus("Active");
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
    public List<MappingResultDTO> saveGenericRelationshipList(List<RelationshipRequest> requests) {
        List<MappingResultDTO> results = new ArrayList<>();

        for (RelationshipRequest request : requests) {
            MappingResultDTO result = new MappingResultDTO();
            result.setParentId(request.getParentId());
            result.setType(request.getType());

            try {
                // Call existing single-save method
                saveGenericRelationship(request);

                result.setStatus("SUCCESS");
                result.setMessage("Linked successfully");
            } catch (Exception e) {
                result.setStatus("FAILED");
                result.setMessage(e.getMessage());
            }

            results.add(result);
        }

        return results;
    }

    @Override
    public Entity updateEntity(Entity incoming) {
        if (incoming.getId() == null) {
            throw new IllegalArgumentException("Entity id cannot be null for update");
        }

        // Fetch existing entity from DB
        Optional<Entity> existingOpt = entityRepository.findById(incoming.getId());
        if (existingOpt.isEmpty()) {
            throw new NoSuchElementException("Entity not found with id: " + incoming.getId());
        }

        Entity existing = existingOpt.get();

        // Update only provided fields
        if (incoming.getType() != null) existing.setType(incoming.getType());
        if (incoming.getName() != null) existing.setName(incoming.getName());
        if (incoming.getDescription() != null) existing.setDescription(incoming.getDescription());
        if (incoming.getAdditionalProperties() != null)
            existing.setAdditionalProperties(incoming.getAdditionalProperties());
        if (incoming.getStatus() != null) existing.setStatus(incoming.getStatus());
        if (incoming.getSource() != null) existing.setSource(incoming.getSource());
        if (incoming.getLevel() != null) existing.setLevel(incoming.getLevel());
        existing.setLevelId(incoming.getLevelId()); // levelId is primitive, always update
        if (incoming.getCode() != null) existing.setCode(incoming.getCode());
        if (incoming.getTranslation() != null) existing.setTranslation(incoming.getTranslation());
        if (incoming.getCreatedBy() != null) existing.setCreatedBy(incoming.getCreatedBy());
        if (incoming.getUpdatedBy() != null) existing.setUpdatedBy(incoming.getUpdatedBy());
        if (incoming.getCreatedDate() != null) existing.setCreatedDate(incoming.getCreatedDate());
        if (incoming.getUpdatedDate() != null) existing.setUpdatedDate(incoming.getUpdatedDate());
        if (incoming.getReviewedBy() != null) existing.setReviewedBy(incoming.getReviewedBy());
        if (incoming.getReviewedDate() != null) existing.setReviewedDate(incoming.getReviewedDate());

        // Merge children if provided
        if (incoming.getChildren() != null && !incoming.getChildren().isEmpty()) {
            List<Map<String, Object>> mergedChildren = new ArrayList<>();
            if (existing.getChildren() != null) {
                mergedChildren.addAll(existing.getChildren());
            }
            mergedChildren.addAll(incoming.getChildren());
            // Optional: remove duplicates
            List<Map<String, Object>> distinctChildren = mergedChildren.stream()
                    .distinct()
                    .collect(Collectors.toList());
            existing.setChildren(distinctChildren);
        }

        // Save updated entity
        return entityRepository.save(existing);
    }

    @Override
    public List<Map<String, Object>> getFullHierarchy(String type, String parentCode) {

        // 1️⃣ Fetch root entity by code
        Entity root = entityESRepository.findByCode(parentCode)
                .orElseThrow(() -> new NoSuchElementException("Entity not found with code: " + parentCode));

        if (!type.equalsIgnoreCase(root.getType())) {
            throw new IllegalArgumentException("Entity type mismatch. Expected: " + type + ", Found: " + root.getType());
        }

        // 2️⃣ Convert root to map
        Map<String, Object> rootMap = entityToMap(root);

        // 3️⃣ Recursively attach children
        attachChildrenFromRelationship(rootMap, root);

        return List.of(rootMap);
    }

    /**
     * Recursive function to attach children based on relationship index.
     * Handles activity→competency→level mapping and stops cleanly at levels.
     */
    private void attachChildrenFromRelationship(Map<String, Object> parentMap, Entity parent) {

        String parentCode = parent.getCode();
        String parentType = parent.getType();

        // ✅ Determine relationship type dynamically
        String relationshipType = getRelationshipTypeForParent(parentType);

        // Construct relationship ID (e.g. POSITION_ROLE:P1)
        String mappingId = relationshipType + ":" + parentCode;

        Optional<EntityRelationship> relOpt = entityRelationshipRepository.findById(mappingId);

        List<Map<String, Object>> childrenList = new ArrayList<>();

        // 1️⃣ If relationship exists — process it
        if (relOpt.isPresent()) {
            EntityRelationship relationship = relOpt.get();

            // ---- Direct children ----
            if (relationship.getChildIds() != null) {
                for (String childCode : relationship.getChildIds()) {
                    entityESRepository.findByCode(childCode).ifPresent(child -> {
                        Map<String, Object> childMap = entityToMap(child);
                        attachChildrenFromRelationship(childMap, child); // recursion
                        childrenList.add(childMap);
                    });
                }
            }

            // ---- Nested children from childMap ----
            if (relationship.getChildMap() != null) {
                for (Map.Entry<String, List<String>> entry : relationship.getChildMap().entrySet()) {
                    String childParentCode = entry.getKey();
                    List<String> grandChildCodes = entry.getValue();

                    entityESRepository.findByCode(childParentCode).ifPresent(childParent -> {
                        Map<String, Object> childParentMap = entityToMap(childParent);

                        List<Map<String, Object>> grandChildrenList = new ArrayList<>();
                        for (String grandChildCode : grandChildCodes) {
                            entityESRepository.findByCode(grandChildCode).ifPresent(grandChild -> {
                                Map<String, Object> grandChildMap = entityToMap(grandChild);
                                attachChildrenFromRelationship(grandChildMap, grandChild); // recursion
                                grandChildrenList.add(grandChildMap);
                            });
                        }

                        if (!grandChildrenList.isEmpty()) {
                            childParentMap.put("children", grandChildrenList);
                        }

                        childrenList.add(childParentMap);
                    });
                }
            }
        }
        // 2️⃣ If no relationship found — throw error for invalid hierarchy (safety)
        else {
            // If there’s truly no mapping for this type, fail gracefully for invalid parent
            if (!"competency".equalsIgnoreCase(parentType) && !"level".equalsIgnoreCase(parentType)) {
                throw new IllegalArgumentException("No relationship mapping found for parent type: " + parentType + " and code: " + parentCode);
            }
        }

        // 3️⃣ Competency → attach levels directly only if NOT already linked via relationship
        if ("competency".equalsIgnoreCase(parentType)) {
            boolean alreadyHasLevels = childrenList.stream()
                    .anyMatch(child -> "level".equalsIgnoreCase((String) child.get("type")));

            if (!alreadyHasLevels) {
                String competencyCode = parent.getCode();

                List<Entity> levels = entityESRepository.findByType("level").stream()
                        .filter(level -> level.getLevel() != null && level.getLevel().endsWith("_" + competencyCode))
                        .filter(level -> "Active".equalsIgnoreCase(level.getStatus()))
                        .collect(Collectors.toList());

                if (!levels.isEmpty()) {
                    List<Map<String, Object>> levelChildren = levels.stream()
                            .map(this::entityToMap)
                            .collect(Collectors.toList());
                    childrenList.addAll(levelChildren);
                }
            }
        }

        // 4️⃣ Final step — attach list to parent map
        if (!childrenList.isEmpty()) {
            parentMap.put("children", childrenList);
        }
    }


    /**
     * Maps parent type to its relationship type in the relationship index.
     * Returns null for terminal types (like level).
     */
    private String getRelationshipTypeForParent(String parentType) {
        if (parentType == null) {
            throw new IllegalArgumentException("Parent type cannot be null");
        }

        switch (parentType.toLowerCase()) {
            case "position":
                return "POSITION_ROLE";
            case "role":
                return "ROLE_ACTIVITY";
            case "activity":
                return "ACTIVITY_COMPETENCY_LEVEL";
            case "competency":
                return "COMPETENCY_LEVEL";
            case "level":
                return null; // ✅ stop recursion at level
            default:
                throw new IllegalArgumentException("Unknown parent type: " + parentType);
        }
    }


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
            mapping.setChildIds(newChildren.stream().distinct().collect(Collectors.toList()));
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

        // 1️⃣ Fetch all parents by type
        List<Entity> parents = entityESRepository.findByType(type);

        if (keyword != null && !keyword.trim().isEmpty()) {
            String lowerKeyword = keyword.toLowerCase();

            parents = parents.stream()
                    .filter(p -> {
                        boolean matches = false;

                        // Check name, description, code
                        if (p.getName() != null && p.getName().toLowerCase().contains(lowerKeyword)) matches = true;
                        else if (p.getDescription() != null && p.getDescription().toLowerCase().contains(lowerKeyword)) matches = true;
                        else if (p.getCode() != null && p.getCode().toLowerCase().contains(lowerKeyword)) matches = true;

                        // Check additionalProperties values
                        if (!matches && p.getAdditionalProperties() != null) {
                            matches = p.getAdditionalProperties().values().stream()
                                    .anyMatch(val -> val != null && val.toString().toLowerCase().contains(lowerKeyword));
                        }

                        return matches;
                    })
                    .collect(Collectors.toList());
        }

        // 2️⃣ Filter for default active
        parents = parents.stream()
                .filter(p -> "Active".equalsIgnoreCase(p.getStatus()))
                .collect(Collectors.toList());

        List<Map<String, Object>> result = new ArrayList<>();

        for (Entity parent : parents) {
            Map<String, Object> parentMap = entityToMap(parent);

            if ("competency".equalsIgnoreCase(parent.getType())) {
                String competencyCode = parent.getCode();

                List<Entity> levels = entityESRepository.findByType("level").stream()
                        .filter(level -> level.getLevel() != null &&
                                level.getLevel().endsWith("_" + competencyCode))
                        .filter(level -> "Active".equalsIgnoreCase(level.getStatus()))
                        .collect(Collectors.toList());

                if (!levels.isEmpty()) {
                    List<Map<String, Object>> children = levels.stream()
                            .map(this::entityToMap)
                            .collect(Collectors.toList());
                    parentMap.put("children", children);
                }
            }

            result.add(parentMap);
        }

        return result;
    }




}
