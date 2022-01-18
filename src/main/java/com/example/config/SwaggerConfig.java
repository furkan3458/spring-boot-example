package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
//http://localhost:8080/swagger-ui/
@ComponentScan(basePackages = {"com.example"})
public class SwaggerConfig{
	
	@Bean
	  public OpenAPI obsAPI() {
	      return new OpenAPI()
	              .info(new Info().title("OBS API")
	              .description("Student information system example application")
	              .version("v0.0.1")
	              .license(new License().name("Apache 2.0").url("http://apache.org")))
	              .externalDocs(new ExternalDocumentation()
	              .description("Student Information System Documentation")
	              .url("https://obs.wiki.org"))
	              .components(new Components()
	                      .addSecuritySchemes("Bearer",
	                      new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("jwt")));
	  }
}