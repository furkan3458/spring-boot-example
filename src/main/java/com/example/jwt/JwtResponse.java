package com.example.jwt;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {

	private String token;
	private Long id;
	private String username, email;
	private List<String> roles;
	
}
