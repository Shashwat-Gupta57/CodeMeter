package dev.codemeter.core.storage;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Provides platform-specific storage paths.
 * Follows OS conventions for application data storage.
 */
public final class StoragePaths {

    private StoragePaths() {}

    /**
     * Returns the global application data directory.
     * Windows: %APPDATA%/CodeMeter
     * macOS:   ~/Library/Application Support/CodeMeter
     * Linux:   ~/.config/codemeter
     */
    public static Path globalDir() {
        String os = System.getProperty("os.name", "").toLowerCase();

        if (os.contains("win")) {
            String appData = System.getenv("APPDATA");
            if (appData != null) {
                return Paths.get(appData, "CodeMeter");
            }
            return Paths.get(System.getProperty("user.home"), "AppData", "Roaming", "CodeMeter");
        }

        if (os.contains("mac") || os.contains("darwin")) {
            return Paths.get(System.getProperty("user.home"), "Library", "Application Support", "CodeMeter");
        }

        // Linux and other Unix-like systems
        String xdgConfig = System.getenv("XDG_CONFIG_HOME");
        if (xdgConfig != null) {
            return Paths.get(xdgConfig, "codemeter");
        }
        return Paths.get(System.getProperty("user.home"), ".config", "codemeter");
    }

    /**
     * Returns the project-local storage directory.
     */
    public static Path projectDir(Path projectPath) {
        return projectPath.resolve(".codemeter");
    }

    // Global files
    public static Path configFile() { return globalDir().resolve("config.toml"); }
    public static Path projectsFile() { return globalDir().resolve("projects.json"); }
    public static Path globalHistoryFile() { return globalDir().resolve("global_history.json"); }
    public static Path recentFile() { return globalDir().resolve("recent.json"); }
    public static Path achievementsFile() { return globalDir().resolve("achievements.json"); }
    public static Path versionFile() { return globalDir().resolve("version.json"); }

    // Project files
    public static Path projectConfig(Path projectPath) { return projectDir(projectPath).resolve("project.toml"); }
    public static Path projectHistory(Path projectPath) { return projectDir(projectPath).resolve("history.json"); }
}
