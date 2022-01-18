package com.example.model;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.http.HttpStatus;

import com.example.util.ErrorHandleType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Error {

	private HttpStatus httpStatus;
	
	private ErrorHandleType error;

	private String message;
	
	private String details;
	
    private LocalDate date;
    
    private LocalTime time;
}
