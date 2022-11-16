package com.sunbird.entity.util;

public class Constants {

	public interface Exception {
		String EXCEPTION_METHOD = "Exception in method %s : %s";
		String GET_NODE_ERROR = "Unable to get the node";
	}

	public class ResponseCodes {
		private ResponseCodes() {
		}

		public static final int UNAUTHORIZED_ID = 401;
		public static final int SUCCESS_ID = 200;
		public static final int FAILURE_ID = 320;
		public static final String UNAUTHORIZED = "Invalid credentials. Please try again.";
		public static final String PROCESS_FAIL = "Process failed, Please try again.";
		public static final String SUCCESS = "success";
	}

	public class ServiceRepositories {
		private ServiceRepositories() {
		}

		public static final String NOTIFICATION_UTIL = "notificationUtil";
	}

	public interface Parameters {
		String ID = "id";
		String AUTHORIZATION = "Authorization";
		String X_USER_TOKEN = "x-authenticated-user-token";
		String RESPONSE = "response";
		String IS_DETAIL = "isDetail";
		String PARENT_ID = "parentId";
		String CHILD = "child";
		String NAME = "name";
		String DESCRIPTION = "description";
		String STATUS = "status";
		String SOURCE = "source";
		String LEVEL = "level";
		String DATA = "data";
		String RESULT = "result";
		String ROOT_ORG = "rootOrg";
		String ORG = "org";
		String USER_ID = "userId";

		String COMPETENCY = "COMPETENCY";
		String KNOWLEDGERESOURCE = "KNOWLEDGERESOURCE";
		String COMPETENCIESLEVEL = "COMPETENCIESLEVEL";
		String COMPETENCYAREA = "COMPETENCYAREA";

		String ANONYMOUS = "Anonymous";
		String UNAUTHORIZED = "Unauthorized";
		String DOT_SEPARATOR = ".";
		String SHA_256_WITH_RSA = "SHA256withRSA";
		String SUB = "sub";
		String ISS = "iss";
		String EXP = "exp";
		String KID = "kid";
	}

	public interface Actions {
		String CREATE = "CREATED";
		String UPDATE = "UPDATED";
		String REMOVE = "REMOVED";
	}

	public interface WorkflowState {
		String SERVICE = "entity";
		String INITIATE = "INITIATE";
		String UNVERIFIED = "UNVERIFIED";
		String DRAFT = "DRAFT";
	}

}
