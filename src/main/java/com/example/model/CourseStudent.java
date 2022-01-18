package com.example.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.example.validator.CourseStudentCheckIdValidator;
import com.example.validator.CourseStudentValidator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Table
@Entity
public class CourseStudent implements Serializable {
	
	private static final long serialVersionUID = 4224146285615607371L;
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Min(value = 1, message = "Course assign identity number cannot null or min than 1.", groups = CourseStudentCheckIdValidator.class)
	private long id;
	
	@Valid 
	@ManyToOne(cascade = CascadeType.DETACH)
	@NotNull(groups = CourseStudentValidator.class, message = "Student cannot be empty.")
	private Student student;
	
	@Valid 
	@ManyToOne(cascade = CascadeType.DETACH)
	@NotNull(groups = CourseStudentValidator.class, message = "Course cannot be empty.")
	private Course course;
	
	public CourseStudent(Course course, Student student) {
		this.course = course;
		this.student = student;
	}
	
	@JsonIgnore
	public Long getStudentId() {
		return student.getId();
	}
	
	@JsonIgnore
	public String getStudentName(){
		return student.getName();
	}
	
	@JsonIgnore
	public int getStudentNumber() {
		return student.getNumber();
	}
	
	@JsonIgnore
	public Long getCourseId() {
		return course.getId();
	}
	
	@JsonIgnore
	public String getTeacherName() {
		return course.getTeacherName();
	}
	
	@JsonIgnore
	public String getSubjectName() {
		return course.getSubjectName();
	}
}
