package com.moodify.song_service.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Producer {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public Producer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessageToSongPredictedQueue(String message) {
        rabbitTemplate.convertAndSend("song_predicted_queue", message);
        System.out.println("Sent '" + message + "' to 'song_predicted_queue'");
    }
}
