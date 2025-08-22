package com.sunbird.entity.controller;


import com.sunbird.entity.model.dao.Entity;
import com.sunbird.entity.model.dao.EntityDao;
import com.sunbird.entity.model.requestDTO.ActivityCompetencyLevelRequest;
import com.sunbird.entity.model.requestDTO.CompetencyLevelRequest;
import com.sunbird.entity.model.requestDTO.PositionRoleRequest;
import com.sunbird.entity.model.requestDTO.RoleActivityRequest;
import com.sunbird.entity.model.requestDTO.RoleActivityRequest;
import com.sunbird.entity.repository.jpa.EntitiesRepository;
import com.sunbird.entity.repository.jpa.EntityRepository;
import com.sunbird.entity.service.EntityRelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/competency")
public class CompetencyContoller extends BaseController{

    @Autowired
    private EntitiesRepository entityRepository;

    @Autowired
    private EntityRelationshipService entityRelationshipService;

    @PostMapping("/entity")
    public ResponseEntity<Entity> createEntity(@RequestBody Entity entity) {
        Entity saved = entityRepository.save(entity);
        return ResponseEntity.ok(saved);
    }

//    @PutMapping("/entity/{id}")
//    public ResponseEntity<Entity> updateEntity(@PathVariable Integer id, @RequestBody Entity entity) {
//        Optional<Entity> existing = entityRepository.findById(id);
//        if (!existing.isPresent()) {
//            return ResponseEntity.notFound().build();
//        }
//        entity.setId(id);
//        Entity updated = entityRepository.save(entity);
//        return ResponseEntity.ok(updated);
//    }

    @PostMapping("/position-role")
    public ResponseEntity<String> linkRolesToPosition(@RequestBody PositionRoleRequest request) {
        entityRelationshipService.savePositionRoleMappings(request);
        return ResponseEntity.ok("Roles linked to position");
    }

    @PostMapping("/role-activity")
    public ResponseEntity<String> linkActivitiesToRole(@RequestBody RoleActivityRequest request) {
        entityRelationshipService.saveRoleActivityMappings(request);
        return ResponseEntity.ok("Activities linked to role");
    }

    @PostMapping("/competency-level")
    public ResponseEntity<String> linkLevelsToCompetency(@RequestBody CompetencyLevelRequest request) {
        entityRelationshipService.saveCompetencyLevelMapping(request);
        return ResponseEntity.ok("Levels linked to competency");
    }

    @PostMapping("/activity-competency-level")
    public ResponseEntity<String> linkActivityToCompetencyAndLevels(@RequestBody ActivityCompetencyLevelRequest request) {
        entityRelationshipService.saveActivityCompetencyLevelMapping(request);
        return ResponseEntity.ok("Activity linked to competency and levels");
    }

    // --- API 1: Get full hierarchy from position ---
    @GetMapping("/position/{positionId}/hierarchy")
    public ResponseEntity<Map<String, Object>> getFullHierarchy(@PathVariable Integer positionId) {
        Map<String, Object> hierarchy = entityRelationshipService.getFullHierarchy(positionId);
        return ResponseEntity.ok(hierarchy);
    }

    // --- API 2: Get competencies and child data from role ID ---
    @GetMapping("/role/{roleId}/competencies")
    public ResponseEntity<List<Map<String, Object>>> getCompetenciesByRole(@PathVariable Integer roleId) {
        List<Map<String, Object>> competencies = entityRelationshipService.getActivitiesDetailsForRole(roleId);
        return ResponseEntity.ok(competencies);
    }


    // --- API 3: Search competencies (with optional keyword) ---
    @GetMapping("/competencies")
    public ResponseEntity<List<Map<String, Object>>> getAllCompetencies(
            @RequestParam(required = false) String keyword) {
        List<Map<String, Object>> competencies = entityRelationshipService.searchCompetencies(keyword);
        return ResponseEntity.ok(competencies);
    }

}