package dev.codemeter.core.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.codemeter.core.model.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages all persistent storage for CodeMeter.
 * Handles global config, project data, history, achievements, and recent projects.
 */
public class StorageManager {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .create();

    private Settings settings;
    private List<Project> projects;
    private List<HistoryEntry> globalHistory;
    private List<String> recentPaths;
    private Map<String, Achievement> achievements;

    public StorageManager() {
        this.settings = new Settings();
        this.projects = new ArrayList<>();
        this.globalHistory = new ArrayList<>();
        this.recentPaths = new ArrayList<>();
        this.achievements = new LinkedHashMap<>();
        initializeAchievements();
    }

    /**
     * Initialize default achievement states.
     */
    private void initializeAchievements() {
        for (AchievementType type : AchievementType.values()) {
            achievements.put(type.name(), Achievement.locked(type));
        }
    }

    /**
     * Load all data from disk.
     */
    public void load() {
        ensureDirectories();
        loadSettings();
        loadProjects();
        loadHistory();
        loadRecent();
        loadAchievements();
    }

    /**
     * Save all data to disk.
     */
    public void save() {
        ensureDirectories();
        saveSettings();
        saveProjects();
        saveHistory();
        saveRecent();
        saveAchievements();
    }

    // -- Settings --

    public Settings getSettings() {
        return settings;
    }

    public void saveSettings() {
        try {
            Path file = StoragePaths.configFile();
            StringBuilder toml = new StringBuilder();
            toml.append("# CodeMeter Configuration\n\n");
            toml.append("[theme]\n");
            toml.append("mode = \"").append(settings.getTheme().name().toLowerCase()).append("\"\n");
            toml.append("animations = ").append(settings.isAnimationsEnabled()).append("\n\n");
            toml.append("[measurement]\n");
            toml.append("system = \"").append(settings.getMeasurement().name().toLowerCase()).append("\"\n\n");
            toml.append("[print]\n");
            toml.append("paper_size = \"").append(settings.getPaperSize().name()).append("\"\n");
            toml.append("margin_type = \"").append(settings.getMarginType().name().toLowerCase()).append("\"\n");
            toml.append("font = \"").append(settings.getFontName()).append("\"\n");
            toml.append("font_size = ").append(settings.getFontSize()).append("\n");
            toml.append("line_spacing = ").append(settings.getLineSpacing()).append("\n");
            toml.append("ink_type = \"").append(settings.getInkType().name().toLowerCase()).append("\"\n");
            toml.append("paper_thickness_mm = ").append(settings.getPaperThicknessMm()).append("\n\n");
            toml.append("[features]\n");
            toml.append("comparison_objects = ").append(settings.isComparisonObjectsEnabled()).append("\n");
            toml.append("history = ").append(settings.isHistoryEnabled()).append("\n");
            Files.writeString(file, toml.toString());
        } catch (IOException e) {
            System.err.println("Failed to save settings: " + e.getMessage());
        }
    }

    private void loadSettings() {
        try {
            Path file = StoragePaths.configFile();
            if (!Files.exists(file)) return;
            // Simple TOML parsing for our known keys
            String content = Files.readString(file);
            for (String line : content.split("\n")) {
                line = line.trim();
                if (line.startsWith("#") || line.startsWith("[") || line.isEmpty()) continue;
                String[] parts = line.split("=", 2);
                if (parts.length != 2) continue;
                String key = parts[0].trim();
                String value = parts[1].trim().replace("\"", "");
                applySettingValue(key, value);
            }
        } catch (IOException e) {
            // Use defaults
        }
    }

    private void applySettingValue(String key, String value) {
        try {
            switch (key) {
                case "mode" -> settings.setTheme(Settings.ThemeMode.valueOf(value.toUpperCase()));
                case "animations" -> settings.setAnimationsEnabled(Boolean.parseBoolean(value));
                case "system" -> settings.setMeasurement(Settings.MeasurementSystem.valueOf(value.toUpperCase()));
                case "paper_size" -> settings.setPaperSize(Settings.PaperSize.valueOf(value.toUpperCase()));
                case "margin_type" -> settings.setMarginType(Settings.MarginType.valueOf(value.toUpperCase()));
                case "font" -> settings.setFontName(value);
                case "font_size" -> settings.setFontSize(Integer.parseInt(value));
                case "line_spacing" -> settings.setLineSpacing(Double.parseDouble(value));
                case "ink_type" -> settings.setInkType(Settings.InkType.valueOf(value.toUpperCase()));
                case "paper_thickness_mm" -> settings.setPaperThicknessMm(Double.parseDouble(value));
                case "comparison_objects" -> settings.setComparisonObjectsEnabled(Boolean.parseBoolean(value));
                case "history" -> settings.setHistoryEnabled(Boolean.parseBoolean(value));
            }
        } catch (IllegalArgumentException ignored) {
            // Skip invalid values
        }
    }

