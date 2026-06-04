package com.moodmatch.controller;

import com.moodmatch.model.*;
import com.moodmatch.service.RecommendationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class MoodController {

    private static final Map<Mood, Set<Mood>> COMPATIBLE_MOODS = new EnumMap<>(Mood.class);

    static {
        COMPATIBLE_MOODS.put(Mood.HAPPY,       EnumSet.of(Mood.EXCITED, Mood.ROMANTIC, Mood.NOSTALGIC, Mood.MOTIVATED));
        COMPATIBLE_MOODS.put(Mood.SAD,         EnumSet.of(Mood.LONELY, Mood.NOSTALGIC, Mood.PEACEFUL, Mood.HEARTBROKEN));
        COMPATIBLE_MOODS.put(Mood.STRESSED,    EnumSet.of(Mood.ANXIOUS, Mood.TIRED, Mood.CONFUSED));
        COMPATIBLE_MOODS.put(Mood.BORED,       EnumSet.of(Mood.EXCITED, Mood.HAPPY, Mood.MOTIVATED));
        COMPATIBLE_MOODS.put(Mood.LONELY,      EnumSet.of(Mood.SAD, Mood.ROMANTIC, Mood.NOSTALGIC, Mood.PEACEFUL));
        COMPATIBLE_MOODS.put(Mood.ANGRY,       EnumSet.of(Mood.STRESSED, Mood.ANXIOUS, Mood.MOTIVATED));
        COMPATIBLE_MOODS.put(Mood.TIRED,       EnumSet.of(Mood.PEACEFUL, Mood.SAD, Mood.BORED));
        COMPATIBLE_MOODS.put(Mood.ANXIOUS,     EnumSet.of(Mood.STRESSED, Mood.CONFUSED, Mood.LONELY));
        COMPATIBLE_MOODS.put(Mood.NOSTALGIC,   EnumSet.of(Mood.HAPPY, Mood.SAD, Mood.LONELY, Mood.ROMANTIC));
        COMPATIBLE_MOODS.put(Mood.ROMANTIC,    EnumSet.of(Mood.HAPPY, Mood.LONELY, Mood.NOSTALGIC, Mood.PEACEFUL));
        COMPATIBLE_MOODS.put(Mood.EXCITED,     EnumSet.of(Mood.HAPPY, Mood.MOTIVATED, Mood.BORED));
        COMPATIBLE_MOODS.put(Mood.MOTIVATED,   EnumSet.of(Mood.EXCITED, Mood.HAPPY, Mood.ANGRY));
        COMPATIBLE_MOODS.put(Mood.HEARTBROKEN, EnumSet.of(Mood.SAD, Mood.LONELY, Mood.NOSTALGIC));
        COMPATIBLE_MOODS.put(Mood.CONFUSED,    EnumSet.of(Mood.ANXIOUS, Mood.STRESSED, Mood.BORED));
        COMPATIBLE_MOODS.put(Mood.PEACEFUL,    EnumSet.of(Mood.TIRED, Mood.LONELY, Mood.NOSTALGIC, Mood.ROMANTIC));
    }

    private final RecommendationService recommendationService;

    public MoodController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    // ── Step 1: mood grid ────────────────────────────────────────────────────
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("moods", Mood.values());
        return "index";
    }

    // ── Step 2: second-mood ──────────────────────────────────────────────────
    @PostMapping("/select-second-mood")
    public String selectSecondMoodPost(@RequestParam String mood, Model model) {
        return loadSecondMood(mood, model);
    }

    @GetMapping("/select-second-mood")
    public String selectSecondMoodGet(@RequestParam String mood, Model model) {
        return loadSecondMood(mood, model);
    }

    private String loadSecondMood(String mood, Model model) {
        Mood primary = Mood.valueOf(mood);
        List<Mood> compatible = COMPATIBLE_MOODS.getOrDefault(primary, EnumSet.noneOf(Mood.class))
            .stream().sorted(Comparator.comparingInt(Mood::ordinal)).collect(Collectors.toList());
        model.addAttribute("primaryMood", primary);
        model.addAttribute("compatibleMoods", compatible);
        return "second-mood";
    }

    // ── Step 3: format selection ─────────────────────────────────────────────
    @PostMapping("/select-format")
    public String selectFormatPost(
            @RequestParam String mood,
            @RequestParam(required = false, defaultValue = "") String secondMood,
            Model model) {
        return loadFormat(mood, secondMood, model);
    }

    @GetMapping("/select-format")
    public String selectFormatGet(
            @RequestParam String mood,
            @RequestParam(required = false, defaultValue = "") String secondMood,
            Model model) {
        return loadFormat(mood, secondMood, model);
    }

    private String loadFormat(String mood, String secondMood, Model model) {
        model.addAttribute("mood", Mood.valueOf(mood));
        model.addAttribute("secondMood", secondMood.isEmpty() ? null : Mood.valueOf(secondMood));
        model.addAttribute("formats", Format.values());
        return "format";
    }

    // ── Step 4a: conditional Movie/Series sub-step ───────────────────────────
    @PostMapping("/select-subformat")
    public String selectSubformat(
            @RequestParam String mood,
            @RequestParam(required = false, defaultValue = "") String secondMood,
            @RequestParam String format,
            Model model) {

        Format selectedFormat = Format.valueOf(format);
        if (selectedFormat.isNeedsContentTypeStep()) {
            model.addAttribute("mood", Mood.valueOf(mood));
            model.addAttribute("secondMood", secondMood.isEmpty() ? null : Mood.valueOf(secondMood));
            model.addAttribute("format", selectedFormat);
            model.addAttribute("contentTypes", ContentType.values());
            return "content-type";
        }
        return loadGenres(mood, secondMood, format, "", model);
    }

    // ── Step 4b: content-type (back-nav GET + POST from genres back-button) ──
    @GetMapping("/select-content-type")
    public String selectContentTypeGet(
            @RequestParam String mood,
            @RequestParam(required = false, defaultValue = "") String secondMood,
            @RequestParam(required = false, defaultValue = "") String format,
            Model model) {
        model.addAttribute("mood", Mood.valueOf(mood));
        model.addAttribute("secondMood", secondMood.isEmpty() ? null : Mood.valueOf(secondMood));
        model.addAttribute("format", format.isEmpty() ? null : Format.valueOf(format));
        model.addAttribute("contentTypes", ContentType.values());
        return "content-type";
    }

    // ── Step 5: genre exclusion ──────────────────────────────────────────────
    @PostMapping("/select-genres")
    public String selectGenresPost(
            @RequestParam String mood,
            @RequestParam(required = false, defaultValue = "") String secondMood,
            @RequestParam String format,
            @RequestParam(required = false, defaultValue = "") String contentType,
            Model model) {
        return loadGenres(mood, secondMood, format, contentType, model);
    }

    @GetMapping("/select-genres")
    public String selectGenresGet(
            @RequestParam String mood,
            @RequestParam(required = false, defaultValue = "") String secondMood,
            @RequestParam String format,
            @RequestParam(required = false, defaultValue = "") String contentType,
            Model model) {
        return loadGenres(mood, secondMood, format, contentType, model);
    }

    private String loadGenres(String mood, String secondMood, String format, String contentType, Model model) {
        model.addAttribute("mood", Mood.valueOf(mood));
        model.addAttribute("secondMood", secondMood.isEmpty() ? null : Mood.valueOf(secondMood));
        model.addAttribute("format", Format.valueOf(format));
        model.addAttribute("contentType", contentType.isEmpty() ? null : ContentType.valueOf(contentType));
        model.addAttribute("genres", Genre.values());
        return "genres";
    }

    // ── Step 6: experience type ──────────────────────────────────────────────
    @PostMapping("/select-experience")
    public String selectExperience(
            @RequestParam String mood,
            @RequestParam(required = false, defaultValue = "") String secondMood,
            @RequestParam String format,
            @RequestParam(required = false, defaultValue = "") String contentType,
            @RequestParam(required = false) List<String> excludedGenre,
            Model model) {

        String excludedGenres = (excludedGenre != null && !excludedGenre.isEmpty())
            ? String.join(",", excludedGenre) : "";

        model.addAttribute("mood", Mood.valueOf(mood));
        model.addAttribute("secondMood", secondMood.isEmpty() ? null : Mood.valueOf(secondMood));
        model.addAttribute("format", Format.valueOf(format));
        model.addAttribute("contentType", contentType.isEmpty() ? null : ContentType.valueOf(contentType));
        model.addAttribute("excludedGenres", excludedGenres);
        model.addAttribute("experienceTypes", ExperienceType.values());
        return "experience";
    }

    // ── Step 7: result ───────────────────────────────────────────────────────
    @PostMapping("/recommend")
    public String recommend(
            @RequestParam String mood,
            @RequestParam(required = false, defaultValue = "") String secondMood,
            @RequestParam String format,
            @RequestParam(required = false, defaultValue = "") String contentType,
            @RequestParam(required = false, defaultValue = "") String excludedGenres,
            @RequestParam String experienceType,
            @RequestParam(required = false, defaultValue = "") String exclude,
            Model model) {

        Mood primaryMood     = Mood.valueOf(mood);
        Mood secondaryMood   = secondMood.isEmpty() ? null : Mood.valueOf(secondMood);
        Format fmt           = Format.valueOf(format);
        ContentType ct       = contentType.isEmpty() ? null : ContentType.valueOf(contentType);
        ExperienceType exp   = ExperienceType.valueOf(experienceType);

        Recommendation rec = recommendationService.getRecommendation(
            primaryMood, secondaryMood, fmt, ct, excludedGenres, exp, exclude);

        model.addAttribute("mood",           primaryMood);
        model.addAttribute("secondMood",     secondaryMood);
        model.addAttribute("format",         fmt);
        model.addAttribute("contentType",    ct);
        model.addAttribute("excludedGenres", excludedGenres);
        model.addAttribute("experienceType", exp);
        model.addAttribute("recommendation", rec);
        return "result";
    }
}
