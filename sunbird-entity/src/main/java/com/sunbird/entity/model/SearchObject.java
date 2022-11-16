package com.sunbird.entity.model;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchObject {

	private Map<String, Object> filter;
	private Map<String, Object> search;
	private Boolean keywordSearch;

}
