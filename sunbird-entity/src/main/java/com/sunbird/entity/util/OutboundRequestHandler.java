package com.sunbird.entity.util;

import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OutboundRequestHandler {

	private static RestTemplate restTemplate;

	@Autowired
	OutboundRequestHandler(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public static final Logger LOGGER = LoggerFactory.getLogger(OutboundRequestHandler.class);

	public static Object makeRestCall(String url, Object request, HttpHeaders headers, HttpMethod method) {
		try {
			LOGGER.info("Outbound request URL : " + url);
			headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
			headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);

			HttpEntity<Object> entity = null;
			if (method.equals(HttpMethod.GET)) {
				entity = new HttpEntity<>(headers);
			} else if (method.equals(HttpMethod.POST)) {
				entity = new HttpEntity<>(request, headers);
			}

			ResponseEntity<Map> response = restTemplate.exchange(url, method, entity, Map.class);

			if (response.getStatusCode().is2xxSuccessful()) {
				Object result = response.getBody().get(Constants.Parameters.RESULT);
				return result;
			} else {
				LOGGER.info(String.format("Failed to get response with status : %s and message : %s",
						response.getStatusCode(), response.getBody()));
			}

		} catch (Exception e) {
			LOGGER.error(String.format("Exception in getRequest: %s", e.getMessage()));
		}
		return null;
	}
}
