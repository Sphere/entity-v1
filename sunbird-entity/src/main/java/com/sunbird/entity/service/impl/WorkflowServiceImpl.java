package com.sunbird.entity.service.impl;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.sunbird.entity.model.WfRequest;
import com.sunbird.entity.service.WorkflowService;
import com.sunbird.entity.util.Constants;
import com.sunbird.entity.util.OutboundRequestHandler;
import com.sunbird.entity.util.ServerProperties;

@Service
public class WorkflowServiceImpl implements WorkflowService {

	public static final Logger LOGGER = LoggerFactory.getLogger(WorkflowServiceImpl.class);

	Gson gson = new Gson();

	ObjectMapper mapper = new ObjectMapper();

	@Autowired
	ServerProperties serverProperties;

	@Override
	public Boolean workflowTransition(WfRequest wfRequest) {
		try {
			Object responseObject = OutboundRequestHandler.makeRestCall(
					serverProperties.getWfHost() + serverProperties.getWfTransition(), wfRequest, getHeaders(),
					HttpMethod.POST);
			if (responseObject != null) {
				Map<String, Object> result = mapper.convertValue(responseObject, Map.class);
				Map<String, Object> data = mapper.convertValue(result.get(Constants.Parameters.DATA), Map.class);
				List<Object> wfIds = mapper.convertValue(data.get("wfIds"), List.class);
				wfRequest.setState((String) data.get(Constants.Parameters.STATUS));
				wfRequest.setWfId((String) wfIds.get(0));
			}
			return Boolean.TRUE;
		} catch (Exception e) {
			LOGGER.error(String.format(Constants.Exception.EXCEPTION_METHOD, "workflowRequest", e.getMessage()));
			return Boolean.FALSE;
		}
	}

	@Override
	public List<Map<String, Object>> getworkflowAction(String state) {
		try {
			Object result = OutboundRequestHandler.makeRestCall(
					serverProperties.getWfHost() + serverProperties.getWfAction() + state, null, getHeaders(),
					HttpMethod.GET);
			Map<String, Object> resultSet = mapper.convertValue(result, Map.class);
			return mapper.convertValue(resultSet.get(Constants.Parameters.DATA),
					new TypeReference<List<Map<String, Object>>>() {
					});
		} catch (Exception e) {
			LOGGER.error(String.format(Constants.Exception.EXCEPTION_METHOD, "getworkflowAction", e.getMessage()));
		}
		return null;
	}

	private HttpHeaders getHeaders() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add(Constants.Parameters.ROOT_ORG, serverProperties.getWfRootOrg());
		headers.add(Constants.Parameters.ORG, serverProperties.getWfOrg());
		return headers;
	}

}
