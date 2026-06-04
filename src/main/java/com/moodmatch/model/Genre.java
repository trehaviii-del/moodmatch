package com.moodmatch.model;

public enum Genre {
    ROMANCE( "Romance",  "💕"),
    HORROR(  "Horror",   "👻"),
    SCI_FI(  "Sci-Fi",   "🚀"),
    FANTASY( "Fantasy",  "🧙"),
    CRIME(   "Crime",    "🔍"),
    THRILLER("Thriller", "😰"),
    COMEDY(  "Comedy",   "😂"),
    DRAMA(   "Drama",    "🎭"),
    ACTION(  "Action",   "💥");

    private final String displayName;
    private final String emoji;

    Genre(String displayName, String emoji) {
        this.displayName = displayName;
        this.emoji       = emoji;
    }

    public String getDisplayName() { return displayName; }
    public String getEmoji()       { return emoji; }
}