    // -- Projects --

    public List<Project> getProjects() {
        return Collections.unmodifiableList(projects);
    }

    public Optional<Project> getProject(String path) {
        return projects.stream()
                .filter(p -> p.path().equals(path))
                .findFirst();
    }

    public void addOrUpdateProject(Project project) {
        projects.removeIf(p -> p.path().equals(project.path()));
        projects.add(0, project);
        saveProjects();
    }

    private void saveProjects() {
        writeJson(StoragePaths.projectsFile(), projects);
    }

    private void loadProjects() {
        Type type = new TypeToken<List<Project>>() {}.getType();
        List<Project> loaded = readJson(StoragePaths.projectsFile(), type);
        if (loaded != null) {
            this.projects = new ArrayList<>(loaded);
        }
    }

    // -- History --

    public List<HistoryEntry> getGlobalHistory() {
        return Collections.unmodifiableList(globalHistory);
    }

    public List<HistoryEntry> getProjectHistory(String projectPath) {
        return globalHistory.stream()
                .filter(h -> h.projectPath().equals(projectPath))
                .collect(Collectors.toList());
    }

    public void addHistoryEntry(HistoryEntry entry) {
        globalHistory.add(0, entry);
        saveHistory();
        // Also save to project-local history
        saveProjectHistory(entry);
    }

    private void saveHistory() {
        writeJson(StoragePaths.globalHistoryFile(), globalHistory);
    }

    private void loadHistory() {
        Type type = new TypeToken<List<HistoryEntry>>() {}.getType();
        List<HistoryEntry> loaded = readJson(StoragePaths.globalHistoryFile(), type);
        if (loaded != null) {
            this.globalHistory = new ArrayList<>(loaded);
        }
    }

    private void saveProjectHistory(HistoryEntry entry) {
        try {
            Path projectPath = Path.of(entry.projectPath());
            Path historyFile = StoragePaths.projectHistory(projectPath);
            Files.createDirectories(historyFile.getParent());

            Type type = new TypeToken<List<HistoryEntry>>() {}.getType();
            List<HistoryEntry> projectHistory = readJson(historyFile, type);
            if (projectHistory == null) projectHistory = new ArrayList<>();
            projectHistory.add(0, entry);
            writeJson(historyFile, projectHistory);
        } catch (IOException e) {
            // Non-critical, skip
        }
    }

    // -- Recent --

    public List<String> getRecentPaths() {
        return Collections.unmodifiableList(recentPaths);
    }

    public void addRecentPath(String path) {
        recentPaths.remove(path);
        recentPaths.add(0, path);
        if (recentPaths.size() > 20) {
            recentPaths = new ArrayList<>(recentPaths.subList(0, 20));
        }
        saveRecent();
    }

    private void saveRecent() {
        writeJson(StoragePaths.recentFile(), recentPaths);
    }

    private void loadRecent() {
        Type type = new TypeToken<List<String>>() {}.getType();
        List<String> loaded = readJson(StoragePaths.recentFile(), type);
        if (loaded != null) {
            this.recentPaths = new ArrayList<>(loaded);
        }
    }

    // -- Achievements --

    public Map<String, Achievement> getAchievements() {
        return Collections.unmodifiableMap(achievements);
    }

    public List<Achievement> getUnlockedAchievements() {
        return achievements.values().stream()
                .filter(Achievement::unlocked)
                .collect(Collectors.toList());
    }

