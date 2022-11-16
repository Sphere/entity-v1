package com.sunbird.entity.model;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntityLog {

	private Integer id;
	private String type;
	private String updatedBy;
	private String updatedDate;
	private String username;
	private Object oldObject;
	private Object updatedObject;
	private Long timestamp;
	private Map<String, Audit> changes;

}
