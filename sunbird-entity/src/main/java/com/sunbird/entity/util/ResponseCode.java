package com.sunbird.entity.util;

public enum ResponseCode {

	SUCCESS(ResponseMessage.Key.SUCCESS_MESSAGE, ResponseMessage.Message.SUCCESS_MESSAGE,
			ResponseMessage.Code.SUCCESS), 
	FAILED(ResponseMessage.Key.FAILURE_MESSAGE, ResponseMessage.Message.FAILURE_MESSAGE,
			ResponseMessage.Code.FAILURE), 
	UNAUTHORIZED(ResponseMessage.Key.UNAUTHORIZED_USER, ResponseMessage.Message.UNAUTHORIZED_USER,
			ResponseMessage.Code.UNAUTHORIZED), 
	TOKEN_MISSING(ResponseMessage.Key.BAD_REQUEST, ResponseMessage.Message.TOKEN_MISSING,
			ResponseMessage.Code.BAD_REQUEST), 
	CREATE_FAILED(ResponseMessage.Key.FAILURE_MESSAGE, ResponseMessage.Message.CREATE_ERROR_MESSAGE,
			ResponseMessage.Code.FAILURE),
	UPLOAD_FAILED(ResponseMessage.Key.FAILURE_MESSAGE, ResponseMessage.Message.UPLOAD_ERROR_MESSAGE,
			ResponseMessage.Code.FAILURE),
	DELETE_FAILED(ResponseMessage.Key.FAILURE_MESSAGE, ResponseMessage.Message.UPLOAD_ERROR_MESSAGE,
			ResponseMessage.Code.FAILURE),
	GET_FAILED(ResponseMessage.Key.FAILURE_MESSAGE, ResponseMessage.Message.GET_ERROR_MESSAGE,
			ResponseMessage.Code.FAILURE),
	MAPPING_FAILED(ResponseMessage.Key.FAILURE_MESSAGE, ResponseMessage.Message.MAPPING_ERROR_MESSAGE,
			ResponseMessage.Code.FAILURE);

	private Integer responseCode;
	private String errorCode;
	private String errorMessage;

	private ResponseCode(String errorCode, String errorMessage, int responseCode) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.responseCode = responseCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public int getResponseCode() {
		return responseCode;
	}

}
