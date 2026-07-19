package dev.codemeter.core.metrics;

import dev.codemeter.core.model.ScanResult;

/**
 * Computes comparison metrics between two scan results.
 */
public record ComparisonResult(
        long addedLoc,
        long removedLoc,
        long netLoc,
        long addedCharacters,
        long addedFiles,
        double growthPercent,
        int addedLanguages,
        int removedLanguages
) {
    /**
     * Compares current scan with previous scan.
     */
    public static ComparisonResult compare(ScanResult current, ScanResult previous) {
        long currentLoc = current.totalCodeLines();
        long previousLoc = previous.totalCodeLines();

        long netLoc = currentLoc - previousLoc;
        long addedLoc = Math.max(0, netLoc);
        long removedLoc = Math.max(0, -netLoc);

        long addedChars = current.totalCharacters() - previous.totalCharacters();
        long addedFiles = current.totalFiles() - previous.totalFiles();

        double growthPercent = previousLoc > 0
                ? ((double) netLoc / previousLoc) * 100
                : (currentLoc > 0 ? 100.0 : 0.0);

        int currentLangs = current.languageCount();
        int previousLangs = previous.languageCount();
        int addedLangs = Math.max(0, currentLangs - previousLangs);
        int removedLangs = Math.max(0, previousLangs - currentLangs);

        return new ComparisonResult(
                addedLoc, removedLoc, netLoc,
                addedChars, addedFiles, growthPercent,
                addedLangs, removedLangs
        );
    }

    /**
     * Returns a human-readable growth description.
     */
    public String growthDescription() {
        if (growthPercent > 0) {
            return String.format("+%.1f%% growth", growthPercent);
        } else if (growthPercent < 0) {
            return String.format("%.1f%% reduction", growthPercent);
        } else {
            return "No change";
        }
    }
}
