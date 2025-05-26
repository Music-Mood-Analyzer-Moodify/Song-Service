package com.moodify.song_service.models;

import java.util.Random;

public enum Emotion {
    HAPPY("happy"),
    SAD("sad"),
    ANGRY("angry"),
    RELAXED("relaxed"),
    EXCITED("excited"),
    UNKNOWN("unknown");

    private final String value;
    private static final Emotion[] VALUES = values();
    private static final Random RANDOM = new Random();

    Emotion(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static Emotion randomEmotion() {
        return VALUES[RANDOM.nextInt(VALUES.length - 1)]; // Exclude UNKNOWN
    }
}