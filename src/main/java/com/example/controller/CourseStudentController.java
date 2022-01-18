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

import com.example.model.CourseStudent;
import com.example.service.CourseStudentService;
import com.example.table.model.Page;
import com.example.table.model.PageArray;
import com.example.table.model.PagingRequest;
import com.example.validator.CourseStudentCheckAllValidator;
import com.example.validator.CourseStudentCheckIdValidator;
import com.example.validator.CourseStudentValidator;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping(path="/obs/course_student")
@SecurityRequirement(name = "Bearer")
public class CourseStudentController {

	private CourseStudentService courseStudentService;
	
	@Autowired
	public CourseStudentController(CourseStudentService courseStudentService) {
		this.courseStudentService = courseStudentService;
	}
	
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HashMap<String,Object>> courseStudent(){
		
		return ResponseEntity.ok(courseStudentService.getAll());
	}
	
	@GetMapping(path="/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HashMap<String,Object>> courseStudentById(@PathVariable(value="id") Long id){
		
		return ResponseEntity.ok(courseStudentService.getById(id));
	}
	
	@PreAuthorize("hasRole(MODERATOR) or hasRole(ADMIN) or hasRole(TEACHER)")
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HashMap<String,Object>> courseStudentUpdate(@RequestBody @Validated(value = CourseStudentCheckAllValidator.class) CourseStudent courseStudent){
		
		return ResponseEntity.ok(courseStudentService.update(courseStudent));
	}
	
	@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('TEACHER')")
	@PutMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HashMap<String,Object>> courseStudentInsert(@RequestBody @Validated(value = CourseStudentValidator.class) CourseStudent courseStudent){
		
		return ResponseEntity.ok(courseStudentService.insert(courseStudent));
	}
	
	@PreAuthorize("hasRole('DB_ADMIN') or hasRole('SUPERADMIN')")
	@DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HashMap<String,Object>> courseStudentDelete(@RequestBody @Validated(value = CourseStudentCheckIdValidator.class) CourseStudent courseStudent){
		
		return ResponseEntity.ok(courseStudentService.delete(courseStudent));
	}
	
	@PreAuthorize("hasRole('SUPERADMIN')")
	@DeleteMapping(path="/deleteAll", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HashMap<String,Object>> courseDeleteAll(){
		
		return ResponseEntity.ok(courseStudentService.deleteAll());
	}
	
	@PostMapping(path="/page")
    public Page<CourseStudent> courseList(@RequestBody PagingRequest pagingRequest) {
        return courseStudentService.getAll(pagingRequest);
    }

    @PostMapping("/array")
    public PageArray courseArray(@RequestBody PagingRequest pagingRequest) {
        return courseStudentService.getAllArray(pagingRequest);
    }
}
