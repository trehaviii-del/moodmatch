package com.moodmatch.model;

public enum ContentType {
    MOVIE("Movie", "🎬", "Under 2.5 hours, standalone story"),
    SERIES("Series", "📺", "Episodic, longer commitment"),
    EITHER("Either", "🎲", "Surprise me");

    private final String displayName;
    private final String emoji;
    private final String description;

    ContentType(String displayName, String emoji, String description) {
        this.displayName = displayName;
        this.emoji = emoji;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getEmoji() { return emoji; }
    public String getDescription() { return description; }
}
