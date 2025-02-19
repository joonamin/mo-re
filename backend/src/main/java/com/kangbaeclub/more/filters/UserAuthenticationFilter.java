package com.kangbaeclub.more.filters;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

public class UserAuthenticationFilter extends OncePerRequestFilter {
	private static final String TOKEN_PREFIX = "Bearer";
	private static final AuthenticationFailedResponse TOKEN_MISSING = new AuthenticationFailedResponse(-401,
		"check the authorization header");

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);

		// 1. 인증이 필요한 API에 토큰이 포함되지 않은 경우
		if (bearerToken == null || bearerToken.isEmpty() || !bearerToken.split(" ")[0].equals(TOKEN_PREFIX)) {
			handleError(response, TOKEN_MISSING);
			return;
		}
		String token = bearerToken.split(" ")[1];
		String userId = extractUserIdFromToken(token);
		request = sanitizeRequest(request, userId);
		filterChain.doFilter(request, response);
	}

	private HttpServletRequest sanitizeRequest(HttpServletRequest request, String userId) throws IOException {
		CustomHttpServletRequestWrapper sanitizedRequest = new CustomHttpServletRequestWrapper(request);
		sanitizedRequest.addUserIdInRequestBody(userId);
		return sanitizedRequest;
	}

	private String extractUserIdFromToken(String token) {
		// TODO: implement this method
		return "testUserId";
	}

	private void handleError(HttpServletResponse response, AuthenticationFailedResponse error) throws IOException {
		response.setStatus(HttpStatus.BAD_REQUEST.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.writeValue(response.getWriter(), error);
	}

	private static class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {
		private Map<String, String> properties;

		public CustomHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
			super(request);
			initializeProperties(request);
		}

		@SuppressWarnings("unchecked")
		private void initializeProperties(HttpServletRequest request) throws IOException {
			StringBuilder stringBuilder = new StringBuilder();
			try (BufferedReader bufferedReader = request.getReader()) {
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					stringBuilder.append(line);
				}
			}
			ObjectMapper objectMapper = new ObjectMapper();
			properties = objectMapper.readValue(stringBuilder.toString(), Map.class);
		}

		private void addUserIdInRequestBody(String userId) {
			properties.put("userId", userId);
		}
	}
	private static class AuthenticationFailedResponse {
		private int code;
		private String message;

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public AuthenticationFailedResponse() {
		}

		public AuthenticationFailedResponse(int code, String messege) {
			this.code = code;
			this.message = messege;
		}
	}
}
