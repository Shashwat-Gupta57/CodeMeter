package dev.codemeter.core.model;

import java.time.Instant;

/**
 * Represents a single entry in scan history for a project.
 */
public record HistoryEntry(
        String projectPath,
        String projectName,
        Instant timestamp,
        long totalFiles,
        long totalCodeLines,
        long totalCommentLines,
        long totalBlankLines,
        long totalCharacters,
        long totalBytes,
        int languageCount,
        String dominantLanguage
) {
    /**
     * Creates a HistoryEntry from a ScanResult.
     */
    public static HistoryEntry from(ScanResult result) {
        return new HistoryEntry(
                result.projectPath(),
                result.projectName(),
                Instant.ofEpochMilli(result.timestamp()),
                result.totalFiles(),
                result.totalCodeLines(),
                result.totalCommentLines(),
                result.totalBlankLines(),
                result.totalCharacters(),
                result.totalBytes(),
                result.languageCount(),
                result.dominantLanguage()
        );
    }

    public long totalLoc() {
        return totalCodeLines + totalCommentLines + totalBlankLines;
    }
}
