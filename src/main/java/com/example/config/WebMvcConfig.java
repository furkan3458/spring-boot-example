package com.example.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import com.example.filter.NonAuthFilter;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer{
	  
	  @Bean
	  public MessageSource messageSource() {
		  ResourceBundleMessageSource  messageSource = new ResourceBundleMessageSource ();
		  messageSource.setBasename("i18n/messages");
	      messageSource.setDefaultEncoding("UTF-8");
	      
		  return messageSource;
	  }
	  
	  @Bean
	  public LocaleResolver localeResolver() {
		  return new CookieLocaleResolver();
	  }

	  @Bean
	  public LocaleChangeInterceptor localeChangeInterceptor() {
		  LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
		  lci.setParamName("lang");
		  return lci;
	  }
	  
	  @Bean
	  public FilterRegistrationBean<NonAuthFilter> loggingFilter(){
	      FilterRegistrationBean<NonAuthFilter> registrationBean 
	        = new FilterRegistrationBean<>();
	          
	      registrationBean.setFilter(new NonAuthFilter());
	      registrationBean.addUrlPatterns("/sign_in","/sign_up");
	          
	      return registrationBean;    
	  }

	  @Override
	  public void addInterceptors(InterceptorRegistry registry) {
		  registry.addInterceptor(localeChangeInterceptor());
	  }
}
