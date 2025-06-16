package com.moodify.song_service.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moodify.song_service.models.Emotion;
import com.moodify.song_service.models.Song;
import com.moodify.song_service.services.SongService;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapPropagator;

@Configuration
public class Consumer {
    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);
    private final Tracer tracer;
    private final Meter meter;
    private final LongCounter messagesConsumedCounter;
    private final TextMapPropagator propagator;
    private final ObjectMapper objectMapper;
    private final SongService songService;

    @Autowired
    public Consumer(OpenTelemetry openTelemetry, ObjectMapper objectMapper, SongService songService) {
        this.tracer = openTelemetry.getTracer("song-service:consumer");
        this.meter = openTelemetry.getMeter("song-service:consumer");
        this.messagesConsumedCounter = meter.counterBuilder("messages_consumed_total")
            .setDescription("Total messages consumed from RabbitMQ")
            .build();
        this.propagator = openTelemetry.getPropagators().getTextMapPropagator();
        this.objectMapper = objectMapper;
        this.songService = songService;
    }

    @RabbitListener(
        queuesToDeclare = @Queue(
            name = "check_song_queue",
            durable = "false"
        )
    )
    public void listenCheckSongQueue(Message message) {
        Context extractedContext = propagator.extract(Context.current(), message, new RabbitMQHeaderGetter());
        String messageBody = new String(message.getBody());
        Span span = tracer.spanBuilder("listenCheckSongQueue")
            .setParent(extractedContext)
            .startSpan();
                
        try (var scope = span.makeCurrent()) {
            logger.info("Consuming message from check_song_queue: {}", messageBody);
            span.setAttribute("message.content", messageBody);
            span.setAttribute("queue.name", "check_song_queue");
            messagesConsumedCounter.add(
                1, 
                    Attributes.of(
                        AttributeKey.stringKey("queue.name"), "check_song_queue"
                    )
            );

            // Do something with the message
            
            logger.info("Successfully processed message from check_song_queue: {}", messageBody);
            span.setAttribute("message.status", "processed");
        } catch (Exception e) {
            logger.error("Failed to process message from check_song_queue: {}", messageBody, e);
            span.recordException(e);
            span.setAttribute("message.status", "failed");
        } finally {
            System.out.println("Span ended. Span contents: " + span.toString());
            span.end();
        }
    }

    @RabbitListener(
        queuesToDeclare = @Queue(
            name = "send_song_queue",
            durable = "false"
        )
    )
    public void listenSendSongQueue(Message message) {
        Context extractedContext = propagator.extract(Context.current(), message, new RabbitMQHeaderGetter());
        String messageBody = new String(message.getBody());
        Span span = tracer.spanBuilder("listenSendSongQueue")
            .setParent(extractedContext)
            .startSpan();
                
        try (var scope = span.makeCurrent()) {
            logger.info("Consuming message from send_song_queue: {}", messageBody);
            span.setAttribute("message.content", messageBody);
            span.setAttribute("queue.name", "send_song_queue");
            messagesConsumedCounter.add(
                1, 
                Attributes.of(
                    AttributeKey.stringKey("queue.name"), "send_song_queue"
                )
            );

            Song song = objectMapper.readValue(messageBody, Song.class);
            song.setEmotion(Emotion.randomEmotion());
            songService.addSong(song);
            
            logger.info("Successfully processed message from send_song_queue: {}", messageBody);
            span.setAttribute("message.status", "processed");
        } catch (Exception e) {
            logger.error("Failed to process message from send_song_queue: {}", messageBody, e);
            span.recordException(e);
            span.setAttribute("message.status", "failed");
        } finally {
            logger.info("Span ended. Span contents: {}", span.toString());
            span.end();
        }
    }

    private static class RabbitMQHeaderGetter implements TextMapGetter<Message> {
        @Override
        public Iterable<String> keys(Message message) {
            return message.getMessageProperties().getHeaders().keySet();
        }

        @Override
        public String get(Message message, String key) {
            if (message == null) {
                return null;
            }
            Object value = message.getMessageProperties().getHeaders().get(key);
            return value != null ? value.toString() : null;
        }
    }
}
