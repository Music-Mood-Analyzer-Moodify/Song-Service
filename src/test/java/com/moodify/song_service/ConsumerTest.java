package com.moodify.song_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moodify.song_service.messaging.Consumer;
import com.moodify.song_service.models.Song;
import com.moodify.song_service.services.SongService;

import io.opentelemetry.api.OpenTelemetry;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.core.Message;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


public class ConsumerTest {
    private AutoCloseable closeable;
    @Mock
    private SongService songService;

    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testSongObjectSetCorrectlyAfterReceivingJSON() throws Exception {
        // Arrange
        String json = "{\"spotifyId\":\"4zFHwVj4A8s0G5f7aOlpKo\",\"name\":\"Hometown Glory\",\"artistNames\":[\"Adele\"]}";
        Message message = new Message(json.getBytes());
        ObjectMapper objectMapper = new ObjectMapper();
        Consumer consumer = new Consumer(OpenTelemetry.noop(), objectMapper, songService);

        ArgumentCaptor<Song> songCaptor = ArgumentCaptor.forClass(Song.class);

        // Act
        consumer.listenSendSongQueue(message);

        // Assert
        verify(songService, times(1)).addSong(songCaptor.capture());
        Song capturedSong = songCaptor.getValue();
        
        assertNotNull(capturedSong);
        assertEquals("4zFHwVj4A8s0G5f7aOlpKo", capturedSong.getSpotifyId());
        assertEquals("Hometown Glory", capturedSong.getName());
        assertTrue(capturedSong.getArtistNames().contains("Adele"));
    }
}