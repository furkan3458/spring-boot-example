package com.example.filter;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class NonAuthFilter implements Filter{

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println("Role:"+auth.getName()+", Contains:"+auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
		
		if(auth.isAuthenticated() && !auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
			log.info("Trying to request with authenticated [ {}.{}, Role: {}]", 
    				((HttpServletRequest)(request)).getRequestURI(),
    				((HttpServletRequest)(request)).getMethod(),
    				auth.getName()
    				);
            
            ((HttpServletResponse)(response)).sendRedirect("/");
		}
		

		else
			chain.doFilter(request, response);
	}

}
