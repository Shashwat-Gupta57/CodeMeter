package dev.codemeter.core.model;

/**
 * Defines all possible achievements in CodeMeter.
 * Each achievement has an icon, name, description, and unlock threshold.
 */
public enum AchievementType {
    // Scan milestones
    FIRST_SCAN("[*]", "First Scan", "Complete your first code scan", 1),
    EXPLORER("[o]", "Explorer", "Scan 5 different projects", 5),
    REPOSITORY_COLLECTOR("[+]", "Repository Collector", "Scan 10 different projects", 10),
    SURVEY_MASTER("[#]", "Survey Master", "Scan 25 different projects", 25),

    // File milestones
    HUNDRED_FILES("[C]", "Centurion", "Scan a project with 100+ files", 100),
    THOUSAND_FILES("[M]", "Library", "Scan a project with 1,000+ files", 1_000),
    TEN_THOUSAND_FILES("[X]", "Archive", "Scan a project with 10,000+ files", 10_000),
    HUNDRED_THOUSAND_FILES("[!]", "Metropolis", "Scan a project with 100,000+ files", 100_000),

    // LOC milestones
    THOUSAND_LINES("[1K]", "Getting Started", "Write 1,000 lines of code", 1_000),
    TEN_THOUSAND_LINES("[10K]", "Author", "Write 10,000 lines of code", 10_000),
    HUNDRED_THOUSAND_LINES("[100K]", "Book Author", "Write 100,000 lines of code", 100_000),
    MILLION_LINES("[1M]", "Novelist", "Write 1,000,000 lines of code", 1_000_000),
    TEN_MILLION_LINES("[10M]", "Encyclopedia", "Write 10,000,000 lines of code", 10_000_000),

    // Character milestones
    MILLION_CHARACTERS("[C]", "Million Characters", "Type 1,000,000 characters", 1_000_000),
    TEN_MILLION_CHARACTERS("[CC]", "Diamond Fingers", "Type 10,000,000 characters", 10_000_000),
    HUNDRED_MILLION_CHARACTERS("[CCC]", "Royal Typist", "Type 100,000,000 characters", 100_000_000),

    // Physical milestones
    FIRST_KILOMETER("[1km]", "First Kilometer", "Code stretches 1 kilometer", 1),
    MARATHON("[42km]", "Marathon Runner", "Code stretches a marathon (42.195 km)", 42),
    HUNDRED_KM("[100km]", "Century Rider", "Code stretches 100 kilometers", 100),

    // Height milestones
    MOUNTAIN_BUILDER("[^]", "Mountain Builder", "Printed code stacks to 1,000 meters", 1_000),
    PLANET_WALKER("[O]", "Planet Walker", "Character length exceeds 1% of Earth's circumference", 1),

    // Streak milestones
    CENTURY("[100]", "Century", "Perform 100 scans", 100),
    FIVE_HUNDRED("[500]", "Sharpshooter", "Perform 500 scans", 500),

    // Language milestones
    POLYGLOT("[P]", "Polyglot", "Have 5+ languages in a single project", 5),
    BABEL("[B]", "Tower of Babel", "Have 10+ languages in a single project", 10),

    // Growth milestones
    DOUBLE_UP("[2x]", "Double Up", "Double your codebase size between scans", 2),
    ROCKET("[10x]", "Rocket Growth", "10x your codebase size between scans", 10);

    private final String icon;
    private final String name;
    private final String description;
    private final long threshold;

    AchievementType(String icon, String name, String description, long threshold) {
        this.icon = icon;
        this.name = name;
        this.description = description;
        this.threshold = threshold;
    }

    public String icon() { return icon; }
    public String displayName() { return name; }
    public String description() { return description; }
    public long threshold() { return threshold; }
}
