package com.example.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.TeacherService;
import com.example.table.model.Page;
import com.example.table.model.PageArray;
import com.example.table.model.PagingRequest;
import com.example.validator.TeacherCheckAllValidator;
import com.example.validator.TeacherCheckIdValidator;
import com.example.validator.TeacherValidator;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import com.example.model.Teacher;

@RestController
@RequestMapping(path="/obs/teacher")
@SecurityRequirement(name = "Bearer")
public class TeacherController {
	
	private TeacherService teacherService;

	@Autowired
	public TeacherController(TeacherService teacherService) {
		this.teacherService = teacherService;
	}
	
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HashMap<String,Object>> teacher(){
		
		return ResponseEntity.ok(teacherService.getAll());
	}
	
	@GetMapping(path="/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HashMap<String,Object>> teacherById(@PathVariable(value="id") Long id){
		
		return ResponseEntity.ok(teacherService.getById(id));
	}
	
	@PreAuthorize("hasRole(MODERATOR) or hasRole(ADMIN) or hasRole(TEACHER)")
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HashMap<String,Object>> teacherUpdate(@RequestBody @Validated(value = TeacherCheckAllValidator.class) Teacher teacher){
		
		return ResponseEntity.ok(teacherService.update(teacher));
	}
	
	@PreAuthorize("hasRole(MODERATOR) or hasRole(ADMIN) or hasRole(TEACHER)")
	@PutMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HashMap<String,Object>> teacherInsert(@RequestBody @Validated(value = TeacherValidator.class) Teacher teacher){
	
		return ResponseEntity.ok(teacherService.insert(teacher));
	}
	
	@PreAuthorize("hasRole('DB_ADMIN') or hasRole('SUPERADMIN')")
	@DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HashMap<String,Object>> teacherDelete(@RequestBody @Validated(value = TeacherCheckIdValidator.class) Teacher teacher){
		
		return ResponseEntity.ok(teacherService.delete(teacher));
	}
	
	@PreAuthorize("hasRole('SUPERADMIN')")
	@DeleteMapping(path="/deleteAll", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HashMap<String,Object>> courseDeleteAll(){
		
		return ResponseEntity.ok(teacherService.deleteAll());
	}
	
	@PostMapping(path="/page")
    public Page<Teacher> courseList(@RequestBody PagingRequest pagingRequest) {
        return teacherService.getAll(pagingRequest);
    }

    @PostMapping("/array")
    public PageArray courseArray(@RequestBody PagingRequest pagingRequest) {
        return teacherService.getAllArray(pagingRequest);
    }
}
