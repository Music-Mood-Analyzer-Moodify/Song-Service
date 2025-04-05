package com.moodify.song_service.messaging;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Producer {

    private static final Logger logger = LoggerFactory.getLogger(Producer.class);
    private final RabbitTemplate rabbitTemplate;
    private final Tracer tracer;

    @Autowired
    public Producer(RabbitTemplate rabbitTemplate, OpenTelemetry openTelemetry) {
        this.rabbitTemplate = rabbitTemplate;
        this.tracer = openTelemetry.getTracer("song-service:producer");
    }

    public void sendMessageToSongPredictedQueue(String message) {
        Span span = tracer.spanBuilder("sendToSongPredictedQueue")
                .setParent(Context.current())
                .startSpan();
                
        try (var scope = span.makeCurrent()) {
            logger.info("Sending message to song_predicted_queue: {}", message);
            span.setAttribute("message.content", message);
            span.setAttribute("queue.name", "song_predicted_queue");

            rabbitTemplate.convertAndSend("song_predicted_queue", message);
            
            logger.info("Successfully sent message to song_predicted_queue: {}", message);
            span.setAttribute("message.status", "sent");
        } catch (Exception e) {
            logger.error("Failed to send message to song_predicted_queue: {}", message, e);
            span.recordException(e);
            span.setAttribute("message.status", "failed");
        } finally {
            span.end();
        }
    }
}
