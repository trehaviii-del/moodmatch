package com.moodmatch.model;

public class Recommendation {
    private String title;
    private int year;
    private String genre;
    private String reason;
    private ExperienceType experienceType;

    public Recommendation() {}

    public Recommendation(String title, int year, String genre, String reason) {
        this.title = title;
        this.year = year;
        this.genre = genre;
        this.reason = reason;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public ExperienceType getExperienceType() { return experienceType; }
    public void setExperienceType(ExperienceType experienceType) { this.experienceType = experienceType; }
}
