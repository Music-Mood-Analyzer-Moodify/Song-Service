// package com.moodify.song_service;

// import com.moodify.song_service.models.Emotion;
// import com.moodify.song_service.models.NoOpOpenTelemetry;
// import com.moodify.song_service.models.Song;
// import com.moodify.song_service.repositories.SongRepository;
// import com.moodify.song_service.services.SongService;
// import java.util.List;
// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.data.domain.Page;
// import org.springframework.test.context.TestPropertySource;
// import org.testcontainers.containers.MongoDBContainer;

// import static org.junit.jupiter.api.Assertions.assertEquals;

// @SpringBootTest
// @TestPropertySource(locations = "classpath:test.properties")
// public class SongServiceIntegrationTest {

//     private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");
//     @Autowired
//     private SongRepository songRepository;
//     private SongService songService;

//     @BeforeEach
//     public void setUp() {
//         mongoDBContainer.start();
//         songRepository.deleteAll();

//         List<Song> songs = List.of(
//             new Song("1", "Song 1", List.of("Artist 1")),
//             new Song("2", "Song 2", List.of("Artist 2")),
//             new Song("3", "Song 3", List.of("Artist 3"))
//         );
//         songs.get(0).setEmotion(Emotion.HAPPY);
//         songs.get(1).setEmotion(Emotion.HAPPY);
//         songs.get(2).setEmotion(Emotion.SAD);

//         songRepository.saveAll(songs);
//         songService = new SongService(songRepository, NoOpOpenTelemetry.getInstance());
//     }

//     @AfterEach
//     public void tearDown() {
//         mongoDBContainer.stop();
//     }

//     @Test
//     public void testGetSongsWithoutEmotion() {
//         Page<Song> songs = songService.getSongs(0);
//         assertEquals(3, songs.getTotalElements());
//     }

//     @Test
//     public void testGetSongsWithEmotion() {
//         Page<Song> songs = songService.getSongs(0, Emotion.HAPPY);
//         assertEquals(2, songs.getTotalElements());
//     }
// }