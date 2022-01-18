package com.example.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="SPRING_SESSION_ATTRIBUTES")
@Entity
public class SpringSessionAttributes implements Serializable{
	private static final long serialVersionUID = 3820591497632834481L;

	@Id
	@Column(name="SESSION_PRIMARY_ID",length=191)
	private String sessionPrimaryId;
	
	@Id
	@Column(name="ATTRIBUTE_NAME",length=191)
	private String attributeName;
	
	@Column(name="ATTRIBUTE_BYTES",length=100000)
	@Lob
	private byte[] attributeBytes;
}
