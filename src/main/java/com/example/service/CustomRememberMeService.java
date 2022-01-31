package com.example.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.stereotype.Service;

import com.example.security.UserDetailsServiceImpl;

@Service
public class CustomRememberMeService extends TokenBasedRememberMeServices {

	private static UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl();
	
	public CustomRememberMeService() {
		super("rememberMe-key", userDetailsService);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onLoginFail(HttpServletRequest request, HttpServletResponse response) {
		Cookie cookie = new Cookie("last-username",request.getParameter("username"));
		cookie.setMaxAge(86400);
		cookie.setPath("/");
		response.addCookie(cookie);
	}
}
