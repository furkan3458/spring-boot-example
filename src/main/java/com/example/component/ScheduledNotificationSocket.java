package com.example.component;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.model.Notification;

@Component
@EnableScheduling
public class ScheduledNotificationSocket {

	@Autowired
    SimpMessagingTemplate template;
	
    @Scheduled(fixedDelay = 3000)
    public void sendAdhocMessages() {
        template.convertAndSend("/scheduled/subscribe", new Notification("test",LocalDate.now().toString()));
    }
}
