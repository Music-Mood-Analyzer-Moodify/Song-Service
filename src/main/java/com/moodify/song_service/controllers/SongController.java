package com.moodify.song_service.controllers;

import com.moodify.song_service.messaging.Producer;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SongController {

    private static final Logger logger = LoggerFactory.getLogger(SongController.class);
    private final Producer producer;
    private final Tracer tracer;

    @Autowired
    public SongController(Producer producer, OpenTelemetry openTelemetry) {
        this.producer = producer;
        this.tracer = openTelemetry.getTracer("song-service:song-controller");
    }

    @PostMapping("/message/{message}")
    public String sendMessage(@PathVariable String message) {
        Span span = tracer.spanBuilder("sendMessage").startSpan();
        try (var scope = span.makeCurrent()) {
            logger.info("Received POST request to /message/{} with message: {}", message, message);
            span.setAttribute("http.method", "POST");
            span.setAttribute("http.path", "/message/" + message);
            span.setAttribute("message.content", message);
            span.setAttribute("message.status", "received");

            producer.sendMessageToSongPredictedQueue(message);

            String response = "Message sent";
            logger.info("Sending response: {}", response);
            span.setAttribute("http.response", response);

            return response;
        } catch (Exception e) {
            logger.error("Failed to process request for message: {}", message, e);
            span.recordException(e);
            throw e; // Re-throw for proper HTTP error handling
        } finally {
            span.end();
        }
    }
}
