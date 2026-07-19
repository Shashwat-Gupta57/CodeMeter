package dev.codemeter.core.model;

/**
 * Statistics for a single programming language within a scan result.
 */
public record LanguageStats(
        String language,
        long files,
        long codeLines,
        long commentLines,
        long blankLines,
        long totalLines,
        long bytes,
        double complexity
) {
    /**
     * Percentage of total code lines.
     */
    public double percentageOf(long totalCode) {
        if (totalCode == 0) return 0;
        return (codeLines * 100.0) / totalCode;
    }
}
