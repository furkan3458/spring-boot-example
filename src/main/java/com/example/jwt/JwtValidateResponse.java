package com.example.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtValidateResponse {
	
	private boolean validate;
	private Integer status;
	private String message;
	
}
