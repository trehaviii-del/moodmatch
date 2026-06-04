package com.moodmatch.model;

public enum ExperienceType {
    COMFORT("Comfort", "🛋️", "Something warm and safe"),
    CHALLENGE("Challenge", "🧠", "Make me think"),
    ESCAPE("Escape", "🌍", "Take me somewhere else"),
    INSPIRATION("Inspiration", "⚡", "Fuel my fire");

    private final String displayName;
    private final String emoji;
    private final String description;

    ExperienceType(String displayName, String emoji, String description) {
        this.displayName = displayName;
        this.emoji = emoji;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getEmoji() { return emoji; }
    public String getDescription() { return description; }
}
