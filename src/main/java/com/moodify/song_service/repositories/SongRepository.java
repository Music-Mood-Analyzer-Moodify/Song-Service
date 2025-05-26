package com.moodify.song_service.repositories;

import com.moodify.song_service.models.Emotion;
import com.moodify.song_service.models.Song;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SongRepository extends MongoRepository<Song, UUID> {
    boolean existsBySpotifyId(String spotifyId);

    @NonNull
    Page<Song> findAll(@NonNull Pageable pageable);
    Page<Song> findAllByEmotion(Emotion emotion, @NonNull Pageable pageable);
}
