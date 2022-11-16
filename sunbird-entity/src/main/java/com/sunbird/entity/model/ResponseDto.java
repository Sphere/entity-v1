package com.sunbird.entity.model;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDto {
	private String id;
	private String ver;
	private Long ts;
	private ResponseParams params;
	private int responseCode;
	private Map<String, Object> result = new HashMap<>();
}
