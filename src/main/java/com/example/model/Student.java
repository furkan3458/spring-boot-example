package com.example.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import com.example.validator.StudentCheckIdValidator;
import com.example.validator.StudentValidator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table
@Entity
public class Student implements Serializable{

	private static final long serialVersionUID = 2135571011903146220L;
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Min(value = 1, message = "Student identify number cannot be null or min than 1.", groups = StudentCheckIdValidator.class)
	private long id;
	
	@Min(value = 1, message = "Student number cannot be minimum than 1.", groups = StudentValidator.class)
	private int number;
	
	@NotBlank(message = "Name cannot be blank.", groups = StudentValidator.class)
	private String name;
	
	public Student(long id) {
		this.id = id;
	}
	
	public Student(int number, String name) {
		this.number = number;
		this.name = name;
	}
	
	@Override
	public String toString() {
		return "Student [name="+name+", number="+number+"]";
	}
}
