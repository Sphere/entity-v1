package com.sunbird.entity.util;

public class ResponseMessage {

	public interface Message {
		String SUCCESS_MESSAGE = "Success";
		String FAILURE_MESSAGE = "Process failed, please try again later.";
		String UNAUTHORIZED_USER = "You are not authorized.";
		String TOKEN_MISSING = "Auth token is missing";
		String CREATE_ERROR_MESSAGE = "Unable to create. Please try again later!";
		String UPLOAD_ERROR_MESSAGE = "Unable to upload. Please try again later!";
		String DELETE_ERROR_MESSAGE = "Unable to delete. Please try again later!";
		String GET_ERROR_MESSAGE = "Unable to fetch the details. Please try again later!";
		String MAPPING_ERROR_MESSAGE = "Mapping failed. Please try again later!";
	}

	public interface Key {
		String SUCCESS_MESSAGE = "SUCCESS";
		String FAILURE_MESSAGE = "INTERNAL_ERROR";
		String UNAUTHORIZED_USER = "UNAUTHORIZED_USER";
		String BAD_REQUEST = "BAD_REQUEST";
	}

	public interface Code {
		int SUCCESS = 200;
		int FAILURE = 500;
		int UNAUTHORIZED = 401;
		int BAD_REQUEST = 400;
	}

}
