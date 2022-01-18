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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Course;
import com.example.model.Subject;
import com.example.model.Teacher;
import com.example.service.CourseService;
import com.example.table.model.Page;
import com.example.table.model.PageArray;
import com.example.table.model.PagingRequest;
import com.example.validator.CourseCheckAllValidator;
import com.example.validator.CourseCheckIdValidator;
import com.example.validator.CourseValidator;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping(path="/obs/course")
@SecurityRequirement(name = "Bearer")
public class CourseController {

	private CourseService courseService;
	
	@Autowired
	public CourseController(CourseService courseService) {
		this.courseService = courseService;
	}	
	
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HashMap<String,Object>> course(){
		
		return ResponseEntity.ok(courseService.getAll());
	}
	
	@GetMapping(path="/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HashMap<String,Object>> courseById(@PathVariable(value="id") Long id){
		
		return ResponseEntity.ok(courseService.getById(id));
	}
	
	@RequestMapping(path="/find", params = "teacher",  produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
	public ResponseEntity<HashMap<String,Object>> courseByTeacher(@RequestParam("teacher") Long id) {
		
		Teacher teacher = new Teacher(id);
		
		return ResponseEntity.ok(courseService.getByTeacher(teacher));
	}
	
	@RequestMapping(path="/find", params = "subject", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
	public ResponseEntity<HashMap<String,Object>> courseBySubject(@RequestParam("subject") Long id) {
		
		Subject subject = new Subject(id);
		
		return ResponseEntity.ok(courseService.getBySubject(subject));
	}
	
	@RequestMapping(path="/find", params = {"teacher","subject"}, produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
	public ResponseEntity<HashMap<String,Object>> courseByTeacherAndSubject(@RequestParam("teacher") Long teacher_id,@RequestParam("subject") Long subject_id) {
		
		Teacher teacher = new Teacher(teacher_id);
		Subject subject = new Subject(subject_id);
		
		return ResponseEntity.ok(courseService.getByTeacherAndSubject(teacher,subject));
	}
	
	@RequestMapping(path="/findAll", params = "teacher",  produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
	public ResponseEntity<HashMap<String,Object>> courseByTeacherAll(@RequestParam("teacher") Long id) {
		
		Teacher teacher = new Teacher(id);
		
		return ResponseEntity.ok(courseService.getByTeacherAll(teacher));
	}
	
	@RequestMapping(path="/findAll", params = "subject", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
	public ResponseEntity<HashMap<String,Object>> courseBySubjectAll(@RequestParam("subject") Long id) {
		
		Subject teacher = new Subject(id);
		
		return ResponseEntity.ok(courseService.getBySubjectAll(teacher));
	}
	
	@RequestMapping(path="/findAll", params = {"teacher","subject"}, produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
	public ResponseEntity<HashMap<String,Object>> courseByTeacherAndSubjectAll(@RequestParam("teacher") Long teacher_id,@RequestParam("subject") Long subject_id) {
		
		Teacher teacher = new Teacher(teacher_id);
		Subject subject = new Subject(subject_id);
		
		return ResponseEntity.ok(courseService.getByTeacherAndSubject(teacher,subject));
	}
	
	@PreAuthorize("hasRole(MODERATOR) or hasRole(ADMIN) or hasRole(TEACHER)")
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HashMap<String,Object>> courseUpdate(@RequestBody @Validated(value = CourseCheckAllValidator.class) Course course){
		
		return ResponseEntity.ok(courseService.update(course));
	}
	
	@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('TEACHER')")
	@PutMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HashMap<String,Object>> courseInsert(@RequestBody @Validated(value = CourseValidator.class) Course course){
		
		return ResponseEntity.ok(courseService.insert(course));
	}
	
	@PreAuthorize("hasRole('DB_ADMIN') or hasRole('SUPERADMIN')")
	@DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HashMap<String,Object>> courseDelete(@RequestBody @Validated(value = CourseCheckIdValidator.class) Course course){
		
		return ResponseEntity.ok(courseService.delete(course));
	}
	
	@PreAuthorize("hasRole('SUPERADMIN')")
	@DeleteMapping(path="/deleteAll", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HashMap<String,Object>> courseDeleteAll(){
		
		return ResponseEntity.ok(courseService.deleteAll());
	}
	
	@PostMapping(path="/page")
    public Page<Course> courseList(@RequestBody PagingRequest pagingRequest) {
        return courseService.getAll(pagingRequest);
    }

    @PostMapping("/array")
    public PageArray courseArray(@RequestBody PagingRequest pagingRequest) {
        return courseService.getAllArray(pagingRequest);
    }
}
