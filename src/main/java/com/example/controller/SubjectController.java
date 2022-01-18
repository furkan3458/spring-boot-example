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

import com.example.service.SubjectService;
import com.example.table.model.Page;
import com.example.table.model.PageArray;
import com.example.table.model.PagingRequest;
import com.example.validator.SubjectCheckAllValidator;
import com.example.validator.SubjectCheckIdValidator;
import com.example.validator.SubjectValidator;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import com.example.model.Subject;

@RestController
@RequestMapping(path="/obs/subject")
@SecurityRequirement(name = "Bearer")
public class SubjectController {

	private SubjectService subjectService;
	
	@Autowired
	public SubjectController(SubjectService subjectService) {
		this.subjectService = subjectService;
	}
	
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HashMap<String,Object>> subject(){
		
		return ResponseEntity.ok(subjectService.getAll());
	}
	
	@GetMapping(path="/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HashMap<String,Object>> subjectById(@PathVariable(value="id") Long id){
		
		return ResponseEntity.ok(subjectService.getById(id));
	}
	
	@PreAuthorize("hasRole(MODERATOR) or hasRole(ADMIN) or hasRole(TEACHER)")
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HashMap<String,Object>> subjectUpdate(@RequestBody @Validated(value = SubjectCheckAllValidator.class) Subject subject){
		
		return ResponseEntity.ok(subjectService.update(subject));
	}
	
	@PreAuthorize("hasRole(MODERATOR) or hasRole(ADMIN) or hasRole(TEACHER)")
	@PutMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HashMap<String,Object>> subjectInsert(@RequestBody @Validated(value = SubjectValidator.class) Subject subject){
		
		return ResponseEntity.ok(subjectService.insert(subject));
	}
	
	@PreAuthorize("hasRole('DB_ADMIN') or hasRole('SUPERADMIN')")
	@DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HashMap<String,Object>> subjectDelete(@RequestBody @Validated(value = SubjectCheckIdValidator.class) Subject subject){

		return ResponseEntity.ok(subjectService.delete(subject));
	}
	
	@PreAuthorize("hasRole('SUPERADMIN')")
	@DeleteMapping(path="/deleteAll", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HashMap<String,Object>> courseDeleteAll(){
		
		return ResponseEntity.ok(subjectService.deleteAll());
	}
	
	
	@PostMapping(path="/page")
    public Page<Subject> courseList(@RequestBody PagingRequest pagingRequest) {
        return subjectService.getAll(pagingRequest);
    }

    @PostMapping("/array")
    public PageArray courseArray(@RequestBody PagingRequest pagingRequest) {
        return subjectService.getAllArray(pagingRequest);
    }
}
