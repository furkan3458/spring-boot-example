package com.example.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import com.example.validator.SubjectCheckIdValidator;
import com.example.validator.SubjectValidator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table
@Entity
public class Subject implements Serializable{
	
	private static final long serialVersionUID = 5725626683169098747L;
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Min(value = 1, message = "Subject identity number cannot be min than 1.", groups = SubjectCheckIdValidator.class)
	private long id;
	
	@NotBlank(message = "Subject cannot be blank.", groups = SubjectValidator.class)
	private String subject;
	
	public Subject(long id) {
		this.id = id;
	}
	
	public Subject(String subject) {
		this.subject = subject;
	}
	
	@Override
	public String toString() {
		return "Subject [id="+id+", subject="+subject+"]";
	}
}
