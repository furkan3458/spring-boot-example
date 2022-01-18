package com.example.controller;

import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Notification;
import com.example.producer.NotificationProducer;
import com.example.util.ERestReponseKeys;

@RestController
@RequestMapping(path="/api/notification")
public class NotificationController {

	@Autowired
	NotificationProducer ntfProducer;
	
	@PostMapping(consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LinkedHashMap<ERestReponseKeys, Object>> notification(@RequestBody Notification notification){	
		return ResponseEntity.ok(ntfProducer.sendToQueue(notification));
	}
	
}
