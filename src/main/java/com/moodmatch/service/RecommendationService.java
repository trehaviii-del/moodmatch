package com.moodmatch.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moodmatch.client.ClaudeApiClient;
import com.moodmatch.model.*;

import java.util.Arrays;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.moodmatch.model.ExperienceType.*;

@Service
public class RecommendationService {

    private static final Random RANDOM = new Random();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final ClaudeApiClient claudeApiClient;

    public RecommendationService(ClaudeApiClient claudeApiClient) {
        this.claudeApiClient = claudeApiClient;
    }
    private static final Map<Mood, Map<ContentType, List<Recommendation>>> RECOMMENDATIONS;

    static {
        Map<Mood, Map<ContentType, List<Recommendation>>> map = new EnumMap<>(Mood.class);

        map.put(Mood.TIRED, recs(
            List.of(
                rec("The Secret Life of Walter Mitty", 2013, "Adventure / Drama",
                    "Visually gorgeous and gently paced — inspiring without demanding focus or energy", ESCAPE),
                rec("Big Fish", 2003, "Fantasy / Drama",
                    "Tim Burton's most warmhearted film — a lush fairy tale that wraps around you like a soft blanket", COMFORT),
                rec("Midnight in Paris", 2011, "Comedy / Romance",
                    "A gentle, whimsical escape to 1920s Paris — Woody Allen's most effortlessly charming film", ESCAPE)
            ),
            List.of(
                rec("Planet Earth II", 2016, "Nature Documentary",
                    "Stunning visuals and David Attenborough's soothing narration — perfect passive viewing", ESCAPE),
                rec("Our Planet", 2019, "Nature Documentary",
                    "Netflix's breathtaking answer to Planet Earth — even more urgent and visually extraordinary", ESCAPE),
                rec("Abstract: The Art of Design", 2017, "Documentary",
                    "Thoughtful deep dives into creative genius — meditative viewing that sparks quiet inspiration", INSPIRATION)
            ),
            List.of(
                rec("Amélie", 2001, "Romantic Comedy",
                    "A warm, dreamy film that carries you along effortlessly — no energy required", COMFORT),
                rec("Paddington", 2014, "Family / Comedy",
                    "Effortlessly charming and warm — pure comfort viewing that requires nothing from you", COMFORT),
                rec("The Princess Bride", 1987, "Fantasy / Adventure",
                    "A timeless fairy tale full of wit and heart — you could watch it half-asleep and love every moment", COMFORT)
            )
        ));

        map.put(Mood.HAPPY, recs(
            List.of(
                rec("Paddington 2", 2017, "Comedy / Family",
                    "The purest distillation of joy on film — amplifies your good mood and keeps you grinning", COMFORT),
                rec("The Lego Movie", 2014, "Animated Comedy",
                    "Genuinely hilarious and surprisingly clever — it fuels the good energy you already have", INSPIRATION),
                rec("Singin' in the Rain", 1952, "Musical / Comedy",
                    "Pure, exuberant cinema joy — the dancing alone will put a permanent grin on your face", ESCAPE)
            ),
            List.of(
                rec("Schitt's Creek", 2015, "Comedy",
                    "Warm, funny, and endlessly charming — perfectly matched to a happy mood", COMFORT),
                rec("Abbott Elementary", 2021, "Comedy",
                    "The funniest workplace comedy in years — big heart, sharp writing, endlessly rewatchable", COMFORT),
                rec("What We Do in the Shadows", 2019, "Comedy",
                    "Gleefully absurd vampire roommate comedy — laugh-out-loud funny with real wit underneath", CHALLENGE)
            ),
            List.of(
                rec("The Grand Budapest Hotel", 2014, "Comedy / Adventure",
                    "A vibrant, witty delight that perfectly matches high spirits", ESCAPE),
                rec("Knives Out", 2019, "Mystery / Comedy",
                    "Smart, funny, and endlessly entertaining — a feel-good whodunit that keeps surprising you", CHALLENGE),
                rec("O Brother, Where Art Thou?", 2000, "Comedy / Adventure",
                    "The Coen Brothers at their most playful — brilliantly weird, joyful, and musical", ESCAPE)
            )
        ));

        map.put(Mood.SAD, recs(
            List.of(
                rec("Soul", 2020, "Animated Drama",
                    "A meditation on what makes life meaningful — deeply moving but leaves you with something beautiful", CHALLENGE),
                rec("Inside Out", 2015, "Animated Drama",
                    "Pixar's most emotionally intelligent film — it validates sadness in the most profound way", CHALLENGE),
                rec("A Man Called Ove", 2015, "Drama / Comedy",
                    "A grumpy man's heart quietly opens — devastating and heartwarming in equal measure", COMFORT)
            ),
            List.of(
                rec("Ted Lasso", 2020, "Comedy / Drama",
                    "Overflowing with genuine kindness and optimism — guaranteed to gently lift you out of the fog", COMFORT),
                rec("This Is Us", 2016, "Drama",
                    "A multigenerational family drama designed to make you feel everything — keep tissues close", COMFORT),
                rec("Anne with an E", 2017, "Drama",
                    "Anne's fierce resilience and wonder will quietly lift you out of yourself — beautiful and tender", INSPIRATION)
            ),
            List.of(
                rec("Good Will Hunting", 1997, "Drama",
                    "Raw and emotional, but ultimately about healing and connection — let it meet you where you are", CHALLENGE),
                rec("Coco", 2017, "Animated Drama",
                    "Pixar's most generous film about love, memory, and letting go — a beautiful, earned cry", COMFORT),
                rec("The Pursuit of Happyness", 2006, "Drama",
                    "A real story of refusing to quit against impossible odds — it'll meet your sadness and pull you through", INSPIRATION)
            )
        ));

        map.put(Mood.STRESSED, recs(
            List.of(
                rec("My Neighbor Totoro", 1988, "Animated Fantasy",
                    "A world with no villains and no tension — pure, unhurried decompression", COMFORT),
                rec("Isle of Dogs", 2018, "Animated Adventure",
                    "Wes Anderson's meticulous stop-motion world — detailed enough to absorb you, calm enough to soothe", ESCAPE),
                rec("The Secret Garden", 2020, "Fantasy / Drama",
                    "A restorative story about finding peace in growing things — gently beautiful and unhurried", ESCAPE)
            ),
            List.of(
                rec("The Great British Bake Off", 2010, "Reality / Competition",
                    "Gentle competition, warm humans, and beautiful pastries — the most reliably de-stressing show on TV", COMFORT),
                rec("Nailed It!", 2018, "Comedy / Reality",
                    "Amateur bakers failing spectacularly at elaborate cakes — pure, uncomplicated comedy relief", COMFORT),
                rec("Salt Fat Acid Heat", 2018, "Food Documentary",
                    "Samin Nosrat explores flavor around the world — warm, personal, and meditative", ESCAPE)
            ),
            List.of(
                rec("Chef's Table", 2015, "Documentary",
                    "Beautiful, slow, and meditative — zero stakes, pure appreciation of craft", ESCAPE),
                rec("Kiki's Delivery Service", 1989, "Animated Fantasy",
                    "Studio Ghibli's gentlest film — a young witch finding her footing in a peaceful, unhurried world", COMFORT),
                rec("Ponyo", 2008, "Animated Fantasy",
                    "Pure childlike wonder and simplicity — Miyazaki at his most uncomplicated and calming", COMFORT)
            )
        ));

        map.put(Mood.BORED, recs(
            List.of(
                rec("Everything Everywhere All at Once", 2022, "Sci-Fi / Action",
                    "Relentlessly inventive and unpredictable — boredom cannot survive a single frame of this film", CHALLENGE),
                rec("Hot Fuzz", 2007, "Comedy / Action",
                    "Edgar Wright's perfectly calibrated action-comedy — dense with jokes, references, and incredible escalation", ESCAPE),
                rec("Speed Racer", 2008, "Action / Adventure",
                    "A criminally underrated visual explosion — the most hyper-stimulating film ever committed to screen", ESCAPE)
            ),
            List.of(
                rec("Dark", 2017, "Sci-Fi / Mystery",
                    "A dense, mind-bending mystery spanning generations — demands full attention and repays every second", CHALLENGE),
                rec("Severance", 2022, "Sci-Fi / Thriller",
                    "A workplace mystery unlike anything on TV — you'll be obsessed from the first unsettling scene", CHALLENGE),
                rec("Mindhunter", 2017, "Crime / Thriller",
                    "Fincher's most gripping series — the psychology of serial killers told with surgical precision", CHALLENGE)
            ),
            List.of(
                rec("Raiders of the Lost Ark", 1981, "Action / Adventure",
                    "The gold standard of propulsive, edge-of-your-seat storytelling — the pace never drops for a second", ESCAPE),
                rec("The Matrix", 1999, "Sci-Fi / Action",
                    "Still the most mind-expanding action blockbuster ever made — it will rewire how you see the world", CHALLENGE),
                rec("Kill Bill: Vol. 1", 2003, "Action / Thriller",
                    "Tarantino's most kinetically pure film — 90 minutes of style, tension, and visceral excitement", ESCAPE)
            )
        ));

        map.put(Mood.LONELY, recs(
            List.of(
                rec("Her", 2013, "Drama / Sci-Fi",
                    "The most honest film ever made about loneliness — it sees and understands you completely", CHALLENGE),
                rec("Cast Away", 2000, "Drama / Adventure",
                    "A man completely alone — paradoxically makes you feel less alone by showing loneliness so honestly", CHALLENGE),
                rec("The Perks of Being a Wallflower", 2012, "Drama",
                    "A love letter to everyone who has ever felt on the outside — it genuinely sees you", COMFORT)
            ),
            List.of(
                rec("Fleabag", 2016, "Comedy / Drama",
                    "A woman talking directly to you about her messy, lonely life — raw, funny, and utterly intimate", CHALLENGE),
                rec("Please Like Me", 2013, "Comedy / Drama",
                    "Josh Thomas's autobiographical series — achingly real about isolation and unlikely human connection", COMFORT),
                rec("Pose", 2018, "Drama",
                    "A found family in New York's ballroom scene — one of the warmest ensemble shows ever made", COMFORT)
            ),
            List.of(
                rec("Lost in Translation", 2003, "Drama / Romance",
                    "Two strangers finding each other in a foreign city — you won't feel alone watching it", ESCAPE),
                rec("The Intouchables", 2011, "Comedy / Drama",
                    "Two very different men form an unlikely bond — warm, funny, and quietly moving", COMFORT),
                rec("Brooklyn", 2015, "Drama / Romance",
                    "A young woman far from home slowly finds herself — tender, quietly beautiful, and deeply hopeful", ESCAPE)
            )
        ));

        map.put(Mood.ANXIOUS, recs(
            List.of(
                rec("Spirited Away", 2001, "Animated Fantasy",
                    "A richly absorbing world that pulls you completely out of your own head", ESCAPE),
                rec("Howl's Moving Castle", 2004, "Animated Fantasy",
                    "Miyazaki's most romantic adventure — wonderfully strange and utterly absorbing", ESCAPE),
                rec("The Princess Bride", 1987, "Fantasy / Adventure",
                    "A perfectly constructed comfort film — funny, exciting, and entirely unthreatening", COMFORT)
            ),
            List.of(
                rec("The Good Place", 2016, "Comedy / Fantasy",
                    "Light, philosophical, and endlessly funny — impossible to stay anxious while watching", CHALLENGE),
                rec("Brooklyn Nine-Nine", 2013, "Comedy",
                    "The most reliably wholesome comedy on TV — zero unnecessary drama, all warmth and laughs", COMFORT),
                rec("Gravity Falls", 2012, "Animated Comedy / Mystery",
                    "A mystery-comedy cartoon that's impossible to watch anxiously — charming, clever, and warm", ESCAPE)
            ),
            List.of(
                rec("Julie & Julia", 2009, "Drama / Comedy",
                    "Warm, cozy, and grounded in the simple pleasure of cooking — it gently anchors you in the present", COMFORT),
                rec("Moana", 2016, "Animated Adventure",
                    "A buoyant, gorgeous adventure that moves at exactly the right pace — hard to feel anxious watching it", ESCAPE),
                rec("My Neighbor Totoro", 1988, "Animated Fantasy",
                    "The most calming film ever made — Totoro's peaceful world has no room left for anxiety", COMFORT)
            )
        ));

        map.put(Mood.ANGRY, recs(
            List.of(
                rec("John Wick", 2014, "Action / Thriller",
                    "Channel your energy into two cathartic hours of perfectly choreographed action", ESCAPE),
                rec("Die Hard", 1988, "Action / Comedy",
                    "The perfect action movie — cathartic, funny, and deeply satisfying from start to finish", ESCAPE),
                rec("Network", 1976, "Drama / Satire",
                    "A film that channels righteous fury into something brilliant — validate your anger with this masterpiece", CHALLENGE)
            ),
            List.of(
                rec("Breaking Bad", 2008, "Crime / Drama",
                    "Pour your feelings into Walter White's transformation — the most satisfying tension and release on TV", CHALLENGE),
                rec("The Wire", 2002, "Crime / Drama",
                    "The greatest TV drama ever made — angry at the right things, and profoundly, humanly correct", CHALLENGE),
                rec("Ozark", 2017, "Crime / Thriller",
                    "High-stakes crime drama that turns frustration and tension into compulsive, gripping viewing", CHALLENGE)
            ),
            List.of(
                rec("Mad Max: Fury Road", 2015, "Action",
                    "Non-stop kinetic energy that transmutes anger into pure, thrilling spectacle", ESCAPE),
                rec("Oldboy", 2003, "Thriller",
                    "A viscerally intense Korean revenge thriller — raw, explosive, and absolutely unforgettable", CHALLENGE),
                rec("Heat", 1995, "Crime / Thriller",
                    "A slow-burn crime epic — tension builds to a release that feels deeply earned and cathartic", CHALLENGE)
            )
        ));

        map.put(Mood.NOSTALGIC, recs(
            List.of(
                rec("Stand By Me", 1986, "Drama / Adventure",
                    "The definitive film about childhood friendship and summers that feel like they'll last forever", COMFORT),
                rec("E.T. the Extra-Terrestrial", 1982, "Sci-Fi / Family",
                    "Spielberg's most purely emotional film — childhood wonder and friendship distilled to perfection", COMFORT),
                rec("Ferris Bueller's Day Off", 1986, "Comedy",
                    "The ultimate 80s coming-of-age fantasy — carefree, funny, and forever bathed in golden light", ESCAPE)
            ),
            List.of(
                rec("Stranger Things", 2016, "Sci-Fi / Horror",
                    "Pure 80s nostalgia wrapped in an irresistible coming-of-age mystery", ESCAPE),
                rec("Freaks and Geeks", 1999, "Comedy / Drama",
                    "The most honest and affectionate portrayal of being a teenager ever put on screen", COMFORT),
                rec("The Wonder Years", 1988, "Drama / Comedy",
                    "A beautifully observed story of growing up in the late 60s — nostalgic about nostalgia itself", COMFORT)
            ),
            List.of(
                rec("The Sandlot", 1993, "Comedy / Drama",
                    "A love letter to childhood summers, baseball, and the friendships you never forget", COMFORT),
                rec("Back to the Future", 1985, "Sci-Fi / Adventure",
                    "The perfect time-travel adventure — endlessly rewatchable and guaranteed to bring the nostalgia flooding back", ESCAPE),
                rec("Home Alone", 1990, "Comedy / Family",
                    "Pure holiday comfort viewing — instantly transports you back to the feeling of being a kid", COMFORT)
            )
        ));

        map.put(Mood.ROMANTIC, recs(
            List.of(
                rec("Before Sunrise", 1995, "Romance / Drama",
                    "Two strangers spend one perfect night in Vienna — intimate, intelligent, and completely enchanting", CHALLENGE),
                rec("Notting Hill", 1999, "Romance / Comedy",
                    "Charming, warm, and wonderfully British — a love story that feels real even at its most fantastical", COMFORT),
                rec("Pride & Prejudice", 2005, "Period Romance",
                    "The most visually beautiful romance ever filmed — every frame a painting, every scene a swoon", ESCAPE)
            ),
            List.of(
                rec("Bridgerton", 2020, "Period Romance",
                    "Lavish settings, swooning romance, and delicious drama — perfect for an indulgent evening", ESCAPE),
                rec("Outlander", 2014, "Period Romance / Drama",
                    "Epic time-travel romance with gorgeous Scottish scenery — deeply passionate and sweepingly cinematic", ESCAPE),
                rec("Virgin River", 2019, "Romance / Drama",
                    "Warm, cozy romance set in a small mountain town — low-stakes comfort viewing at its most pleasant", COMFORT)
            ),
            List.of(
                rec("Eternal Sunshine of the Spotless Mind", 2004, "Romance / Sci-Fi",
                    "A profound meditation on love, memory, and why we keep choosing each other", CHALLENGE),
                rec("La La Land", 2016, "Musical / Romance",
                    "A bittersweet ode to love, dreams, and sacrifice — visually stunning and emotionally honest", ESCAPE),
                rec("Before Sunset", 2004, "Romance / Drama",
                    "Two people reunite after nine years — the conversation is just as electric as their first night", CHALLENGE)
            )
        ));

        map.put(Mood.EXCITED, recs(
            List.of(
                rec("Top Gun: Maverick", 2022, "Action / Drama",
                    "High-octane thrills with a genuinely moving story — perfectly matched to your high energy", ESCAPE),
                rec("Mission: Impossible — Fallout", 2018, "Action / Thriller",
                    "The best action set pieces of the decade, tied together with genuine tension — heart-racing throughout", ESCAPE),
                rec("Baby Driver", 2017, "Action / Crime",
                    "Every scene choreographed to music — the most kinetically thrilling film of its era", ESCAPE)
            ),
            List.of(
                rec("Succession", 2018, "Drama / Thriller",
                    "Razor-sharp, wickedly addictive, and impossible to stop — your excitement will only grow", CHALLENGE),
                rec("Squid Game", 2021, "Drama / Thriller",
                    "Global phenomenon for a reason — gripping, tense, and impossible to stop once you've started", CHALLENGE),
                rec("The Bear", 2022, "Drama / Comedy",
                    "A restaurant drama so tightly wound it barely lets you breathe — precision filmmaking at full speed", CHALLENGE)
            ),
            List.of(
                rec("Inception", 2010, "Sci-Fi / Action",
                    "A bold, thrilling puzzle that fully rewards an engaged, energized mind", CHALLENGE),
                rec("Edge of Tomorrow", 2014, "Sci-Fi / Action",
                    "Tom Cruise in a brilliant action loop — gets better with every cycle, endlessly satisfying", ESCAPE),
                rec("Parasite", 2019, "Thriller / Drama",
                    "Bong Joon-ho's Palme d'Or masterpiece — starts tense and ends in pure, unforgettable spectacle", CHALLENGE)
            )
        ));

        map.put(Mood.MOTIVATED, recs(
            List.of(
                rec("Whiplash", 2014, "Drama / Music",
                    "An intense portrait of ambition and excellence — will push your motivation even further", INSPIRATION),
                rec("Rocky", 1976, "Drama / Sports",
                    "The original underdog story — somehow still the most effective motivational film ever made", INSPIRATION),
                rec("The Pursuit of Happyness", 2006, "Drama",
                    "A real story of refusing to quit against impossible odds — impossible not to be fired up by it", INSPIRATION)
            ),
            List.of(
                rec("Abstract: The Art of Design", 2017, "Documentary",
                    "Deep dives into creative masters pursuing perfection in their craft — endlessly inspiring", INSPIRATION),
                rec("The Last Dance", 2020, "Sports Documentary",
                    "Michael Jordan's obsessive pursuit of greatness — the most compelling sports documentary ever made", INSPIRATION),
                rec("Wild Wild Country", 2018, "Documentary",
                    "A jaw-dropping true story of vision, ambition, and what people will sacrifice for a belief", CHALLENGE)
            ),
            List.of(
                rec("The Social Network", 2010, "Drama / Biopic",
                    "A razor-sharp story of obsession and creation — it fuels the fire already burning in you", INSPIRATION),
                rec("Moneyball", 2011, "Drama / Sports",
                    "Using data and belief to challenge the establishment — smart, satisfying, and quietly inspiring", INSPIRATION),
                rec("Steve Jobs", 2015, "Drama / Biopic",
                    "Sorkin's rapid-fire portrait of a complicated genius — relentless ambition distilled into three defining moments", CHALLENGE)
            )
        ));

        map.put(Mood.HEARTBROKEN, recs(
            List.of(
                rec("500 Days of Summer", 2009, "Romance / Drama",
                    "Honest and cathartic about love that didn't work out — it validates exactly how you feel right now", CHALLENGE),
                rec("Blue Valentine", 2010, "Drama / Romance",
                    "Brutally honest about how love falls apart — it will break your heart but make you feel less alone", CHALLENGE),
                rec("Celeste and Jesse Forever", 2012, "Comedy / Drama",
                    "A sharper, funnier look at the slow ache of a relationship ending — cathartic and painfully real", COMFORT)
            ),
            List.of(
                rec("Normal People", 2020, "Drama / Romance",
                    "An achingly honest portrait of love and loss — it understands heartbreak in a way few shows do", CHALLENGE),
                rec("Catastrophe", 2015, "Comedy / Drama",
                    "Two people figuring out love and life together — funny, raw, and honest about how hard it all is", COMFORT),
                rec("I May Destroy You", 2020, "Drama",
                    "Michaela Coel's extraordinary meditation on trauma, healing, and reclaiming yourself — essential viewing", CHALLENGE)
            ),
            List.of(
                rec("About Time", 2013, "Romance / Drama",
                    "A tender film about love, loss, and gratitude — leaves you softer, not more broken", COMFORT),
                rec("Marriage Story", 2019, "Drama",
                    "Noah Baumbach's unflinching portrait of a relationship ending — devastating and deeply humane", CHALLENGE),
                rec("Brooklyn", 2015, "Drama / Romance",
                    "A story about loss and starting over somewhere new — quietly moving and ultimately hopeful", ESCAPE)
            )
        ));

        map.put(Mood.CONFUSED, recs(
            List.of(
                rec("The Truman Show", 1998, "Drama / Sci-Fi",
                    "A story about questioning everything you think you know — strangely clarifying when you feel lost", CHALLENGE),
                rec("Coherence", 2013, "Sci-Fi / Thriller",
                    "A dinner party that spirals into existential crisis — gripping, disorienting, and oddly comforting", CHALLENGE),
                rec("A Beautiful Mind", 2001, "Drama / Biopic",
                    "A story about a mind that can't trust itself — quietly illuminating when you feel similarly adrift", CHALLENGE)
            ),
            List.of(
                rec("BoJack Horseman", 2014, "Animated Drama",
                    "A deep exploration of identity and self-understanding — confusion is exactly the right state to begin", CHALLENGE),
                rec("Legion", 2017, "Sci-Fi / Drama",
                    "A visually extraordinary show where reality is genuinely uncertain — confusion is the whole aesthetic", ESCAPE),
                rec("Twin Peaks: The Return", 2017, "Mystery / Drama",
                    "David Lynch at his most surreal — the series that turned confusion into a legitimate art form", ESCAPE)
            ),
            List.of(
                rec("Being John Malkovich", 1999, "Comedy / Fantasy",
                    "Gleefully weird and oddly comforting — here confusion is the entire point and it works brilliantly", CHALLENGE),
                rec("Primer", 2004, "Sci-Fi",
                    "The most genuinely confusing time-travel film ever made — perfect company for a confused mind", CHALLENGE),
                rec("Adaptation", 2002, "Comedy / Drama",
                    "A film about the struggle to understand and create something — Kaufman's most weirdly comforting work", CHALLENGE)
            )
        ));

        map.put(Mood.PEACEFUL, recs(
            List.of(
                rec("Paterson", 2016, "Drama",
                    "A quiet film about the beauty hidden in everyday routines — meditative, gentle, and deeply calming", COMFORT),
                rec("The Straight Story", 1999, "Drama",
                    "David Lynch's most gentle film — an old man travels by lawnmower to see his brother — pure serenity", COMFORT),
                rec("Hunt for the Wilderpeople", 2016, "Comedy / Adventure",
                    "A warm, funny adventure through New Zealand's wilderness — endlessly charming and completely unhurried", ESCAPE)
            ),
            List.of(
                rec("Midnight Diner: Tokyo Stories", 2016, "Drama",
                    "A slow, warm Japanese anthology about a late-night diner — deeply human and completely unhurried", COMFORT),
                rec("Detectorists", 2014, "Comedy / Drama",
                    "Two men searching fields with metal detectors — a British gem that is slow, funny, and deeply peaceful", COMFORT),
                rec("Terrace House", 2012, "Reality",
                    "Japanese reality show about strangers living together — soft-spoken, gentle, and oddly meditative", COMFORT)
            ),
            List.of(
                rec("Moonrise Kingdom", 2012, "Romance / Adventure",
                    "A perfectly crafted, quietly magical story — like settling into a beautifully illustrated dream", ESCAPE),
                rec("Portrait of a Lady on Fire", 2019, "Romance / Drama",
                    "A slow-burning, achingly beautiful love story — meditative and visually stunning in every frame", ESCAPE),
                rec("Columbus", 2017, "Drama",
                    "A quiet film about architecture, grief, and unexpected connection — moves at exactly the right pace", CHALLENGE)
            )
        ));

        RECOMMENDATIONS = Collections.unmodifiableMap(map);
    }

