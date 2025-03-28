package com.moodify.song_service.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Consumer {
    @RabbitListener(queues = "check_song_queue")
    public void listen(String message) {
        System.out.println("Message read from check_song_queue: " + message);
    }
}
