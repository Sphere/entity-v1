package com.sunbird.entity.controller;


import com.sunbird.entity.model.dao.Entity;
import com.sunbird.entity.model.requestDTO.*;
import com.sunbird.entity.model.requestDTO.RoleActivityRequest;
import com.sunbird.entity.repository.jpa.EntitiesRepository;
import com.sunbird.entity.service.EntityRelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/")
public class EntityController extends BaseController{

    @Autowired
    private EntitiesRepository entityRepository;

    @Autowired
    private EntityRelationshipService entityRelationshipService;

    @PostMapping("/entity")
    public ResponseEntity<Entity> createEntity(@RequestBody Entity entity) {
        Entity saved = entityRelationshipService.createEntity(entity);

        return ResponseEntity.ok(saved);
    }

    // --- API 1: Get full hierarchy from position ---
    @GetMapping("/position/{positionId}/hierarchy")
    public ResponseEntity<Map<String, Object>> getFullHierarchy(@PathVariable Integer positionId) {
        Map<String, Object> hierarchy = entityRelationshipService.getFullHierarchy(positionId);
        return ResponseEntity.ok(hierarchy);
    }

    @GetMapping("/listByType")
    public ResponseEntity<List<Map<String, Object>>> getDetailsByType(@RequestParam String type,@RequestParam Integer typeId) {
        List<Map<String, Object>> competencies = entityRelationshipService.getDetailsForSpecifiedEntityType(type, typeId);
        return ResponseEntity.ok(competencies);
    }


    @GetMapping("/entities")
    public ResponseEntity<List<Map<String, Object>>> getEntities(
            @RequestParam String type,
            @RequestParam(required = false) String keyword) {

        List<Map<String, Object>> entities = entityRelationshipService.searchEntities(type, keyword);
        return ResponseEntity.ok(entities);
    }


    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty or missing");
        }

        try {
            List<Entity> entities = entityRelationshipService.parseCsv(multipartFile);

            List<Entity> savedEntities = (List<Entity>) entityRepository.saveAll(entities);

            return ResponseEntity.ok(savedEntities);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body("Error while processing file: " + e.getMessage());
        }
    }

    @PostMapping("/link-entities")
    public ResponseEntity<String> linkEntities(@RequestBody RelationshipRequest request) {
        entityRelationshipService.saveGenericRelationship(request);
        return ResponseEntity.ok("Relationship saved successfully for type: " + request.getType());
    }



}