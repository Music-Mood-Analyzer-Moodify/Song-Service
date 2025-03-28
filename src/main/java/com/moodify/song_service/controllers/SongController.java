package com.moodify.song_service.controllers;

import com.moodify.song_service.messaging.Producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SongController {

    private final Producer producer;

    @Autowired
    public SongController(Producer producer) {
        this.producer = producer;
    }

    @PostMapping("/message/{message}")
    public String sendMessage(@PathVariable String message) {
        producer.sendMessageToSongPredictedQueue(message);
        return "Message sent";
    }
}
