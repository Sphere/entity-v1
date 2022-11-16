package com.sunbird.entity.util;

public class PathRoutes {

	public interface Endpoints {
		String ADD_UPDATE_ENTITY = "/addUpdateEntity";
		String ADD_ENTITIES = "/addEntities";
		String GET_ENTITY_BY_ID = "/getEntityById/{id}";
		String ADD_ENTITY_RELATION = "/addEntityRelation";
		String BOOKMARK_DATA_NODE = "/bookmarkDataNode";
		String GET_ALL_ENTITY = "/getAllEntity";
		String DELETE_ENTITY = "/deleteEntity";
		String UPLOAD_FILE = "/upload";
		String DELETE_FILE = "/delete";
		String NODE_FEEDBACK = "/nodeFeedback";
		String REVIEW_ENTITY = "/reviewEntity";

	}

}
