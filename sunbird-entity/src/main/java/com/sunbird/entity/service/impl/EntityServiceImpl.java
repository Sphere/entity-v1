package com.sunbird.entity.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.htrace.fasterxml.jackson.core.type.TypeReference;
import org.apache.htrace.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sunbird.entity.ConfigurationPanel;
import com.sunbird.entity.model.Bookmark;
import com.sunbird.entity.model.EntityRelation;
import com.sunbird.entity.model.EntityVerification;
import com.sunbird.entity.model.QueryDao;
import com.sunbird.entity.model.SearchObject;
import com.sunbird.entity.model.UserProfile;
import com.sunbird.entity.model.WfRequest;
import com.sunbird.entity.model.dao.BookmarkDao;
import com.sunbird.entity.model.dao.ChildEntityDao;
import com.sunbird.entity.model.dao.EntityDao;
import com.sunbird.entity.model.dao.ParentEntityDao;
import com.sunbird.entity.repository.BookmarkRepository;
import com.sunbird.entity.repository.ChildEntityRepository;
import com.sunbird.entity.repository.EntityRepository;
import com.sunbird.entity.repository.ParentEntityRepository;
import com.sunbird.entity.service.AuditService;
import com.sunbird.entity.service.EntityService;
import com.sunbird.entity.service.WorkflowService;
import com.sunbird.entity.util.Constants;
import com.sunbird.entity.util.DateUtils;
import com.sunbird.entity.util.QueryUtils;
import com.sunbird.entity.util.ServerProperties;

@Service
public class EntityServiceImpl implements EntityService {

	public static final Logger LOGGER = LoggerFactory.getLogger(EntityServiceImpl.class);

	public static ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	EntityRepository entityRepo;

	@Autowired
	ParentEntityRepository parentEntityRepo;

	@Autowired
	ChildEntityRepository childEntityRepo;

	@Autowired
	BookmarkRepository bookmarkRepo;

	@Autowired
	ServerProperties serverProperties;

	@Autowired
	WorkflowService workflowService;

	@Autowired
	AuditService auditService;

	@Override
	public EntityDao addUpdateEntity(EntityDao entityDao, String userId) {
		try {
			EntityDao oldEntity = null;
			setEntityDefaultData(entityDao);
			if (entityDao.getId() != null) {
				Optional<EntityDao> entityObj = entityRepo.findById(entityDao.getId());
				if (entityObj.isPresent()) {
					oldEntity = entityObj.get();
					// set existing data
					entityDao.setCreatedBy(oldEntity.getCreatedBy());
					entityDao.setCreatedDate(oldEntity.getCreatedDate());
				}
				entityDao.setUpdatedBy(userId);
				entityDao.setUpdatedDate(DateUtils.getCurrentDateTimeInUTC());
			} else {
				entityDao.setCreatedBy(userId);
				entityDao.setCreatedDate(DateUtils.getCurrentDateTimeInUTC());
			}

			entityRepo.save(entityDao);
			saveChildEntity(entityDao);
			auditService.addEntityAudit(oldEntity, entityDao);
			return entityDao;
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(String.format(Constants.Exception.EXCEPTION_METHOD, "addUpdateEntity", e.getMessage()));
		}
		return null;

	}

	/**
	 * Sets default value for the entity
	 * 
	 * @param entityDao
	 *            EntityDao
	 */
	private void setEntityDefaultData(EntityDao entityDao) {
		if (entityDao.getIsActive() == null) {
			entityDao.setIsActive(Boolean.TRUE);
		}
		// set Entity status
		entityDao.setStatus(
				entityDao.getStatus() != null && entityDao.getStatus().equalsIgnoreCase(Constants.WorkflowState.DRAFT)
						? Constants.WorkflowState.DRAFT
						: Constants.WorkflowState.UNVERIFIED);
		// wf level
		entityDao.setLevel(Constants.WorkflowState.INITIATE);
	}

	@Override
	public EntityDao getEntityById(Integer id, SearchObject searchObject) {
		try {
			Optional<EntityDao> result = entityRepo.findById(id);
			if (result.isPresent()) {
				// filter the response set
				if (searchObject.getFilter() != null) {
					for (Map.Entry<String, Object> entry : searchObject.getFilter().entrySet()) {
						// append child Nodes
						if (entry.getKey().equals(Constants.Parameters.IS_DETAIL)
								&& entry.getValue().equals(Boolean.TRUE)) {
							appendChildEntity(result.get());
						}
					}
				}

				return result.get();
			}

		} catch (Exception e) {
			LOGGER.error(String.format(Constants.Exception.EXCEPTION_METHOD, "getEntityById", e.getMessage()));
		}
		return null;
	}

