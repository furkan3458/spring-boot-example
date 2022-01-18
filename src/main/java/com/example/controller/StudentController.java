package com.example.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.StudentService;
import com.example.table.model.Page;
import com.example.table.model.PageArray;
import com.example.table.model.PagingRequest;
import com.example.validator.StudentCheckAllValidator;
import com.example.validator.StudentCheckIdValidator;
import com.example.validator.StudentValidator;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import com.example.model.Student;

@RestController
@RequestMapping(path="/obs/student")
@SecurityRequirement(name = "Bearer")
public class StudentController {
	private StudentService studentService;
	
	@Autowired
	public StudentController(StudentService studentService) {
		this.studentService = studentService;
	}
	
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HashMap<String,Object>> student(){
		
		return ResponseEntity.ok(studentService.getAll());
	}
	
	@GetMapping(path="/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HashMap<String,Object>> studentById(@PathVariable(value="id") Long id){
		
		return ResponseEntity.ok(studentService.getById(id));
	}
	
	@RequestMapping(path="/find", params = "name", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
	public ResponseEntity<HashMap<String,Object>> studentByName(@RequestParam("name") String name){
		
		return ResponseEntity.ok(studentService.getByName(name));
	}
	
	
	@RequestMapping(path="/findAll", params="name", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
	public ResponseEntity<HashMap<String,Object>> studentByNameAll(@RequestParam("name") String name, 
			@RequestParam(name = "sort", required = false, defaultValue = "id") String sort){
		
		Sort sortBy = Sort.by(sort);
		
		return ResponseEntity.ok(studentService.getByNameAll(name,sortBy));
	}
	
	@RequestMapping(path="/find", params = {"name","number"}, produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
	public ResponseEntity<HashMap<String,Object>> studentByNameAndNumber(@RequestParam("name") String name,
			@RequestParam("number") Integer number){
		
		return ResponseEntity.ok(studentService.getByNameAndNumber(name,number));
	}
	
	@RequestMapping(path="/findAll", params = {"name","number"}, produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
	public ResponseEntity<HashMap<String,Object>> studentByNameAndNumberAll(@RequestParam("name") String name,
			@RequestParam("number") Integer number,
			@RequestParam(name = "sort", required = false, defaultValue = "id") String sort){
		
		Sort sortBy = Sort.by(sort);
		
		return ResponseEntity.ok(studentService.getByNameAndNumberAll(name,number,sortBy));
	}
	
	@RequestMapping(path="/findAll", params = "number", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
	public ResponseEntity<HashMap<String,Object>> studentByNumber(@RequestParam("number") Integer number,
			@RequestParam(name = "sort", required = false, defaultValue = "id") String sort){
		
		Sort sortBy = Sort.by(sort);
		
		return ResponseEntity.ok(studentService.getByNumberAll(number,sortBy));
	}
	
	@RequestMapping(path="/search", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
	public ResponseEntity<HashMap<String,Object>> searchStudentByName(@RequestParam(value="name") String name, 
			@RequestParam(value = "sort", required = false, defaultValue = "id") String sort){
		
		Sort sortBy = Sort.by(sort);
		
		return ResponseEntity.ok(studentService.getByNameLikeAll(name,sortBy));
	}
	
	@PreAuthorize("hasRole(MODERATOR) or hasRole(ADMIN)")
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HashMap<String,Object>> studentUpdate(@RequestBody @Validated(value = StudentCheckAllValidator.class) Student student){
		
		return ResponseEntity.ok(studentService.update(student));
	}
	
	@PreAuthorize("hasRole(MODERATOR) or hasRole(ADMIN)")
	@PutMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HashMap<String,Object>> studentInsert(@RequestBody @Validated(value = StudentValidator.class) Student student){
		
		return ResponseEntity.ok(studentService.insert(student));
	}
	
	@PreAuthorize("hasRole('DB_ADMIN') or hasRole('SUPERADMIN')")
	@DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HashMap<String,Object>> studentDelete(@RequestBody @Validated(value = StudentCheckIdValidator.class) Student student){
		
		return ResponseEntity.ok(studentService.delete(student));
	}
	
	@PreAuthorize("hasRole('SUPERADMIN')")
	@DeleteMapping(path="/deleteAll", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HashMap<String,Object>> courseDeleteAll(){
		
		return ResponseEntity.ok(studentService.deleteAll());
	}
	
	@PostMapping(path="/page")
    public Page<Student> courseList(@RequestBody PagingRequest pagingRequest) {
        return studentService.getAll(pagingRequest);
    }

    @PostMapping("/array")
    public PageArray courseArray(@RequestBody PagingRequest pagingRequest) {
        return studentService.getAllArray(pagingRequest);
    }
	
}