    public Recommendation getRecommendation(
            Mood primary, Mood secondary,
            Format format, ContentType contentType, String excludedGenres,
            ExperienceType experienceType, String exclude) {
        try {
            String prompt = buildPrompt(primary, secondary, format, contentType, excludedGenres, experienceType, exclude);
            String raw = claudeApiClient.sendMessage(prompt);
            return parseApiResponse(raw);
        } catch (Exception e) {
            System.err.println("[MoodMatch] API call failed, using mock. Reason: " + e.getMessage());
            return getMockRecommendation(primary, secondary, contentType, experienceType, exclude);
        }
    }

    // ── Prompt construction ──────────────────────────────────────────────────

    private String buildPrompt(
            Mood primary, Mood secondary,
            Format format, ContentType contentType, String excludedGenres,
            ExperienceType experienceType, String exclude) {

        StringBuilder p = new StringBuilder();
        p.append("You are a film and television recommendation expert.\n");
        p.append("Recommend exactly one title that perfectly fits the following viewer profile.\n\n");

        p.append("EMOTIONAL STATE\n");
        p.append("Primary mood: ").append(primary.getDisplayName()).append("\n");
        if (secondary != null) {
            p.append("Secondary mood: ").append(secondary.getDisplayName())
             .append(" (both feelings are present simultaneously — the recommendation should speak to this blend)\n");
        }

        p.append("\nFORMAT\n");
        p.append("The viewer wants: ").append(format.getDisplayName())
         .append(" — ").append(format.getDescription()).append("\n");
        if (contentType != null) {
            switch (contentType) {
                case MOVIE  -> p.append("Structure: standalone film, ideally under 2.5 hours\n");
                case SERIES -> p.append("Structure: episodic series — the viewer is ready for a longer commitment\n");
                case EITHER -> p.append("Structure: no preference — movie or series, whichever fits better\n");
            }
        }
        // Extra constraints that each format implies
        switch (format) {
            case ANIME        -> p.append("Must be Japanese animation (anime).\n");
            case ANIMATION    -> p.append("Must be Western or non-Japanese animation (e.g. Pixar, DreamWorks, Studio Ghibli counts here too).\n");
            case DOCUMENTARY  -> p.append("Must be a documentary (feature or series) about real events or people.\n");
            case KOREAN_DRAMA -> p.append("Must be a Korean drama (K-drama).\n");
            case MINI_SERIES  -> p.append("Must be a limited series or mini-series with fewer than 10 episodes.\n");
            case CLASSIC      -> p.append("Must have been released before the year 2000.\n");
            case LATEST       -> p.append("Must have been released in 2021 or later.\n");
            default           -> {} // LIVE_ACTION: no extra constraint
        }

        p.append("\nDESIRED EXPERIENCE\n");
        p.append(experienceType.getDisplayName()).append(": ");
        switch (experienceType) {
            case COMFORT     -> p.append("Something warm, safe, and reassuring — the viewer needs to feel held and at ease\n");
            case CHALLENGE   -> p.append("Something intellectually or emotionally demanding — make them think hard or feel deeply\n");
            case ESCAPE      -> p.append("Fully transport them somewhere else — away from their current reality\n");
            case INSPIRATION -> p.append("Energising and motivating — something that stokes ambition or renews purpose\n");
        }

        if (excludedGenres != null && !excludedGenres.isBlank()) {
            String readable = Arrays.stream(excludedGenres.split(","))
                .map(g -> {
                    try { return Genre.valueOf(g.trim()).getDisplayName(); }
                    catch (Exception ex) { return g.trim(); }
                })
                .collect(java.util.stream.Collectors.joining(", "));
            p.append("\nEXCLUDED GENRES\n");
            p.append("HARD CONSTRAINT — do NOT recommend anything that contains these genres, even as a secondary genre: ")
             .append(readable).append("\n");
        }

        if (exclude != null && !exclude.isBlank()) {
            p.append("\nALREADY SEEN\n");
            p.append("Do NOT recommend \"").append(exclude).append("\" — the viewer has already seen it.\n");
        }

        p.append("\nRespond ONLY with valid JSON — no markdown, no explanation, no extra text:\n");
        p.append("{\n");
        p.append("  \"title\": \"exact official title\",\n");
        p.append("  \"year\": release_year_as_integer,\n");
        p.append("  \"genre\": \"Primary Genre / Secondary Genre\",\n");
        p.append("  \"reason\": \"2-3 sentences explaining why this fits the viewer's mood");
        if (secondary != null) p.append(" combination");
        p.append(", format preference, and desired experience\"\n}");

        return p.toString();
    }