	@Override
	public Boolean addEntityRelation(EntityRelation entityRelation) {
		try {
			// get the mapping id
			Map<String, Object> search = new HashMap<>();
			search.put(Constants.Parameters.PARENT_ID, entityRelation.getParentId());
			search.put(Constants.Parameters.CHILD, entityRelation.getChild());
			entityRelation.setId(getEntityMappingId(search));
			if (entityRelation.getId() == null) {
				ParentEntityDao parentNode = new ParentEntityDao(0, entityRelation.getParent(),
						entityRelation.getParentId(), entityRelation.getChild(), null, null);

				parentNode = parentEntityRepo.save(parentNode);
				entityRelation.setId(parentNode.getId());
			} else {
				// delete all child entry
				childEntityRepo.deleteByMapId(entityRelation.getId());
			}
			// add all child nodes
			List<ChildEntityDao> childNodes = new ArrayList<>();
			entityRelation.getChildIds().forEach((childId) -> {
				ChildEntityDao childNode = new ChildEntityDao(0, entityRelation.getId(), childId);
				childNodes.add(childNode);
			});
			childEntityRepo.saveAll(childNodes);
			// store relations in cache
			ConfigurationPanel.setChildId(entityRelation.getParentId(), entityRelation.getChildIds());

			return Boolean.TRUE;

		} catch (Exception e) {
			LOGGER.error(String.format(Constants.Exception.EXCEPTION_METHOD, "addEntityRelation", e.getMessage()));
		}
		return Boolean.FALSE;
	}

	private Integer getEntityMappingId(Map<String, Object> search) {
		QueryDao queryDao = QueryUtils.queryBuilder(QueryUtils.Table.PARENT_NODE, search, Boolean.FALSE,
				QueryUtils.Clauses.AND);
		ParentEntityDao parentEntity = parentEntityRepo.customFindOne(queryDao.getQuery(), queryDao.getParams());
		return parentEntity != null ? parentEntity.getId() : null;
	}

	@Override
	public Boolean bookmarkEntity(Bookmark bookmarkEntityNode, String userId) {
		try {
			BookmarkDao bookmarkDao = new BookmarkDao();
			bookmarkDao.setNodeId(bookmarkEntityNode.getId());
			bookmarkDao.setUserId(userId);
			if (bookmarkEntityNode.getBookmark() == null || bookmarkEntityNode.getBookmark()) {
				bookmarkRepo.save(bookmarkDao);
			} else {
				bookmarkRepo.delete(bookmarkDao);
			}
			return true;
		} catch (Exception e) {
			LOGGER.error(String.format(Constants.Exception.EXCEPTION_METHOD, "bookmarkEntity", e.getMessage()));
			return false;
		}
	}

	@Override
	public void addEntities(List<EntityDao> entityList) {
		try {
			new Thread(() -> {
				if (entityList != null) {
					// save all parent entity
					List<EntityDao> entities = (List<EntityDao>) entityRepo.saveAll(entityList);

					// save children
					entities.forEach((entityObj) -> {
						saveChildEntity(entityObj);
					});
				}

			}).start();

		} catch (Exception e) {
			LOGGER.error(String.format(Constants.Exception.EXCEPTION_METHOD, "addEntities", e.getMessage()));
		}
	}

	/**
	 * To save the children entities and its relation
	 * 
	 * @param parentEntity
	 *            EntityDao - parent entity
	 * @return Boolean
	 */
	private Boolean saveChildEntity(EntityDao parentEntity) {
		try {
			if (parentEntity.getChildren() != null && parentEntity.getChildren().size() > 0) {
				// save children entity
				List<EntityDao> children = (List<EntityDao>) entityRepo.saveAll(
						objectMapper.convertValue(parentEntity.getChildren(), new TypeReference<List<EntityDao>>() {
						}));
				parentEntity.setChildren(
						objectMapper.convertValue(children, new TypeReference<List<Map<String, Object>>>() {
						}));

				// entity relation map
				Map<String, List<Integer>> childMap = new HashMap<>();
				children.forEach((child) -> {
					List<Integer> childId = new ArrayList<>();
					if (childMap.containsKey(child.getType())) {
						childId = childMap.get(child.getType());
					}
					childId.add(child.getId());
					childMap.put(child.getType(), childId);
				});
				// Save entity relation map
				for (Map.Entry<String, List<Integer>> entry : childMap.entrySet()) {
					EntityRelation entityRelation = new EntityRelation(0, parentEntity.getType(), parentEntity.getId(),
							entry.getKey(), entry.getValue(), null);
					addEntityRelation(entityRelation);
				}
			}
			return Boolean.TRUE;
		} catch (Exception e) {
			LOGGER.error(String.format(Constants.Exception.EXCEPTION_METHOD, "saveChildEntity", e.getMessage()));
			return Boolean.FALSE;
		}
	}

