package com.example.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.JwtSession;
import com.example.model.User;

public interface JwtSessionRepository extends JpaRepository<JwtSession,Long>{
	
	public Optional<JwtSession> findByUsers(User users);
	
	public Optional<JwtSession> findByJwttoken(String jwttoken);
}
