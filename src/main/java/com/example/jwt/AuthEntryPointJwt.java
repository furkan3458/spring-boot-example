package com.example.jwt;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.example.model.Error;
import com.example.util.ErrorHandleType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import io.jsonwebtoken.JwtException;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

	private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		logger.error("Unauthorized error: {}", authException.getMessage());

		response.setContentType("application/json");
		response.getWriter().print(authorizedError(authException.getMessage()));
		response.getWriter().flush();
	}
	
	public void commence(HttpServletRequest request, HttpServletResponse response,
			JwtException authException) throws IOException, ServletException {
		logger.error("Unauthorized error: {}", authException.getMessage());

		response.setContentType("application/json");
		response.getWriter().print(authorizedError(authException.getMessage()));
		response.getWriter().flush();
	}

	private String authorizedError(String message) {
		ObjectMapper o = new ObjectMapper();
		Error error = new Error(HttpStatus.UNAUTHORIZED, ErrorHandleType.UNAUTHORIZED_ACCESS, "Unauthorized access denied.", message,LocalDateTime.now().toLocalDate().toString(), LocalDateTime.now().toLocalTime().toString());
		String er;
		
		DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

		LocalDateTimeDeserializer dateTimeDeserializer = new LocalDateTimeDeserializer(formatter);
		LocalDateTimeSerializer dateTimeSerializer = new LocalDateTimeSerializer(formatter);

		JavaTimeModule javaTimeModule = new JavaTimeModule(); 
		javaTimeModule.addDeserializer(LocalDateTime.class, dateTimeDeserializer);
		javaTimeModule.addSerializer(LocalDateTime.class, dateTimeSerializer);

		o.registerModule(javaTimeModule);
		
		try {
			er = o.writeValueAsString(error);
		} catch (JsonProcessingException e) {
			er = "Unauthorized access denied.";
			e.printStackTrace();
		}
		
		return er;
	}
	
}