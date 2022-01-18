package com.example.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.model.Notification;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationListener {

	@Autowired
    SimpMessagingTemplate template;
	
	@RabbitListener(queues = "${app.rabbit.queue.name}")
    public void handleOrder(Notification ntf) {
        try {
        	template.convertAndSend("/notification/subscribe", ntf);
        	log.info("Notification queued -> message: {}",ntf.getMessage());
        }catch (Exception ex) {
            System.err.println("handleOrder Error : " + ex);
        }
    }
}
