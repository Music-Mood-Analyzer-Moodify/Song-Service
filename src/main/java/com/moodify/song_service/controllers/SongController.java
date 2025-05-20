package com.moodify.song_service.controllers;

import com.moodify.song_service.messaging.Producer;
import com.moodify.song_service.models.Emotion;
import com.moodify.song_service.models.Song;
import com.moodify.song_service.services.SongService;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;

import java.util.UUID;
import java.util.Locale;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class SongController {

    private static final Logger logger = LoggerFactory.getLogger(SongController.class);
    private final Producer producer;
    private final Tracer tracer;
    private final SongService songService;

    @Autowired
    public SongController(
        Producer producer,
        OpenTelemetry openTelemetry,
        SongService songService
    ) {
        this.producer = producer;
        this.tracer = openTelemetry.getTracer("song-service:song-controller");
        this.songService = songService;
    }

    @GetMapping("/allSongs")
    public ResponseEntity<?> allSongs(
        @RequestParam int page
    ) {
        Span span = tracer.spanBuilder("allSongs").startSpan();
        try (var scope = span.makeCurrent()) {
            logger.info("Received GET request to /allSongs with page={}", page);
            span.setAttribute("http.method", "GET");
            span.setAttribute("http.path", "/allSongs");
            span.setAttribute("request.page", page);
            Page<Song> songs = songService.getSongs(page);
            songs.forEach(song -> song.setEmotion(Emotion.UNKNOWN));
            logger.info("Returning {} songs for page {}", songs.getNumberOfElements(), page);
            span.setAttribute("response.size", songs.getNumberOfElements());
            span.setAttribute("response.status", "OK");
            return ResponseEntity.ok(songs);
        } catch(Exception e) {
            span.setAttribute("error", true);
            span.recordException(new RuntimeException(e.getMessage()));
            span.setAttribute("response.status", "INTERNAL_SERVER_ERROR");
            logger.error("Error occurred while processing request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        } finally {
            span.end();
        }
    }

    @GetMapping("/allSongsAuthenticated")
    public ResponseEntity<?> allSongsAuthenticated(
        @RequestParam int page
    ) {
        Span span = tracer.spanBuilder("allSongsAuthenticated").startSpan();
        try (var scope = span.makeCurrent()) {
            logger.info("Received GET request to /allSongsAuthenticated with page={}", page);
            span.setAttribute("http.method", "GET");
            span.setAttribute("http.path", "/allSongsAuthenticated");
            span.setAttribute("request.page", page);
            Page<Song> songs = songService.getSongs(page);
            logger.info("Returning {} songs for page {}", songs.getNumberOfElements(), page);
            span.setAttribute("response.size", songs.getNumberOfElements());
            return ResponseEntity.ok(songs);
        } catch(Exception e) {
            span.setAttribute("error", true);
            span.recordException(new RuntimeException(e.getMessage()));
            span.setAttribute("response.status", "INTERNAL_SERVER_ERROR");
            logger.error("Error occurred while processing request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        } finally {
            span.end();
        }
    }

    @GetMapping("/songsWithEmotion")
    public ResponseEntity<?> songsWithEmotion(
        @RequestParam int page,
        @RequestParam String emotion
    ) {
        Span span = tracer.spanBuilder("songsWithEmotion").startSpan();
        try (var scope = span.makeCurrent()) {
            logger.info("Received GET request to /songsWithEmotion with page={} and emotion={}", page, emotion);
            span.setAttribute("http.method", "GET");
            span.setAttribute("http.path", "/songsWithEmotion");
            span.setAttribute("request.page", page);
            span.setAttribute("request.emotion", emotion == null ? "null" : emotion);
    
            if (emotion == null) {
                return ResponseEntity.badRequest().body("Emotion cannot be null");
            }
            try {
                Emotion emotionEnum = Emotion.valueOf(emotion.toUpperCase(Locale.ROOT));
                Page<Song> songs = songService.getSongs(page, emotionEnum);
                logger.info("Returning {} songs for page {} and emotion {}", songs.getNumberOfElements(), page, emotionEnum);
                span.setAttribute("response.size", songs.getNumberOfElements());
                return ResponseEntity.ok(songs);
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid emotion received: {}", emotion);
                span.setAttribute("error", true);
                span.recordException(e);
                return ResponseEntity.badRequest().body("Invalid emotion: " + emotion);
            }
        } catch(Exception e) {
            span.setAttribute("error", true);
            span.recordException(new RuntimeException(e.getMessage()));
            span.setAttribute("response.status", "INTERNAL_SERVER_ERROR");
            logger.error("Error occurred while processing request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        } finally {
            span.end();
        }
    }
    
    @GetMapping("song/{id}")
    public Song getSong(@PathVariable UUID id) throws ResponseStatusException {
        Span span = tracer.spanBuilder("getSong").startSpan();
        try (var scope = span.makeCurrent()) {
            logger.info("Received GET request to /song/{}", id);
            span.setAttribute("http.method", "GET");
            span.setAttribute("http.path", "/song/" + id);
            span.setAttribute("song.id", id.toString());
    
            Optional<Song> song = songService.getSong(id);
            if (song.isEmpty()) {
                logger.warn("Song with id {} does not exist", id);
                span.setAttribute("error", true);
                span.setAttribute("song.found", false);
                throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Song with id " + id + " does not exist"
                );
            }
            logger.info("Returning song with id {}", id);
            span.setAttribute("song.found", true);
            return song.get();
        } finally {
            span.end();
        }
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

            producer.produceMessageToSongPredictedQueue(message);

            String response = "Message sent";
            logger.info("Sending response: {}", response);
            span.setAttribute("http.response", response);

            return response;
        } catch (Exception e) {
            logger.error("Failed to process request for message: {}", message, e);
            span.recordException(e);
            throw e; // Re-throw for proper HTTP error handling
        } finally {
            System.out.println("Span ended. Span contents: " + span.toString());
            span.end();
        }
    }
}
