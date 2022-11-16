package com.sunbird.entity.model;

import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WfRequest {

	private String state;

	private String action;

	private String applicationId;

	private String userId;

	private String actorUserId;

	private String wfId;

	private List<HashMap<String, Object>> updateFieldValues;

	private String comment;

	private String serviceName;

	private String deptName;

}