	@Override
	public List<EntityDao> getAllDataNodes(SearchObject searchObject) {
		try {
			QueryDao queryDao = QueryUtils.queryBuilder(QueryUtils.Table.DATA_NODE, searchObject.getSearch(),
					searchObject.getKeywordSearch(), QueryUtils.Clauses.AND);
			List<EntityDao> result = entityRepo.customFindAll(queryDao.getQuery(), queryDao.getParams());

			return result;
		} catch (Exception e) {
			LOGGER.error(String.format(Constants.Exception.EXCEPTION_METHOD, "getAllDataNodes", e.getMessage()));
			return null;
		}
	}

	/**
	 * Binds children entity with parent entity
	 * 
	 * @param result
	 *            EntityDao - parent entity
	 */
	private void appendChildEntity(EntityDao entityDao) throws Exception {

		Map<String, Object> search = new HashMap<>();
		search.put(Constants.Parameters.ID, ConfigurationPanel.getChildId(entityDao.getId()));
		QueryDao queryDao = QueryUtils.queryBuilder(QueryUtils.Table.DATA_NODE, search, Boolean.FALSE,
				QueryUtils.Clauses.AND);
		List<EntityDao> childEntity = entityRepo.customFindAll(queryDao.getQuery(), queryDao.getParams());

		if (entityDao.getChildren() == null) {
			entityDao.setChildren(new ArrayList<>());
		}

		entityDao.setChildren(objectMapper.convertValue(childEntity, new TypeReference<List<Map<String, Object>>>() {
		}));

	}

	@Override
	public Boolean deleteEntity(Integer id) {
		try {
			entityRepo.deleteById(id);
			return true;
		} catch (Exception e) {
			LOGGER.error(String.format(Constants.Exception.EXCEPTION_METHOD, "deleteEntity", e.getMessage()));
		}
		return false;
	}

	@Override
	public Boolean addFeedback(Map<String, Object> feedbackDocument) {
		try {
			// esRepository.writeAnyObjectToElastic(feedbackDocument, "uniqueId",
			// serverProperties.getEsProfileIndex(),
			// serverProperties.getEsProfileIndexType());
			return Boolean.TRUE;
		} catch (Exception e) {
			LOGGER.error(String.format(Constants.Exception.EXCEPTION_METHOD, "addFeedback", e.getMessage()));
		}
		return false;
	}

	@Override
	public Boolean reviewEntity(EntityVerification entityVerification, UserProfile userProfile) {
		try {
			Optional<EntityDao> entityObj = entityRepo.findById(entityVerification.getId());
			if (entityObj.isPresent()) {
				EntityDao oldEntity = (entityObj.get()).clone();
				EntityDao entityDao = entityObj.get();
				entityDao.setReviewedBy(userProfile.getUserId());
				entityDao.setReviewedDate(DateUtils.getCurrentDateTimeInUTC());
				wfTransition(entityDao);
				entityDao.setStatus(entityVerification.getAction());
				if (checkReviewAccess(entityDao, userProfile.getRoles())) {
					entityRepo.save(entityDao);
					auditService.addEntityAudit(oldEntity, entityDao);
					return Boolean.TRUE;
				}

			}
		} catch (Exception e) {
			LOGGER.error(String.format(Constants.Exception.EXCEPTION_METHOD, "reviewEntity", e.getMessage()));
		}
		return Boolean.FALSE;
	}

	private void reviewAudit() {

	}

	/**
	 * Get workflow transition on review
	 * 
	 * @param entityDao
	 *            EntityDao
	 */
	private void wfTransition(EntityDao entityDao) {
		WfRequest wfRequest = new WfRequest();
		wfRequest.setWfId(entityDao.getWfId());
		wfRequest.setState(entityDao.getLevel());
		wfRequest.setAction(entityDao.getStatus());
		String userId = entityDao.getReviewedBy();
		wfRequest.setUserId(userId);
		wfRequest.setActorUserId(userId);
		wfRequest.setApplicationId(entityDao.getId().toString());
		wfRequest.setServiceName(Constants.WorkflowState.SERVICE);
		wfRequest.setUpdateFieldValues(Arrays.asList(new HashMap<>()));

		Boolean transition = workflowService.workflowTransition(wfRequest);
		if (transition) {
			entityDao.setLevel(wfRequest.getState());
			entityDao.setWfId(wfRequest.getWfId());
		}
	}

	private Boolean checkReviewAccess(EntityDao entityDao, List<String> userRoles) throws Exception {
		Boolean hasAccess = Boolean.FALSE;
		if (userRoles != null) {
			List<Map<String, Object>> data = workflowService.getworkflowAction(entityDao.getLevel());
			if (data != null) {
				for (Map<String, Object> roleObj : data) {
					if (entityDao.getStatus().equals(roleObj.get("action"))) {
						List<String> roles = objectMapper.convertValue(roleObj.get("roles"),
								new TypeReference<List<String>>() {
								});
						for (String roleName : roles) {
							if (userRoles.stream().anyMatch(name -> name.equals(roleName))) {
								hasAccess = Boolean.TRUE;
								break;
							}
						}
					}
					if (hasAccess) {
						break;
					}
				}
			}
		}

		return hasAccess;
	}

}
