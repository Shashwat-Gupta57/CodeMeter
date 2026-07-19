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
            
            toml.append("[typography]\n");
            toml.append("font_name = \"").append(settings.getFontName()).append("\"\n");
            toml.append("font_size_pt = ").append(settings.getFontSizePt()).append("\n");
            toml.append("character_width_mm = ").append(settings.getCharacterWidthMm()).append("\n");
            toml.append("character_height_mm = ").append(settings.getCharacterHeightMm()).append("\n");
            toml.append("line_spacing = ").append(settings.getLineSpacing()).append("\n");
            toml.append("tab_width_spaces = ").append(settings.getTabWidthSpaces()).append("\n");
            toml.append("characters_per_tab = ").append(settings.getCharactersPerTab()).append("\n\n");

            toml.append("[paper]\n");
            toml.append("paper_size = \"").append(settings.getPaperSize().name()).append("\"\n");
            toml.append("page_width_mm = ").append(settings.getPageWidthMm()).append("\n");
            toml.append("page_height_mm = ").append(settings.getPageHeightMm()).append("\n");
            toml.append("margin_top_mm = ").append(settings.getMarginTopMm()).append("\n");
            toml.append("margin_bottom_mm = ").append(settings.getMarginBottomMm()).append("\n");
            toml.append("margin_left_mm = ").append(settings.getMarginLeftMm()).append("\n");
            toml.append("margin_right_mm = ").append(settings.getMarginRightMm()).append("\n");
            toml.append("paper_weight_gsm = ").append(settings.getPaperWeightGsm()).append("\n");
            toml.append("paper_thickness_mm = ").append(settings.getPaperThicknessMm()).append("\n");
            toml.append("double_sided_printing = ").append(settings.isDoubleSidedPrinting()).append("\n");
            toml.append("binding_margin_mm = ").append(settings.getBindingMarginMm()).append("\n\n");

            toml.append("[printing]\n");
            toml.append("printer_dpi = ").append(settings.getPrinterDpi()).append("\n");
            toml.append("ink_coverage_percent = ").append(settings.getInkCoveragePercent()).append("\n");
            toml.append("printing_cost_per_page = ").append(settings.getPrintingCostPerPage()).append("\n");
            toml.append("binding_cost_per_book = ").append(settings.getBindingCostPerBook()).append("\n");
            toml.append("pages_per_book = ").append(settings.getPagesPerBook()).append("\n");
            toml.append("pages_per_printer_tray = ").append(settings.getPagesPerPrinterTray()).append("\n");
            toml.append("pages_per_box = ").append(settings.getPagesPerBox()).append("\n");
            toml.append("shelf_width_per_book_mm = ").append(settings.getShelfWidthPerBookMm()).append("\n");
            toml.append("average_print_speed_ppm = ").append(settings.getAveragePrintSpeedPpm()).append("\n\n");

            toml.append("[environment]\n");
            toml.append("tree_pages_per_tree = ").append(settings.getTreePagesPerTree()).append("\n");
            toml.append("co2_per_sheet_grams = ").append(settings.getCo2PerSheetGrams()).append("\n");
            toml.append("paper_recycling_factor = ").append(settings.getPaperRecyclingFactor()).append("\n\n");

            toml.append("[reading_writing]\n");
            toml.append("reading_speed_wpm = ").append(settings.getReadingSpeedWpm()).append("\n");
            toml.append("typing_speed_wpm = ").append(settings.getTypingSpeedWpm()).append("\n");
            toml.append("working_hours_per_day = ").append(settings.getWorkingHoursPerDay()).append("\n");
            toml.append("average_word_length = ").append(settings.getAverageWordLength()).append("\n");
            toml.append("average_sentence_length = ").append(settings.getAverageSentenceLength()).append("\n\n");

            toml.append("[distance]\n");
            toml.append("character_spacing_mm = ").append(settings.getCharacterSpacingMm()).append("\n");
            toml.append("space_character_width_mm = ").append(settings.getSpaceCharacterWidthMm()).append("\n");
            toml.append("tab_render_width_mm = ").append(settings.getTabRenderWidthMm()).append("\n\n");

            toml.append("[comparisons]\n");
            toml.append("comparison_style = \"").append(settings.getComparisonStyle()).append("\"\n");
            toml.append("comparison_units = \"").append(settings.getComparisonUnits()).append("\"\n");
            toml.append("measurement_system = \"").append(settings.getMeasurementSystem().name()).append("\"\n");
            toml.append("show_estimates = ").append(settings.isShowEstimates()).append("\n");
            toml.append("show_confidence_levels = ").append(settings.isShowConfidenceLevels()).append("\n");
            toml.append("show_fun_facts = ").append(settings.isShowFunFacts()).append("\n");
            toml.append("show_achievements = ").append(settings.isShowAchievements()).append("\n");
            toml.append("show_repository_comparisons = ").append(settings.isShowRepositoryComparisons()).append("\n");
            toml.append("show_git_statistics = ").append(settings.isShowGitStatistics()).append("\n\n");

            toml.append("[story]\n");
            toml.append("theme = \"").append(settings.getTheme().name()).append("\"\n");
            toml.append("story_density = \"").append(settings.getStoryDensity()).append("\"\n");
            toml.append("verbosity = \"").append(settings.getVerbosity()).append("\"\n");
            toml.append("whitespace_level = \"").append(settings.getWhitespaceLevel()).append("\"\n");
            toml.append("maximum_comparisons_per_section = ").append(settings.getMaximumComparisonsPerSection()).append("\n");
            toml.append("headline_strategy = \"").append(settings.getHeadlineStrategy().name()).append("\"\n\n");

            toml.append("[backend]\n");
            toml.append("scanner_backend = \"").append(settings.getScannerBackend().name()).append("\"\n");
            toml.append("git_integration = ").append(settings.isGitIntegration()).append("\n");
            toml.append("parallelism = ").append(settings.isParallelism()).append("\n");
            toml.append("cache_enabled = ").append(settings.isCacheEnabled()).append("\n");
            
            Files.writeString(file, toml.toString());
        } catch (IOException e) {
            System.err.println("Failed to save settings: " + e.getMessage());
        }
    }

    private void loadSettings() {
        try {
            Path file = StoragePaths.configFile();
            if (!Files.exists(file)) {
                saveSettings(); // Automatically generate it on first run
                return;
            }
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
                // Backend
                case "scanner_backend" -> settings.setScannerBackend(Settings.ScannerBackend.valueOf(value.toUpperCase()));
                case "git_integration" -> settings.setGitIntegration(Boolean.parseBoolean(value));
                case "parallelism" -> settings.setParallelism(Boolean.parseBoolean(value));
                case "cache_enabled" -> settings.setCacheEnabled(Boolean.parseBoolean(value));

                // Typography
                case "font_name" -> settings.setFontName(value);
                case "font_size_pt" -> settings.setFontSizePt(Integer.parseInt(value));
                case "character_width_mm" -> settings.setCharacterWidthMm(Double.parseDouble(value));
                case "character_height_mm" -> settings.setCharacterHeightMm(Double.parseDouble(value));
                case "line_spacing" -> settings.setLineSpacing(Double.parseDouble(value));
                case "tab_width_spaces" -> settings.setTabWidthSpaces(Integer.parseInt(value));
                case "characters_per_tab" -> settings.setCharactersPerTab(Integer.parseInt(value));

                // Paper
                case "paper_size" -> settings.setPaperSize(Settings.PaperSize.valueOf(value.toUpperCase()));
                case "page_width_mm" -> settings.setPageWidthMm(Double.parseDouble(value));
                case "page_height_mm" -> settings.setPageHeightMm(Double.parseDouble(value));
                case "margin_top_mm" -> settings.setMarginTopMm(Double.parseDouble(value));
                case "margin_bottom_mm" -> settings.setMarginBottomMm(Double.parseDouble(value));
                case "margin_left_mm" -> settings.setMarginLeftMm(Double.parseDouble(value));
                case "margin_right_mm" -> settings.setMarginRightMm(Double.parseDouble(value));
                case "paper_weight_gsm" -> settings.setPaperWeightGsm(Double.parseDouble(value));
                case "paper_thickness_mm" -> settings.setPaperThicknessMm(Double.parseDouble(value));
                case "double_sided_printing" -> settings.setDoubleSidedPrinting(Boolean.parseBoolean(value));
                case "binding_margin_mm" -> settings.setBindingMarginMm(Double.parseDouble(value));

                // Printing
                case "printer_dpi" -> settings.setPrinterDpi(Integer.parseInt(value));
                case "ink_coverage_percent" -> settings.setInkCoveragePercent(Double.parseDouble(value));
                case "printing_cost_per_page" -> settings.setPrintingCostPerPage(Double.parseDouble(value));
                case "binding_cost_per_book" -> settings.setBindingCostPerBook(Double.parseDouble(value));
                case "pages_per_book" -> settings.setPagesPerBook(Integer.parseInt(value));
                case "pages_per_printer_tray" -> settings.setPagesPerPrinterTray(Integer.parseInt(value));
                case "pages_per_box" -> settings.setPagesPerBox(Integer.parseInt(value));
                case "shelf_width_per_book_mm" -> settings.setShelfWidthPerBookMm(Double.parseDouble(value));
                case "average_print_speed_ppm" -> settings.setAveragePrintSpeedPpm(Double.parseDouble(value));

                // Environment
                case "tree_pages_per_tree" -> settings.setTreePagesPerTree(Double.parseDouble(value));
                case "co2_per_sheet_grams" -> settings.setCo2PerSheetGrams(Double.parseDouble(value));
                case "paper_recycling_factor" -> settings.setPaperRecyclingFactor(Double.parseDouble(value));

                // Reading & Writing
                case "reading_speed_wpm" -> settings.setReadingSpeedWpm(Double.parseDouble(value));
                case "typing_speed_wpm" -> settings.setTypingSpeedWpm(Double.parseDouble(value));
                case "working_hours_per_day" -> settings.setWorkingHoursPerDay(Double.parseDouble(value));
                case "average_word_length" -> settings.setAverageWordLength(Double.parseDouble(value));
                case "average_sentence_length" -> settings.setAverageSentenceLength(Double.parseDouble(value));

                // Distance Calculations
                case "character_spacing_mm" -> settings.setCharacterSpacingMm(Double.parseDouble(value));
                case "space_character_width_mm" -> settings.setSpaceCharacterWidthMm(Double.parseDouble(value));
                case "tab_render_width_mm" -> settings.setTabRenderWidthMm(Double.parseDouble(value));

                // Comparisons
                case "comparison_style" -> settings.setComparisonStyle(value);
                case "comparison_units" -> settings.setComparisonUnits(value);
                case "measurement_system" -> settings.setMeasurementSystem(Settings.MeasurementSystem.valueOf(value.toUpperCase()));
                case "show_estimates" -> settings.setShowEstimates(Boolean.parseBoolean(value));
                case "show_confidence_levels" -> settings.setShowConfidenceLevels(Boolean.parseBoolean(value));
                case "show_fun_facts" -> settings.setShowFunFacts(Boolean.parseBoolean(value));
                case "show_achievements" -> settings.setShowAchievements(Boolean.parseBoolean(value));
                case "show_repository_comparisons" -> settings.setShowRepositoryComparisons(Boolean.parseBoolean(value));
                case "show_git_statistics" -> settings.setShowGitStatistics(Boolean.parseBoolean(value));

                // Story
                case "theme" -> settings.setTheme(Settings.ThemeMode.valueOf(value.toUpperCase()));
                case "story_density" -> settings.setStoryDensity(value);
                case "verbosity" -> settings.setVerbosity(value);
                case "whitespace_level" -> settings.setWhitespaceLevel(value);
                case "maximum_comparisons_per_section" -> settings.setMaximumComparisonsPerSection(Integer.parseInt(value));
                case "headline_strategy" -> settings.setHeadlineStrategy(Settings.HeadlineStrategy.valueOf(value.toUpperCase()));
            }
        } catch (Exception ignored) {
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
