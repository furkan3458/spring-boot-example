package com.example.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jwt.JwtResponse;
import com.example.jwt.JwtUtils;
import com.example.jwt.MessageResponse;
import com.example.model.JwtSession;
import com.example.model.Role;
import com.example.model.User;
import com.example.repository.JwtSessionRepository;
import com.example.repository.RoleRepository;
import com.example.repository.UserRepository;
import com.example.request.LoginRequest;
import com.example.request.SignupRequest;
import com.example.request.ValidateRequest;
import com.example.response.ValidateResponse;
import com.example.security.UserDetailsImpl;
import com.example.util.ERole;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	JwtSessionRepository jwtSessionRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;
	
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> authController(){
		
		return ResponseEntity.ok("Success");
	}

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication,loginRequest.isRememberMe());
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		JwtSession jwtSession = new JwtSession();
		
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());
		
		User user = new User(userDetails.getId(), 
				userDetails.getUsername(), 
				userDetails.getFullname(), 
				userDetails.getEmail(),
				userDetails.getPassword());
		Optional <JwtSession> jwtSessionOptional = jwtSessionRepository.findByUsers(user);
		
		if(jwtSessionOptional.isEmpty()) 
			jwtSession.setUsers(user);
		else 
			jwtSession = jwtSessionOptional.get();
		
		jwtSession.setJwttoken(jwt);
		jwtSession.setExpiretime(loginRequest.isRememberMe() ? null : jwtUtils.getExpireTimeFromJwtToken(jwt));	
		
		jwtSessionRepository.save(jwtSession);

		return ResponseEntity.ok(new JwtResponse(jwt, 
												 userDetails.getId(), 
												 userDetails.getFullname(),
												 userDetails.getUsername(), 
												 userDetails.getEmail(), 
												 roles,
												 loginRequest.isRememberMe()));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		
		if(!signUpRequest.getUsername().matches("^[a-zA-Z0-9.!@_]{6,30}$")) {
			return ResponseEntity
					.ok(new MessageResponse(false,1,"Error: Username length must be betweeen 6 and 30 character."));
		}
		else if(!signUpRequest.getEmail().matches("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")) {
			return ResponseEntity
					.ok(new MessageResponse(false,2,"Error: Email is invalid."));
		}
		else if(!signUpRequest.getPassword().matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$")) {
			return ResponseEntity
					.ok(new MessageResponse(false,3,"Error: Password must be at least 8 characters long and contain uppercase letters, numbers and special characters."));
		}
		else if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity
					.ok(new MessageResponse(false,4,"Error: Username is already taken!"));
		}
		else if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity
					.ok(new MessageResponse(false,5,"Error: Email is already in use!"));
		}

		// Create new user's account
		User user = new User(signUpRequest.getUsername(), 
							 signUpRequest.getFullname(),
							 signUpRequest.getEmail(),
							 encoder.encode(signUpRequest.getPassword()));

		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(adminRole);

					break;
				case "mod":
					Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(modRole);

					break;
				case "teacher":
					Role teacherRole = roleRepository.findByName(ERole.ROLE_TEACHER)
						.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(teacherRole);
					break;
				case "db_admin":
					Role dbAdminRole = roleRepository.findByName(ERole.ROLE_DB_ADMIN)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(dbAdminRole);
				default:
					Role userRole = roleRepository.findByName(ERole.ROLE_USER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(userRole);
				}
			});
		}

		user.setRoles(roles);
		user = userRepository.save(user);
		
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(signUpRequest.getUsername(), signUpRequest.getPassword()));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication,false);

		jwtSessionRepository.save(new JwtSession(user,jwt,jwtUtils.getExpireTimeFromJwtToken(jwt)));

		List<String> listRoles = roles.stream()
				.map(item -> item.getName().toString())
				.collect(Collectors.toList());
		
		return ResponseEntity.ok(new JwtResponse(jwt, 
												 user.getId(), 
												 user.getFullname(),
												 user.getUsername(), 
												 user.getEmail(), 
												 listRoles,
												 false));

	}
	
	@PostMapping(path="/validate", consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> validateUser(@RequestBody ValidateRequest validateRequest) {
		
		Optional<JwtSession> jwtSessionOptional;
		
		if(validateRequest.getToken().isEmpty() || validateRequest.getToken() == null || validateRequest.getToken().isBlank() ||
			validateRequest.getUsername().isEmpty() || validateRequest.getUsername() == null || validateRequest.getUsername().isBlank()) {
			return ResponseEntity.ok(new ValidateResponse(false, 1, "Cannot acceptable."));
		}
		else if(!jwtUtils.validateToken(validateRequest.getToken()) || !jwtUtils.getUserNameFromJwtToken(validateRequest.getToken()).equals(validateRequest.getUsername())) {
			return ResponseEntity.ok(new ValidateResponse(false, 2, "Cannot acceptable."));
		}
		else if((jwtSessionOptional = jwtSessionRepository.findByJwttoken(validateRequest.getToken())).isEmpty() || !jwtSessionOptional.get().getUsers().getUsername().equals(validateRequest.getUsername())) {
			return ResponseEntity.ok(new ValidateResponse(false, 3, "Cannot acceptable."));
		}
			
		return ResponseEntity.ok(new ValidateResponse(true, 0, "Success."));
	}
	
	@PostMapping(path="/validate_username", consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> validateUsername(@RequestBody ValidateRequest validateRequest) {
		
		Optional<User> user = userRepository.findByUsername(validateRequest.getUsername());
		
		if(user.isPresent()) {
			return ResponseEntity.ok(new ValidateResponse(false, 0, "User found with that username."));
		}
		
		return ResponseEntity.ok(new ValidateResponse(true, 0, "Success."));
	}
	
	@PostMapping(path="/validate_email", consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> validateEmail(@RequestBody ValidateRequest validateRequest) {
		
		Optional<User> user = userRepository.findByEmail(validateRequest.getEmail());
		
		if(user.isPresent()) {
			return ResponseEntity.ok(new ValidateResponse(false, 0, "User found with that email."));
		}
		
		return ResponseEntity.ok(new ValidateResponse(true, 0, "Success."));
	}
	
	@PostMapping(path="/logout", consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> logout(@RequestBody ValidateRequest validateRequest) {
		
		Optional<JwtSession> session = jwtSessionRepository.findByJwttoken(validateRequest.getToken());
		
		if(session.isEmpty()) {
			
			return ResponseEntity.ok(new ValidateResponse(false, 0, "Token not found. Session is invalid but logout can applicable."));
		}
		
		jwtSessionRepository.delete(session.get());
		
		return ResponseEntity.ok(new ValidateResponse(true, 0, "Success."));
	}
}