    public List<Achievement> checkAndUpdateAchievements(ScanResult result) {
        List<Achievement> newlyUnlocked = new ArrayList<>();

        // File milestones
        checkAchievement(AchievementType.HUNDRED_FILES, result.totalFiles(), newlyUnlocked);
        checkAchievement(AchievementType.THOUSAND_FILES, result.totalFiles(), newlyUnlocked);
        checkAchievement(AchievementType.TEN_THOUSAND_FILES, result.totalFiles(), newlyUnlocked);
        checkAchievement(AchievementType.HUNDRED_THOUSAND_FILES, result.totalFiles(), newlyUnlocked);

        // LOC milestones
        checkAchievement(AchievementType.THOUSAND_LINES, result.totalCodeLines(), newlyUnlocked);
        checkAchievement(AchievementType.TEN_THOUSAND_LINES, result.totalCodeLines(), newlyUnlocked);
        checkAchievement(AchievementType.HUNDRED_THOUSAND_LINES, result.totalCodeLines(), newlyUnlocked);
        checkAchievement(AchievementType.MILLION_LINES, result.totalCodeLines(), newlyUnlocked);
        checkAchievement(AchievementType.TEN_MILLION_LINES, result.totalCodeLines(), newlyUnlocked);

        // Character milestones
        long chars = result.totalCharacters() > 0 ? result.totalCharacters() : result.totalBytes();
        checkAchievement(AchievementType.MILLION_CHARACTERS, chars, newlyUnlocked);
        checkAchievement(AchievementType.TEN_MILLION_CHARACTERS, chars, newlyUnlocked);
        checkAchievement(AchievementType.HUNDRED_MILLION_CHARACTERS, chars, newlyUnlocked);

        // Language milestones
        checkAchievement(AchievementType.POLYGLOT, result.languageCount(), newlyUnlocked);
        checkAchievement(AchievementType.BABEL, result.languageCount(), newlyUnlocked);

        // First scan
        checkAchievement(AchievementType.FIRST_SCAN, 1, newlyUnlocked);

        // Project count
        int projectCount = projects.size();
        checkAchievement(AchievementType.EXPLORER, projectCount, newlyUnlocked);
        checkAchievement(AchievementType.REPOSITORY_COLLECTOR, projectCount, newlyUnlocked);
        checkAchievement(AchievementType.SURVEY_MASTER, projectCount, newlyUnlocked);

        // Scan count
        int scanCount = globalHistory.size();
        checkAchievement(AchievementType.CENTURY, scanCount, newlyUnlocked);
        checkAchievement(AchievementType.FIVE_HUNDRED, scanCount, newlyUnlocked);

        // Physical milestones (need to calculate)
        double charLengthKm = (chars * 2.5) / 1_000_000.0;
        checkAchievement(AchievementType.FIRST_KILOMETER, (long) charLengthKm, newlyUnlocked);
        checkAchievement(AchievementType.MARATHON, (long) charLengthKm, newlyUnlocked);
        checkAchievement(AchievementType.HUNDRED_KM, (long) charLengthKm, newlyUnlocked);

        // Stack height
        double stackHeightM = (result.totalLines() / 55.0) * 0.1 / 1000.0;
        checkAchievement(AchievementType.MOUNTAIN_BUILDER, (long) stackHeightM, newlyUnlocked);

        // Earth circumference
        double earthPercent = (charLengthKm / 40075.0) * 100;
        checkAchievement(AchievementType.PLANET_WALKER, (long) earthPercent, newlyUnlocked);

        if (!newlyUnlocked.isEmpty()) {
            saveAchievements();
        }

        return newlyUnlocked;
    }

    private void checkAchievement(AchievementType type, long currentValue, List<Achievement> newlyUnlocked) {
        Achievement current = achievements.get(type.name());
        if (current != null && !current.unlocked()) {
            Achievement updated = current.withProgress(currentValue);
            achievements.put(type.name(), updated);
            if (updated.unlocked()) {
                newlyUnlocked.add(updated);
            }
        }
    }

    private void saveAchievements() {
        writeJson(StoragePaths.achievementsFile(), achievements);
    }

    private void loadAchievements() {
        try {
            Path file = StoragePaths.achievementsFile();
            if (!Files.exists(file)) return;
            String json = Files.readString(file);
            Type type = new TypeToken<Map<String, Achievement>>() {}.getType();
            Map<String, Achievement> loaded = GSON.fromJson(json, type);
            if (loaded != null) {
                // Merge with defaults (in case new achievements were added)
                for (AchievementType at : AchievementType.values()) {
                    if (loaded.containsKey(at.name())) {
                        achievements.put(at.name(), loaded.get(at.name()));
                    }
                }
            }
        } catch (Exception e) {
            // Use defaults
        }
    }

    // -- Utility --

    private void ensureDirectories() {
        try {
            Files.createDirectories(StoragePaths.globalDir());
        } catch (IOException e) {
            System.err.println("Failed to create storage directory: " + e.getMessage());
        }
    }

    private <T> void writeJson(Path file, T data) {
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, GSON.toJson(data));
        } catch (IOException e) {
            System.err.println("Failed to write " + file + ": " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T readJson(Path file, Type type) {
        try {
            if (!Files.exists(file)) return null;
            String json = Files.readString(file);
            return GSON.fromJson(json, type);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get total scan count across all projects.
     */
    public int getTotalScanCount() {
        return globalHistory.size();
    }

    /**
     * Get total projects tracked.
     */
    public int getProjectCount() {
        return projects.size();
    }
}
