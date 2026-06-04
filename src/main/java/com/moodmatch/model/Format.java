package com.moodmatch.model;

public enum Format {
    LIVE_ACTION( "Live Action",   "🎬", "Real actors, any length",                  true),
    ANIME(       "Anime",         "⛩️",  "Japanese animation — series or film",       false),
    ANIMATION(   "Animation",     "🎨", "Pixar, Studio Ghibli, and more",            false),
    DOCUMENTARY( "Documentary",   "📽️",  "Real stories, real people",                 false),
    KOREAN_DRAMA("Korean Drama",  "🇰🇷", "K-drama series",                           false),
    MINI_SERIES( "Mini-Series",   "📺", "Under 10 episodes, complete story",         false),
    CLASSIC(     "Classic",       "🎞️",  "Released before 2000",                      true),
    LATEST(      "Latest",        "✨",  "Released after 2020",                       true);

    private final String  displayName;
    private final String  emoji;
    private final String  description;
    private final boolean needsContentTypeStep;

    Format(String displayName, String emoji, String description, boolean needsContentTypeStep) {
        this.displayName          = displayName;
        this.emoji                = emoji;
        this.description          = description;
        this.needsContentTypeStep = needsContentTypeStep;
    }

    public String  getDisplayName()       { return displayName; }
    public String  getEmoji()             { return emoji; }
    public String  getDescription()       { return description; }
    public boolean isNeedsContentTypeStep(){ return needsContentTypeStep; }
}
