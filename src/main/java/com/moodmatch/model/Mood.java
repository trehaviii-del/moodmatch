package com.moodmatch.model;

public enum Mood {
    TIRED("Tired", "😴"),
    HAPPY("Happy", "😄"),
    SAD("Sad", "😢"),
    STRESSED("Stressed", "😤"),
    BORED("Bored", "😑"),
    LONELY("Lonely", "🥺"),
    ANXIOUS("Anxious", "😰"),
    ANGRY("Angry", "😠"),
    NOSTALGIC("Nostalgic", "🌅"),
    ROMANTIC("Romantic", "💕"),
    EXCITED("Excited", "🤩"),
    MOTIVATED("Motivated", "💪"),
    HEARTBROKEN("Heartbroken", "💔"),
    CONFUSED("Confused", "🤔"),
    PEACEFUL("Peaceful", "🌿");

    private final String displayName;
    private final String emoji;

    Mood(String displayName, String emoji) {
        this.displayName = displayName;
        this.emoji = emoji;
    }

    public String getDisplayName() { return displayName; }
    public String getEmoji() { return emoji; }
}
