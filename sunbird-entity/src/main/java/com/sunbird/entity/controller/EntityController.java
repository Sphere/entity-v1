package com.sunbird.entity.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunbird.entity.model.dao.Entity;
import com.sunbird.entity.model.DTO.*;
import com.sunbird.entity.repository.jpa.EntitiesRepository;
import com.sunbird.entity.service.EntityRelationshipService;
import com.sunbird.entity.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/frac/entity/")
public class EntityController extends BaseController{

    @Autowired
    private EntitiesRepository entityRepository;

    @Autowired
    private EntityRelationshipService entityRelationshipService;

    @PostMapping("/create")
    public ResponseEntity<ResponseDTO<EntityDataDTO>> createEntity(
            @RequestBody RequestDTO<FracDTO<EntityDataDTO>> requestDTO) {

        try {
            EntityDataDTO dto = requestDTO.getRequest().getFrac();
            Entity entity = new ObjectMapper().convertValue(dto, Entity.class);

            Entity savedEntity = entityRelationshipService.createEntity(entity);

            EntityDataDTO responseData = new ObjectMapper().convertValue(savedEntity, EntityDataDTO.class);

            ResponseDTO<EntityDataDTO> response = ResponseUtil.successResponse(responseData, "api.entity.create");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ResponseDTO<EntityDataDTO> errorResponse =
                    ResponseUtil.errorResponse("api.entity.create", "ENTITY_CREATION_FAILED", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }


    @PostMapping("/update")
    public ResponseEntity<ResponseDTO<EntityDataDTO>> updateEntity(
            @RequestBody RequestDTO<FracDTO<EntityDataDTO>> requestDTO) {

        try {
            EntityDataDTO dto = requestDTO.getRequest().getFrac();

            if (dto.getId() == null) {
                return ResponseEntity.badRequest().body(
                        ResponseUtil.errorResponse(
                                "api.entity.update",
                                "ENTITY_ID_MISSING",
                                "Entity id is required for update"
                        )
                );
            }

            Entity entity = new ObjectMapper().convertValue(dto, Entity.class);

            Entity updatedEntity = entityRelationshipService.updateEntity(entity);

            EntityDataDTO responseData = new ObjectMapper().convertValue(updatedEntity, EntityDataDTO.class);

            ResponseDTO<EntityDataDTO> response =
                    ResponseUtil.successResponse(responseData, "api.entity.update");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ResponseDTO<EntityDataDTO> errorResponse =
                    ResponseUtil.errorResponse("api.entity.update", "ENTITY_UPDATE_FAILED", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    @PostMapping("/hierarchy")
    public ResponseEntity<ResponseDTO<List<Map<String, Object>>>> getHierarchyByType(
            @RequestBody RequestDTO<FracDTO<HierarchyRequestDTO>> requestDTO) {

        try {
            HierarchyRequestDTO dto = requestDTO.getRequest().getFrac();

            if (dto.getType() == null || dto.getCode() == null) {
                return ResponseEntity.badRequest().body(
                        ResponseUtil.errorResponse(
                                "api.entity.hierarchy",
                                "INVALID_REQUEST",
                                "Both type and typeId are required"
                        )
                );
            }

            List<Map<String, Object>> hierarchy = entityRelationshipService.getFullHierarchy(dto.getType(), dto.getCode());

            ResponseDTO<List<Map<String, Object>>> response =
                    ResponseUtil.successResponse(hierarchy, "api.entity.hierarchy");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ResponseDTO<List<Map<String, Object>>> errorResponse =
                    ResponseUtil.errorResponse(
                            "api.entity.hierarchy",
                            "HIERARCHY_FETCH_FAILED",
                            e.getMessage()
                    );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }



    @PostMapping("/search")
    public ResponseEntity<ResponseDTO<List<Map<String, Object>>>> getEntities(
            @RequestBody RequestDTO<FracDTO<EntitySearchRequestDTO>> requestDTO) {

        try {
            EntitySearchRequestDTO request = requestDTO.getRequest().getFrac(); // direct object

            if (request.getType() == null || request.getType().isEmpty()) {
                throw new IllegalArgumentException("Type cannot be null or empty");
            }

            List<Map<String, Object>> entities = entityRelationshipService.searchEntities(
                    request.getType(),
                    request.getKeyword()
            );

            ResponseDTO<List<Map<String, Object>>> response =
                    ResponseUtil.successResponse(entities, "api.entity.search");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ResponseDTO<List<Map<String, Object>>> errorResponse =
                    ResponseUtil.errorResponse(
                            "api.entity.search",
                            "SEARCH_FAILED",
                            e.getMessage()
                    );

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }


    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO<FracDTO<List<EntityDataDTO>>>> upload(@RequestParam("file") MultipartFile multipartFile) {

        if (multipartFile == null || multipartFile.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ResponseUtil.errorResponse(
                            "api.entity.upload",
                            "FILE_MISSING",
                            "File is empty or missing"
                    )
            );
        }

        List<Entity> entities = entityRelationshipService.parseCsv(multipartFile);
        List<Entity> savedEntities = (List<Entity>) entityRepository.saveAll(entities);

        List<EntityDataDTO> responseData = savedEntities.stream()
                .map(entity -> new ObjectMapper().convertValue(entity, EntityDataDTO.class))
                .collect(Collectors.toList());

        FracDTO<List<EntityDataDTO>> fracResponse = new FracDTO<>();
        fracResponse.setFrac(responseData);
        return ResponseEntity.ok(
                ResponseUtil.successResponse(fracResponse, "api.entity.upload")
        );
    }


    @PostMapping("/mapping")
    public ResponseEntity<ResponseDTO<List<MappingResultDTO>>> linkEntities(
            @RequestBody RequestDTO<FracDTO<List<RelationshipRequest>>> requests) {

        if (requests == null || requests.getRequest().getFrac().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ResponseUtil.errorResponse(
                            "api.entity.mapping",
                            "INVALID_REQUEST",
                            "Request list cannot be empty"
                    )
            );
        }

        List<MappingResultDTO> resultList = entityRelationshipService.saveGenericRelationshipList(requests.getRequest().getFrac());

        ResponseDTO<List<MappingResultDTO>> response = ResponseUtil.successResponse(resultList, "api.entity.mapping");

        return ResponseEntity.ok(response);
    }

}