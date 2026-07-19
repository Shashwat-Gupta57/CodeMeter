package dev.codemeter.core.model;

import java.util.List;
import java.util.Map;

/**
 * Represents the result of scanning a codebase.
 * Aggregates all language statistics and provides summary metrics.
 */
public record ScanResult(
        String projectPath,
        String projectName,
        long timestamp,
        long totalFiles,
        long totalDirectories,
        long totalCodeLines,
        long totalCommentLines,
        long totalBlankLines,
        long totalLines,
        long totalCharacters,
        long totalWords,
        long totalBytes,
        String largestFile,
        long largestFileLines,
        double averageFileSize,
        double averageLineLength,
        List<LanguageStats> languages,
        Map<String, Long> filesByExtension
) {
    /**
     * Total LOC = code + comments + blanks.
     */
    public long totalLoc() {
        return totalCodeLines + totalCommentLines + totalBlankLines;
    }

    /**
     * Returns the dominant language by code line count.
     */
    public String dominantLanguage() {
        return languages.stream()
                .max((a, b) -> Long.compare(a.codeLines(), b.codeLines()))
                .map(LanguageStats::language)
                .orElse("Unknown");
    }

    /**
     * Returns number of distinct languages.
     */
    public int languageCount() {
        return languages.size();
    }
}
