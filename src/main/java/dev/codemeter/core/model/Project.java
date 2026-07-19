package dev.codemeter.core.model;

import java.time.Instant;

/**
 * Represents a tracked project in the system.
 */
public record Project(
        String name,
        String path,
        Instant firstScanned,
        Instant lastScanned,
        int scanCount,
        long lastLoc,
        long lastFiles,
        String dominantLanguage
) {
    /**
     * Creates a Project entry from a scan result.
     */
    public static Project from(ScanResult result) {
        Instant now = Instant.ofEpochMilli(result.timestamp());
        return new Project(
                result.projectName(),
                result.projectPath(),
                now,
                now,
                1,
                result.totalCodeLines(),
                result.totalFiles(),
                result.dominantLanguage()
        );
    }

    /**
     * Returns an updated project after a new scan.
     */
    public Project withNewScan(ScanResult result) {
        return new Project(
                name,
                path,
                firstScanned,
                Instant.ofEpochMilli(result.timestamp()),
                scanCount + 1,
                result.totalCodeLines(),
                result.totalFiles(),
                result.dominantLanguage()
        );
    }
}
