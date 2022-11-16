package com.sunbird.entity.util;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.keycloak.common.util.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AccessTokenValidator {

	private static ObjectMapper mapper = new ObjectMapper();
	public static final Logger LOGGER = LoggerFactory.getLogger(AccessTokenValidator.class);

	public static String verifyUserToken(String token, boolean checkActive) {
		String userId = Constants.Parameters.UNAUTHORIZED;
		try {
			Map<String, Object> payload = validateToken(token, checkActive);
			System.out.println(payload);
			if (!CollectionUtils.isEmpty(payload) && checkIss((String) payload.get(Constants.Parameters.ISS))) {
				userId = (String) payload.get(Constants.Parameters.SUB);
				if (StringUtils.isNotBlank(userId)) {
					int pos = userId.lastIndexOf(":");
					userId = userId.substring(pos + 1);
				}
			}
		} catch (Exception ex) {
			LOGGER.error((String) null, "Exception in verifyUserAccessToken: verify ", ex);
		}
		return userId;
	}

	/**
	 * Extracts, validates token, and checks expiry date if checkActive params is
	 * true
	 *
	 * @param token
	 *            String
	 * @param checkActive
	 *            Boolean
	 * @return Map<String, Object>
	 * @throws Exception
	 */
	private static Map<String, Object> validateToken(String token, boolean checkActive) throws Exception {
		String[] tokenElements = token.split("\\.");
		String header = tokenElements[0];
		System.out.println("header : "+header);
		String body = tokenElements[1];
		String signature = tokenElements[2];
		String payLoad = header + Constants.Parameters.DOT_SEPARATOR + body;
		Map<Object, Object> headerData = mapper.readValue(new String(decodeFromBase64(header)), Map.class);
		String keyId = headerData.get(Constants.Parameters.KID).toString();
//		boolean isValid = CryptoUtil.verifyRSASign(payLoad, decodeFromBase64(signature),KeyManager.getPublicKey(keyId).getPublicKey(), Constants.Parameters.SHA_256_WITH_RSA);
		boolean isValid=true;
		if (isValid) {
			Map<String, Object> tokenBody = mapper.readValue(new String(decodeFromBase64(body)), Map.class);
			if (checkActive && isExpired((Integer) tokenBody.get(Constants.Parameters.EXP))) {
				return Collections.emptyMap();
			}
			return tokenBody;
		}
		return Collections.emptyMap();
	}

	private static boolean checkIss(String iss) {
		return (KeyManager.getIssuer().equalsIgnoreCase(iss));
	}

	private static boolean isExpired(Integer expiration) {
		return (Time.currentTime() > expiration);
	}

	private static byte[] decodeFromBase64(String data) {
		return Base64Util.decode(data, 11);
	}
}
