package com.sunbird.entity.controller;


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
@RequestMapping("/v1/entity")
public class EntityController extends BaseController{

    @Autowired
    private EntitiesRepository entityRepository;

    @Autowired
    private EntityRelationshipService entityRelationshipService;

    @PostMapping("/create")
    public ResponseEntity<ResponseDTO<EntityDTO<EntityDataDTO>>> createEntity(
            @RequestBody RequestDTO<EntityDTO<EntityDataDTO>> requestDTO) {

        try {
            EntityDataDTO dto = requestDTO.getRequest().getEntity();
            Entity entity = new ObjectMapper().convertValue(dto, Entity.class);

            Entity savedEntity = entityRelationshipService.createEntity(entity);

            EntityDataDTO responseData = new ObjectMapper().convertValue(savedEntity, EntityDataDTO.class);

            EntityDTO<EntityDataDTO> entityDTO = new EntityDTO<>();
            entityDTO.setEntity(responseData);

            ResponseDTO<EntityDTO<EntityDataDTO>> response =
                    ResponseUtil.successResponse(entityDTO, "api.entity.create");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ResponseDTO<EntityDTO<EntityDataDTO>> errorResponse =
                    ResponseUtil.errorResponse("api.entity.create", "ENTITY_CREATION_FAILED", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }



    @PostMapping("/update")
    public ResponseEntity<ResponseDTO<EntityDTO<EntityDataDTO>>> updateEntity(
            @RequestBody RequestDTO<EntityDTO<EntityDataDTO>> requestDTO) {

        try {
            EntityDataDTO dto = requestDTO.getRequest().getEntity();

            if (dto.getId() == null) {
                ResponseDTO<EntityDTO<EntityDataDTO>> errorResponse =
                        ResponseUtil.errorResponse(
                                "api.entity.update",
                                "ENTITY_ID_MISSING",
                                "Entity id is required for update"
                        );
                return ResponseEntity.badRequest().body(errorResponse);
            }

            Entity entity = new ObjectMapper().convertValue(dto, Entity.class);

            Entity updatedEntity = entityRelationshipService.updateEntity(entity);

            EntityDataDTO responseData = new ObjectMapper().convertValue(updatedEntity, EntityDataDTO.class);

            EntityDTO<EntityDataDTO> entityDTO = new EntityDTO<>();
            entityDTO.setEntity(responseData);

            ResponseDTO<EntityDTO<EntityDataDTO>> response =
                    ResponseUtil.successResponse(entityDTO, "api.entity.update");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ResponseDTO<EntityDTO<EntityDataDTO>> errorResponse =
                    ResponseUtil.errorResponse("api.entity.update", "ENTITY_UPDATE_FAILED", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }



    @PostMapping("/hierarchy")
    public ResponseEntity<ResponseDTO<EntityDTO<List<Map<String, Object>>>>> getHierarchyByType(
            @RequestBody RequestDTO<EntityDTO<HierarchyRequestDTO>> requestDTO) {

        try {
            HierarchyRequestDTO dto = requestDTO.getRequest().getEntity();

            if (dto.getType() == null || dto.getCode() == null) {
                ResponseDTO<EntityDTO<List<Map<String, Object>>>> errorResponse =
                        ResponseUtil.errorResponse(
                                "api.entity.hierarchy",
                                "INVALID_REQUEST",
                                "Both type and code are required"
                        );
                return ResponseEntity.badRequest().body(errorResponse);
            }

            List<Map<String, Object>> hierarchy =
                    entityRelationshipService.getFullHierarchy(dto.getType(), dto.getCode());

            EntityDTO<List<Map<String, Object>>> entityDTO = new EntityDTO<>();
            entityDTO.setEntity(hierarchy);

            ResponseDTO<EntityDTO<List<Map<String, Object>>>> response =
                    ResponseUtil.successResponse(entityDTO, "api.entity.hierarchy");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ResponseDTO<EntityDTO<List<Map<String, Object>>>> errorResponse =
                    ResponseUtil.errorResponse(
                            "api.entity.hierarchy",
                            "HIERARCHY_FETCH_FAILED",
                            e.getMessage()
                    );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }



    @PostMapping("/search")
    public ResponseEntity<ResponseDTO<EntityDTO<List<Map<String, Object>>>>> getEntities(
            @RequestBody RequestDTO<EntityDTO<EntitySearchRequestDTO>> requestDTO) {

        try {
            EntitySearchRequestDTO request = requestDTO.getRequest().getEntity(); // direct object

            if (request.getType() == null || request.getType().isEmpty()) {
                throw new IllegalArgumentException("Type cannot be null or empty");
            }

            List<Map<String, Object>> entities = entityRelationshipService.searchEntities(
                    request.getType(),
                    request.getQuery(),
                    request.getLimit()
            );

            EntityDTO<List<Map<String, Object>>> entityDTO = new EntityDTO<>();
            entityDTO.setEntity(entities);

            ResponseDTO.Result<EntityDTO<List<Map<String, Object>>>> result = new ResponseDTO.Result<>();
            result.setData(entityDTO);
            result.setCount(entities != null ? entities.size() : 0);

            ResponseDTO<EntityDTO<List<Map<String, Object>>>> response = new ResponseDTO<>();
            response.setResult(result);

            ResponseUtil.successResponse(response, "api.entity.search");

            return ResponseEntity.ok(response);


        } catch (Exception e) {
            ResponseDTO<EntityDTO<List<Map<String, Object>>>> errorResponse =
                    ResponseUtil.errorResponse(
                            "api.entity.search",
                            "SEARCH_FAILED",
                            e.getMessage()
                    );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }


    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO<EntityDTO<List<EntityDataDTO>>>> upload(@RequestParam("file") MultipartFile multipartFile) {

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

        EntityDTO<List<EntityDataDTO>> entityResponse = new EntityDTO<>();
        entityResponse.setEntity(responseData);

        ResponseDTO<EntityDTO<List<EntityDataDTO>>> response =
                ResponseUtil.successResponse(entityResponse, "api.entity.upload");

        if (response.getResult() != null && response.getResult().getData() != null) {
            response.getResult().setCount(responseData.size());
        }

        return ResponseEntity.ok(response);

    }


    @PostMapping("/mapping")
    public ResponseEntity<ResponseDTO<EntityDTO<List<MappingResultDTO>>>> linkEntities(
            @RequestBody RequestDTO<EntityDTO<List<RelationshipRequest>>> requests) {

        if (requests == null || requests.getRequest().getEntity().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ResponseUtil.errorResponse(
                            "api.entity.mapping",
                            "INVALID_REQUEST",
                            "Request list cannot be empty"
                    )
            );
        }

        List<MappingResultDTO> resultList = entityRelationshipService.saveGenericRelationshipList(requests.getRequest().getEntity());

        EntityDTO<List<MappingResultDTO>> entityDTO = new EntityDTO<>();
        entityDTO.setEntity(resultList);

        ResponseDTO<EntityDTO<List<MappingResultDTO>>> response =
                ResponseUtil.successResponse(entityDTO, "api.entity.mapping");

        return ResponseEntity.ok(response);
    }

}