package com.example.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.example.filter.AuthTokenFilter;
import com.example.jwt.AuthEntryPointJwt;
import com.example.security.UserDetailsServiceImpl;
import com.example.service.CustomRememberMeService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
		// securedEnabled = true,
		// jsr250Enabled = true,
		prePostEnabled = true)

public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	UserDetailsServiceImpl userDetailsService;

	@Autowired
	private AuthEntryPointJwt unauthorizedHandler;

	@Autowired
	private DataSource dataSource;
	
	@Autowired
    private CustomRememberMeService rememberMeServices;
	
	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}

	@Override
	public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	 public PersistentTokenRepository persistentTokenRepository() {
	     JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
	     db.setDataSource(dataSource);
	     return db;
	}
	
	@Override
    public void configure(WebSecurity web) {
    }

	@Override
	protected void configure(HttpSecurity http) throws Exception {	
		http.exceptionHandling().authenticationEntryPoint(unauthorizedHandler);
		http.authorizeHttpRequests().antMatchers("/obs/**").authenticated()
		.anyRequest().permitAll();
		http.formLogin().loginPage("/login").defaultSuccessUrl("/login_success").failureUrl("/login_error");
		http.rememberMe().tokenValiditySeconds(86400).userDetailsService(userDetailsService).rememberMeServices(rememberMeServices);
		
		http.logout().logoutSuccessUrl("/logout_success");
		
		http.csrf().disable();

		http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
	}
}
