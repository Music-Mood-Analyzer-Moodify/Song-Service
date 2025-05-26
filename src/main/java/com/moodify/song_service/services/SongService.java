package com.moodify.song_service.services;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.moodify.song_service.models.Emotion;
import com.moodify.song_service.models.Song;
import com.moodify.song_service.repositories.SongRepository;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;

@Service
public class SongService {
    private static final Logger logger = LoggerFactory.getLogger(SongService.class);
    private static final int pageSize = 25;
    private final SongRepository songRepository;
    private final Tracer tracer;

    @Autowired
    public SongService(SongRepository songRepository, OpenTelemetry openTelemetry) {
        this.songRepository = songRepository;
        this.tracer = openTelemetry.getTracer("song-service:song-service");
    }

    public Page<Song> getSongs(int page) {
        Span span = tracer.spanBuilder("getSongs").startSpan();
        try (var scope = span.makeCurrent()) {
            logger.info("Fetching songs for page {}", page);
            span.setAttribute("service.method", "getSongs");
            span.setAttribute("request.page", page);

            if (page < 0) {
                logger.warn("Invalid page number: {}", page);
                span.setAttribute("error", true);
                throw new IllegalArgumentException("Page number must be non-negative");
            }
            PageRequest pageRequest = PageRequest.of(page, pageSize);
            Page<Song> result = songRepository.findAll(pageRequest);
            logger.info("Fetched {} songs for page {}", result.getNumberOfElements(), page);
            span.setAttribute("response.size", result.getNumberOfElements());
            return result;
        } finally {
            span.end();
        }
    }

    public Page<Song> getSongs(int page, Emotion emotion) {
        Span span = tracer.spanBuilder("getSongsByEmotion").startSpan();
        try (var scope = span.makeCurrent()) {
            logger.info("Fetching songs for page {} with emotion {}", page, emotion);
            span.setAttribute("service.method", "getSongsByEmotion");
            span.setAttribute("request.page", page);
            span.setAttribute("request.emotion", emotion.toString());

            if (page < 0) {
                logger.warn("Invalid page number: {}", page);
                span.setAttribute("error", true);
                throw new IllegalArgumentException("Page number must be greater than 0");
            }
            PageRequest pageRequest = PageRequest.of(page, pageSize);
            Page<Song> result = songRepository.findAllByEmotion(emotion, pageRequest);
            logger.info("Fetched {} songs for page {} and emotion {}", result.getNumberOfElements(), page, emotion);
            span.setAttribute("response.size", result.getNumberOfElements());
            return result;
        } finally {
            span.end();
        }
    }

    public Optional<Song> getSong(UUID id) {
        Span span = tracer.spanBuilder("getSongById").startSpan();
        try (var scope = span.makeCurrent()) {
            logger.info("Fetching song with id {}", id);
            span.setAttribute("service.method", "getSongById");
            span.setAttribute("song.id", id.toString());

            Optional<Song> result = songRepository.findById(id);
            span.setAttribute("song.found", result.isPresent());
            if (result.isPresent()) {
                logger.info("Song found with id {}", id);
            } else {
                logger.warn("Song not found with id {}", id);
            }
            return result;
        } finally {
            span.end();
        }
    }

    public void addSong(Song song) {
        Span span = tracer.spanBuilder("addSong").startSpan();
        try (var scope = span.makeCurrent()) {
            logger.info("Adding song with spotify id {}", song.getSpotifyId());
            span.setAttribute("service.method", "addSong");
            span.setAttribute("song.spotifyId", song.getSpotifyId());

            boolean exists = songRepository.existsBySpotifyId(song.getSpotifyId());
            if (exists) {
                logger.info("Song with spotify id {} already exists", song.getSpotifyId());
                span.setAttribute("song.exists", true);
                return;
            }

            songRepository.save(song);
            logger.info("Song saved with spotify id {}", song.getSpotifyId());
            span.setAttribute("song.saved", true);
        } finally {
            span.end();
        }
    }

    public void deleteSong(UUID id) {
        Span span = tracer.spanBuilder("deleteSong").startSpan();
        try (var scope = span.makeCurrent()) {
            logger.info("Deleting song with id {}", id);
            span.setAttribute("service.method", "deleteSong");
            span.setAttribute("song.id", id.toString());

            boolean exists = songRepository.existsById(id);
            if (!exists) {
                logger.warn("Song with id {} does not exist", id);
                span.setAttribute("song.exists", false);
                throw new IllegalStateException("Song with id " + id + " does not exist");
            }

            songRepository.deleteById(id);
            logger.info("Song deleted with id {}", id);
            span.setAttribute("song.deleted", true);
        } finally {
            span.end();
        }
    }
}