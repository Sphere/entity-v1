package com.sunbird.entity.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseParams {
	private String resmsgid;
	private String msgid;
	private String err;
	private String status;
	private String errmsg;
}