    // ── API response parsing ─────────────────────────────────────────────────

    private Recommendation parseApiResponse(String raw) throws Exception {
        String json = raw.trim();
        int start = json.indexOf('{');
        int end   = json.lastIndexOf('}');
        if (start != -1 && end != -1) json = json.substring(start, end + 1);

        JsonNode node = MAPPER.readTree(json);
        return new Recommendation(
            node.get("title").asText(),
            node.get("year").asInt(),
            node.get("genre").asText(),
            node.get("reason").asText()
        );
    }

    // ── Mock fallback (used when API is unavailable) ─────────────────────────

    private Recommendation getMockRecommendation(
            Mood primary, Mood secondary, ContentType contentType,
            ExperienceType experienceType, String exclude) {

        List<Recommendation> primaryPool = RECOMMENDATIONS.get(primary).get(contentType);
        List<Recommendation> candidates  = new ArrayList<>(primaryPool);

        if (secondary != null) {
            List<Recommendation> secondaryPool = RECOMMENDATIONS.get(secondary).get(contentType);
            for (Recommendation r : secondaryPool) {
                if (r.getExperienceType() == experienceType
                        && primaryPool.stream().noneMatch(p -> p.getTitle().equals(r.getTitle()))) {
                    candidates.add(r);
                }
            }
        }

        List<Recommendation> byExperience = candidates.stream()
            .filter(r -> r.getExperienceType() == experienceType && !r.getTitle().equals(exclude))
            .collect(Collectors.toList());
        if (!byExperience.isEmpty()) return byExperience.get(RANDOM.nextInt(byExperience.size()));

        List<Recommendation> available = candidates.stream()
            .filter(r -> !r.getTitle().equals(exclude))
            .collect(Collectors.toList());
        if (!available.isEmpty()) return available.get(RANDOM.nextInt(available.size()));

        return primaryPool.get(RANDOM.nextInt(primaryPool.size()));
    }

    private static Recommendation rec(String title, int year, String genre, String reason, ExperienceType expType) {
        Recommendation r = new Recommendation(title, year, genre, reason);
        r.setExperienceType(expType);
        return r;
    }

    private static Map<ContentType, List<Recommendation>> recs(
            List<Recommendation> movies, List<Recommendation> series, List<Recommendation> either) {
        Map<ContentType, List<Recommendation>> m = new EnumMap<>(ContentType.class);
        m.put(ContentType.MOVIE, movies);
        m.put(ContentType.SERIES, series);
        m.put(ContentType.EITHER, either);
        return Collections.unmodifiableMap(m);
    }
}
