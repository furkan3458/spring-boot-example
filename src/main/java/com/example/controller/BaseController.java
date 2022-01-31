package com.example.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.jwt.JwtUtils;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.security.UserDetailsImpl;

@Controller
@RequestMapping("/")
public class BaseController {
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	JwtUtils jwtUtils;
	
	
	@GetMapping
	public ModelAndView index(HttpSession session) {
		
		HashMap<String,Object> objects = new HashMap<>();
		
		if(session.getAttribute("user") != null) {
			List<String> columns = new ArrayList<>();
			columns.add("Id");
			columns.add("Teacher");
			columns.add("Subject");
			
			objects.put("columns", columns);
			objects.put("table","course");
			objects.put("session_user", session.getAttribute("user"));
		}
		
		return new ModelAndView("index").addAllObjects(objects);
	}
	
	@GetMapping(path="/student")
	public ModelAndView student(HttpSession session) {
		
		HashMap<String,Object> objects = new HashMap<>();
		List<String> columns = new ArrayList<>();
		
		columns.add("Id");
		columns.add("Name");
		columns.add("Number");
		
		objects.put("columns", columns);
		objects.put("table","student");
		objects.put("session_user", session.getAttribute("user"));
		
		return new ModelAndView("index").addAllObjects(objects);
	}
	
	@GetMapping(path="/assignment")
	public ModelAndView assignment(HttpSession session) {
		
		HashMap<String,Object> objects = new HashMap<>();
		List<String> columns = new ArrayList<>();
		
		columns.add("Id");
		columns.add("Student Name");
		columns.add("Student Number");
		columns.add("Subject");
		columns.add("Teacher");
		
		objects.put("columns", columns);
		objects.put("table","course_student");
		objects.put("session_user", session.getAttribute("user"));
		
		return new ModelAndView("index").addAllObjects(objects);
	}
	
	@GetMapping(path="/subject")
	public ModelAndView subject(HttpSession session) {
		
		HashMap<String,Object> objects = new HashMap<>();
		List<String> columns = new ArrayList<>();
		
		columns.add("Id");
		columns.add("Subject");
		
		objects.put("columns", columns);
		objects.put("table","subject");
		objects.put("session_user", session.getAttribute("user"));
		
		return new ModelAndView("index").addAllObjects(objects);
	}
	
	@GetMapping(path="/teacher")
	public ModelAndView teacher(HttpSession session) {
		
		HashMap<String,Object> objects = new HashMap<>();
		List<String> columns = new ArrayList<>();
		
		columns.add("Id");
		columns.add("Name");
		
		objects.put("columns", columns);
		objects.put("table","teacher");
		objects.put("session_user", session.getAttribute("user"));
		
		return new ModelAndView("index").addAllObjects(objects);
	}
	
	@GetMapping(value = "/login_success")
	public ModelAndView loginSuccess(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication(); 
		UserDetailsImpl userDetail = (UserDetailsImpl)auth.getPrincipal();
		Cookie cookies[] = request.getCookies();
		boolean rememberMe = false;
		
		Optional<User> user = userRepository.findByUsername(userDetail.getUsername());
		
		for(Cookie c : cookies) {
			if(c.getName().equals("remember-me")) {
				rememberMe = true;
			}
		}
		
		String jwt = jwtUtils.generateJwtToken(auth,rememberMe);
	    
	    HttpSession session = request.getSession(true);
	    
	    session.setAttribute("user", user.get());
	    session.setAttribute("jwt", jwt);

	    return new ModelAndView("redirect:/");
	}

	@GetMapping(path="/login_error")
	public ModelAndView loginError(HttpServletRequest request, HttpServletResponse response) {
		HashMap<String,Object> objects = new HashMap<>();
		Cookie cookies[] = request.getCookies();
		
		for(Cookie c : cookies) {
			if(c.getName().equals("last-username")) {
				objects.put("username", c.getValue());
			}
		}
		
		objects.put("error", true);
		
		return new ModelAndView("index").addAllObjects(objects);
	}
	
	@GetMapping(path="/logout_success")
	public ModelAndView logout(HttpServletRequest request) {
		request.getSession().invalidate();
		return new ModelAndView("redirect:/");
	}
}
