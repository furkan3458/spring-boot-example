package com.example.model;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Notification implements Serializable{
	private static final long serialVersionUID = 1379094291458273907L;
	
	private String message;
	private String date;
	
	public Notification() {
		this.date = LocalDate.now().toString();
	}
	
	@Override
	public String toString() {
		return "Notification [message="+message+", date="+date+"]";
	}
}
