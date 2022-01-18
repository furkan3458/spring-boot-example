package com.example.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.example.validator.CourseCheckIdValidator;
import com.example.validator.CourseValidator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table
@Entity
public class Course implements Serializable {
	private static final long serialVersionUID = 6026995156858563560L;
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Min(value = 1, message = "Course identity number cannot be null or min than 1.", groups = CourseCheckIdValidator.class)
	private long id;
	
	@Valid @ManyToOne(cascade = CascadeType.MERGE) 
	@NotNull(groups = CourseValidator.class, message = "Teacher id cannot be empty.")
	private Teacher teacher;
	
	@Valid @ManyToOne(cascade = CascadeType.MERGE)
	@NotNull(groups = CourseValidator.class, message = "Subject id cannot be empty.")
	private Subject subject;
	
	public Course(long id) {
		this.id = id;
	}
	
	public Course(Teacher teacher,Subject subject) {
		this.teacher = teacher;
		this.subject = subject;
	}
	
	@JsonIgnore
	public String getTeacherName() {
		return teacher.getName();
	}
	
	@JsonIgnore
	public String getSubjectName() {
		return subject.getSubject();
	}
	
	@Override
	public String toString(){
		return "";
	}
}
