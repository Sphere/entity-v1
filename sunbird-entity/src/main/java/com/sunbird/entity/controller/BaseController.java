package com.sunbird.entity.controller;

import java.util.HashMap;
import java.util.Map;

import ch.qos.logback.core.net.SyslogOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunbird.entity.model.ResponseDto;
import com.sunbird.entity.model.ResponseParams;
import com.sunbird.entity.model.UserProfile;
import com.sunbird.entity.util.Constants;
import com.sunbird.entity.util.DateUtils;
import com.sunbird.entity.util.OutboundRequestHandler;
import com.sunbird.entity.util.ResponseCode;
import com.sunbird.entity.util.ServerProperties;

public class BaseController {

	@Autowired
	public ObjectMapper mapper;

	@Autowired
	ServerProperties serverProperties;

	public static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

	public String handleResponse(Object response, ResponseCode responseCode) throws JsonProcessingException {
		ResponseDto responseDto = new ResponseDto();
		responseDto.setTs(DateUtils.getCurrentTimestamp());
		responseDto.setResult(new HashMap<>());
		if (responseCode == null) {
			responseCode = ResponseCode.FAILED;
		}
		// set response value
		if (response != null) {
			if (!(response instanceof Boolean) || (response instanceof Boolean && (Boolean) response)) {
				responseCode = ResponseCode.SUCCESS;
				responseDto.getResult().put(Constants.Parameters.RESPONSE, response);
			}
		}

		ResponseParams params = new ResponseParams();
		params.setErrmsg(responseCode.getErrorMessage());
		params.setStatus(responseCode.getErrorCode());
		responseDto.setResponseCode(responseCode.getResponseCode());
		responseDto.setParams(params);

		return mapper.writeValueAsString(responseDto);
	}

	public UserProfile getUserProfile(String userId, String token) {
		System.out.println(" getuserprofile =================== ");
		System.out.println(userId);
		System.out.println(token);
		try {
			String url = serverProperties.getReadEndpoint() + userId;

			HttpHeaders headers = new HttpHeaders();
			headers.add(Constants.Parameters.AUTHORIZATION, serverProperties.getAuthAPIKey());
//			headers.add(Constants.Parameters.X_USER_TOKEN, token);

			Map<String, Object> result = mapper
					.convertValue(OutboundRequestHandler.makeRestCall(url, null, headers, HttpMethod.GET), Map.class);
			UserProfile userProfile = mapper.convertValue(result.get(Constants.Parameters.RESPONSE), UserProfile.class);
			return userProfile;
		} catch (Exception e) {
			LOGGER.error(String.format("Exception in getUserProfile method : %s", e.getMessage()));
			return null;
		}

	}

}
