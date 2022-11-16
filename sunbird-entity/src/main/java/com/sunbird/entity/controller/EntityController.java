package com.sunbird.entity.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sunbird.entity.model.Bookmark;
import com.sunbird.entity.model.EntityRelation;
import com.sunbird.entity.model.EntityVerification;
import com.sunbird.entity.model.SearchObject;
import com.sunbird.entity.model.UserProfile;
import com.sunbird.entity.model.dao.EntityDao;
import com.sunbird.entity.service.EntityService;
import com.sunbird.entity.util.Constants;
import com.sunbird.entity.util.PathRoutes;
import com.sunbird.entity.util.ResponseCode;
import com.sunbird.entity.util.Storageutil;

@RestController
public class EntityController extends BaseController {

	@Autowired
	EntityService entityService;

	@PostMapping(value = PathRoutes.Endpoints.ADD_UPDATE_ENTITY)
	public String addUpdateEntity(@RequestBody EntityDao entityDao,
			@RequestAttribute(Constants.Parameters.USER_ID) String userId) throws JsonProcessingException {
		EntityDao entity = entityService.addUpdateEntity(entityDao, userId);
		return handleResponse(entity, ResponseCode.CREATE_FAILED);
	}

	@PostMapping(value = PathRoutes.Endpoints.GET_ENTITY_BY_ID)
	public String getEntityById(@PathVariable Integer id, @RequestBody SearchObject searchObject,
			@RequestAttribute(Constants.Parameters.USER_ID) String userId) throws JsonProcessingException {
		EntityDao entityNode = entityService.getEntityById(id, searchObject);
		return handleResponse(entityNode, ResponseCode.GET_FAILED);
	}

	@PostMapping(value = PathRoutes.Endpoints.ADD_ENTITY_RELATION)
	public String addEntityRelation(@RequestBody EntityRelation entityRelation,
			@RequestAttribute(Constants.Parameters.USER_ID) String userId) throws JsonProcessingException {
		Boolean response = entityService.addEntityRelation(entityRelation);
		return handleResponse(response, ResponseCode.MAPPING_FAILED);
	}

	@PostMapping(value = PathRoutes.Endpoints.BOOKMARK_DATA_NODE)
	public String bookmarkEntity(@RequestBody Bookmark bookmarkEntityNode,
			@RequestAttribute(Constants.Parameters.USER_ID) String userId) throws JsonProcessingException {
		Boolean isBookmarked = entityService.bookmarkEntity(bookmarkEntityNode, "userId");
		return handleResponse(isBookmarked, ResponseCode.MAPPING_FAILED);

	}

	@PostMapping(value = PathRoutes.Endpoints.ADD_ENTITIES)
	public String addEntities(@RequestBody List<EntityDao> entityList,
			@RequestAttribute(Constants.Parameters.USER_ID) String userId) throws JsonProcessingException {
		entityService.addEntities(entityList);
		return handleResponse(Boolean.TRUE, ResponseCode.CREATE_FAILED);
	}

	@PostMapping(value = PathRoutes.Endpoints.GET_ALL_ENTITY, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getAllDataNodes(@RequestBody SearchObject searchObject,
			@RequestAttribute(Constants.Parameters.USER_ID) String userId) throws JsonProcessingException {
		List<EntityDao> entityDaos = entityService.getAllDataNodes(searchObject);
		return handleResponse(entityDaos, ResponseCode.GET_FAILED);

	}

	@DeleteMapping(value = PathRoutes.Endpoints.DELETE_ENTITY, produces = MediaType.APPLICATION_JSON_VALUE)
	public String deleteNode(@RequestParam(value = "id", required = true) Integer id,
			@RequestAttribute(Constants.Parameters.USER_ID) String userId) throws IOException {
		Boolean deleted = entityService.deleteEntity(id);
		return handleResponse(deleted, ResponseCode.CREATE_FAILED);

	}

	@PostMapping(value = PathRoutes.Endpoints.UPLOAD_FILE)
	public String upload(@RequestParam(value = "file", required = true) MultipartFile multipartFile,
			@RequestAttribute(Constants.Parameters.USER_ID) String userId) throws IOException {
		Boolean uploadResponse = Storageutil.uploadFile(multipartFile);
		return handleResponse(uploadResponse, ResponseCode.UPLOAD_FAILED);
	}

	@PostMapping(value = PathRoutes.Endpoints.NODE_FEEDBACK)
	public String entityFeedback(@RequestBody Map<String, Object> feedbackDocument) throws IOException {
		Boolean feedbackResponse = entityService.addFeedback(feedbackDocument);
		return null;
	}

	@DeleteMapping(value = PathRoutes.Endpoints.DELETE_FILE)
	public String deleteCloudFile(@RequestParam(value = "fileName", required = true) String fileName,
			@RequestAttribute(Constants.Parameters.USER_ID) String userId) throws JsonProcessingException {
		Boolean deleteResponse = Storageutil.deleteFile(fileName);
		return handleResponse(deleteResponse, ResponseCode.DELETE_FAILED);
	}

	@PostMapping(value = PathRoutes.Endpoints.REVIEW_ENTITY)
	public String reviewEntity(@RequestBody EntityVerification entityVerification,
			@RequestHeader(Constants.Parameters.X_USER_TOKEN) String authToken,
			@RequestAttribute(Constants.Parameters.USER_ID) String userId) throws IOException {
		UserProfile userProfile = getUserProfile(userId, authToken);
		Boolean response = entityService.reviewEntity(entityVerification, userProfile);
		return handleResponse(response, ResponseCode.FAILED);
	}
}