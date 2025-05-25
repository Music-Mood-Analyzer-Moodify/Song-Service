package com.moodify.song_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.moodify.song_service.models.Emotion;
import com.moodify.song_service.models.Song;
import com.moodify.song_service.repositories.SongRepository;
import com.moodify.song_service.services.SongService;

import io.opentelemetry.api.OpenTelemetry;

public class SongServiceTest {
    private AutoCloseable closeable;
    @Mock
    private SongRepository songRepository;
    private SongService songService;

    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        songService = new SongService(songRepository, OpenTelemetry.noop());
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testSongNotSavedIfDuplicateSpotifyIdExists() {
        // Arrange
        Song song = new Song("4zFHwVj4A8s0G5f7aOlpKo", "Hometown Glory", List.of("Adele"));

        when(songRepository.existsBySpotifyId("4zFHwVj4A8s0G5f7aOlpKo")).thenReturn(true);

        // Act
        songService.addSong(song);

        // Assert
        verify(songRepository, never()).save(any(Song.class));
    }

    @Test
    public void testSongSavedIfNoDuplicateSpotifyIdExists() {
        // Arrange
        Song song = new Song("4zFHwVj4A8s0G5f7aOlpKo", "Hometown Glory", List.of("Adele"));

        when(songRepository.existsBySpotifyId("4zFHwVj4A8s0G5f7aOlpKo")).thenReturn(false);

        // Act
        songService.addSong(song);

        // Assert
        verify(songRepository, times(1)).save(song);
    }

    @Test
    public void testFindAllCalledWhenGetSongsWithoutEmotion() {
        // Arrange
        when(songRepository.findAll(any(PageRequest.class))).thenReturn(
            Page.empty(PageRequest.of(0, 25))
        );
        
        // Act
        songService.getSongs(0);

        // Assert
        verify(songRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    public void testFindAllByEmotionCalledWhenGetSongsWithEmotion() {
        // Arrange
        when(songRepository.findAllByEmotion(eq(Emotion.HAPPY), any(PageRequest.class))).thenReturn(
            Page.empty(PageRequest.of(0, 25))
        );
        
        // Act
        songService.getSongs(0, Emotion.HAPPY);

        // Assert
        verify(songRepository, times(1)).findAllByEmotion(eq(Emotion.HAPPY), any(PageRequest.class));
    }
}
