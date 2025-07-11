package com.moodify.song_service.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@NoArgsConstructor
@Document(collection = "song")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Song {
    @Id
    private UUID id;
    private String spotifyId;
    private String name;
    private List<String> artistNames;
    @Setter
    private Emotion emotion;

    @JsonCreator
    public Song(
        @JsonProperty("spotifyId") String spotifyId,
        @JsonProperty("name") String name,
        @JsonProperty("artistNames") List<String> artistNames
    ) {
        this.id = UUID.randomUUID();
        this.spotifyId = spotifyId;
        this.name = name;
        this.artistNames = artistNames;
        this.emotion = Emotion.randomEmotion();
    }
}
