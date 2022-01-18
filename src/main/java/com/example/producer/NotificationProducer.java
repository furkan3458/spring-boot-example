package com.example.producer;

import java.util.LinkedHashMap;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.example.model.Notification;
import com.example.util.ERestReponseKeys;

@Service
@PropertySource("classpath:application.properties")
public class NotificationProducer {

	@Autowired
	RabbitTemplate rabbitTemplate;

    @Value("${app.rabbit.routing.name}")
    private String routingName;

    @Value("${app.rabbit.exchange.name}")
    private String exchangeName;

    public LinkedHashMap<ERestReponseKeys, Object> sendToQueue(Notification ntf) {
    	LinkedHashMap<ERestReponseKeys, Object> result = new LinkedHashMap<>();
    	
    	try {
    		rabbitTemplate.convertAndSend(exchangeName, routingName, ntf);
    		result.put(ERestReponseKeys.success, true);
    		result.put(ERestReponseKeys.message, "Notification has queued successfully.");
    		result.put(ERestReponseKeys.data, ntf);
    	}
    	catch(AmqpException ex){
    		result.put(ERestReponseKeys.success, false);
    		result.put(ERestReponseKeys.error, "NOTIFICATION_QUEUE_ERROR");
    		result.put(ERestReponseKeys.message, ex.getLocalizedMessage());
    	}
  
        return result;
    }
}
