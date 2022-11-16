package com.sunbird.entity.service;

import java.util.List;
import java.util.Map;

import com.sunbird.entity.model.WfRequest;

public interface WorkflowService {

	Boolean workflowTransition(WfRequest wfRequest);

	List<Map<String, Object>> getworkflowAction(String state);

}
