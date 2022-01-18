package com.example.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.stereotype.Service;

@Service
public class CustomRememberMeService implements RememberMeServices {

	@Override
	public Authentication autoLogin(HttpServletRequest request, HttpServletResponse response) {
		return null;
	}

	@Override
	public void loginFail(HttpServletRequest request, HttpServletResponse response) {
		Cookie cookie = new Cookie("last-username",request.getParameter("username"));
		cookie.setMaxAge(86400);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	@Override
	public void loginSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication successfulAuthentication) {
	}

}
