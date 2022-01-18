package com.example.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import com.example.validator.TeacherCheckIdValidator;
import com.example.validator.TeacherValidator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table
@Entity
public class Teacher implements Serializable{
	private static final long serialVersionUID = -7003883309801624422L;
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Min(value = 1, message = "Teacher identity number cannot be null or min than 1.", groups = TeacherCheckIdValidator.class)
	private long id;
	
	@NotBlank(message = "Name cannot be blank.", groups = TeacherValidator.class)
	private String name;
	
	public Teacher(long id) {
		this.id = id;
	}
	
	public Teacher(String name) {
		this.name = name;
	}
	
	@Override
	public String toString(){
		return "Teacher [id=" + id + ", name=" + name + "]";
	}
}
