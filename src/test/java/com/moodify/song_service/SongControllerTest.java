package com.moodify.song_service;

import com.moodify.song_service.controllers.SongController;
import com.moodify.song_service.models.Emotion;
import com.moodify.song_service.services.SongService;

import io.opentelemetry.api.OpenTelemetry;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class SongControllerTest {

    private AutoCloseable closeable;
    @Mock
    private SongService songService;
    private SongController controller;

    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        controller = new SongController(null, OpenTelemetry.noop(), songService);
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testGetSongsWithoutEmotionCallsCorrectServiceMethod() {
        // Act
        controller.allSongs(0);

        // Assert
        verify(songService, times(1)).getSongs(0);
    }

    @Test
    public void testGetSongsWithEmotionCallsCorrectServiceMethod() {
        // Act
        controller.songsWithEmotion(0, "HAPPY");

        // Assert
        verify(songService, times(1)).getSongs(0, Emotion.HAPPY);
    }
}