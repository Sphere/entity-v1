package com.sunbird.entity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.sunbird.entity.controller.BaseController;
import com.sunbird.entity.util.AccessTokenValidator;
import com.sunbird.entity.util.Constants;
import com.sunbird.entity.util.ResponseCode;

@Component
public class RequestInterceptor extends BaseController implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		// read auth token from header
		String authToken = request.getHeader(Constants.Parameters.X_USER_TOKEN);
		if (StringUtils.isBlank(authToken)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write(handleResponse(false, ResponseCode.TOKEN_MISSING));
			response.setContentType(MediaType.APPLICATION_JSON);
			return Boolean.FALSE;
		}
		// authentication
		System.out.println("request_token :"+ authToken);
		String userId = verifyRequestData(authToken);
		System.out.println("userId :"+ userId);
		if (userId.equalsIgnoreCase(Constants.Parameters.UNAUTHORIZED)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write(handleResponse(false, ResponseCode.UNAUTHORIZED));
			response.setContentType(MediaType.APPLICATION_JSON);
			return Boolean.FALSE;
		}
		request.setAttribute(Constants.Parameters.USER_ID, userId);
		return Boolean.TRUE;
	}

	private String verifyRequestData(String accessToken) {
		System.out.println("verifyRequestData () "+accessToken);
		String clientAccessTokenId = AccessTokenValidator.verifyUserToken(accessToken, true);
		System.out.println("verifyRequestData clientAccessTokenId (): "+clientAccessTokenId);
		return StringUtils.isBlank(clientAccessTokenId) ? Constants.Parameters.UNAUTHORIZED : clientAccessTokenId;
	}

}
