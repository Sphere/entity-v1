package com.sunbird.entity.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class QueryDao {

	private String query;
	private Map<String, Object> params;

}
