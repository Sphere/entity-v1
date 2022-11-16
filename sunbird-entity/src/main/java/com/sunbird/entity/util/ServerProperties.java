package com.sunbird.entity.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class ServerProperties {

	@Value("${storage.key}")
	private String storageKey;

	@Value("${container.name}")
	private String containerName;

	@Value("${identity.name}")
	private String identityName;

	@Value("${type.name}")
	private String typeName;

	@Value("${send-notification}")
	private String sendNotification;

	@Value("${notification-url}")
	private String notificationUrl;

	@Value("${auth-api-key}")
	private String authAPIKey;

	@Value("${wf.service.url}")
	private String wfHost;

	@Value("${wf.transition.endpoint}")
	private String wfTransition;

	@Value("${wf.action.endpoint}")
	private String wfAction;

	@Value("${wf.rootorg}")
	private String wfRootOrg;

	@Value("${wf.org}")
	private String wfOrg;

	@Value("${accesstoken.publickey.basepath}")
	private String publickeyFile;

	@Value("${sunbird_sso_url}")
	private String ssoUrl;

	@Value("${sunbird_sso_realm}")
	private String ssoRealm;

	@Value("${user.read.endpoint}")
	private String readEndpoint;

	@Value("${entity.audit.index}")
	private String entityAuditIndex;

}
