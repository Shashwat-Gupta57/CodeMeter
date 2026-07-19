package dev.codemeter.core.model;

/**
 * Metrics related to printing the codebase on physical paper.
 * All values depend on configurable print settings.
 */
public record PrintedMetrics(
        // Paper configuration used
        String paperSize,
        String fontName,
        int fontSize,
        double lineSpacing,
        String marginType,
        double paperThicknessMm,
        String inkType,

        // Calculated values
        long totalPages,
        int linesPerPage,
        int charsPerLine,

        // Physical measurements
        double stackHeightCm,
        double stackHeightInches,
        double totalWeightKg,
        double totalWeightLbs,

        // Ink usage
        double inkMl,
        double inkCartridges,

        // Cost estimates
        double paperCostUsd,
        double inkCostUsd,
        double totalCostUsd,

        // Time estimates
        double printTimeMinutes,
        double readingTimeHours,
        double typingTimeHours
) {}
