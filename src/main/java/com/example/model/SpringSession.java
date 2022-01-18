package com.example.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="SPRING_SESSION")
public class SpringSession implements Serializable {
	private static final long serialVersionUID = -2344338279277649539L;

	@Id
	@Column(name="PRIMARY_ID", length=191)
	private String primaryId;
	
	@Column(name="SESSION_ID")
	private String sessionId;
	
	@Column(name="CREATION_TIME")
	private Long creationTime;
	
	@Column(name="LAST_ACCESS_TIME")
	private Long lastAccessTime;
	
	@Column(name="MAX_INACTIVE_INTERVAL")
	private Integer maxInactiveInternal;
	
	@Column(name="EXPIRY_TIME")
	private Long expiryTime;
	
	@Column(name="PRINCIPAL_NAME")
	private String principalName;
}
