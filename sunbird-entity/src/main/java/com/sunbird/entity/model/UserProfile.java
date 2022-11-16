package com.sunbird.entity.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfile {

	private String userId;
	private String firstName;
	private String userName;
	private String email;
	private String channel;
	private String rootOrgId;
	private List<String> roles;

}
