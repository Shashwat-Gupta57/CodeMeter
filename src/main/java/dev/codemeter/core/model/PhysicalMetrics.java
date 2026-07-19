package dev.codemeter.core.model;

/**
 * Physical-world metrics derived from code measurements.
 * Converts abstract code quantities into tangible real-world equivalents.
 */
public record PhysicalMetrics(
        // Character dimensions (assuming average char width ~2.5mm in monospace)
        double characterLengthKm,
        double characterLengthMiles,

        // Vertical stack (printed pages stacked, ~0.1mm per sheet)
        double verticalStackMeters,
        double verticalStackFeet,

        // Horizontal length (all lines end to end)
        double horizontalLengthKm,
        double horizontalLengthMiles,

        // Area comparisons
        double footballFields,
        double cricketGrounds,
        double basketballCourts,
        double tennisCourts,
        double olympicSwimmingPools,

        // Height comparisons
        double burjKhalifas,
        double empireStateBuildings,
        double eiffelTowers,
        double mountEverests,

        // Distance comparisons
        double earthCircumferencePercent,
        double moonDistancePercent,
        double marathons,
        double centralParkLoops,

        // Paper & printing
        double treesRequired,
        double shelfWidthMeters,
        double shelfWidthFeet,
        double estimatedWeightKg,
        double estimatedWeightLbs,
        double printerTrays,
        double bookshelves,
        double sheetsRequired,
        double doubleSidedPages,
        double booksRequired,
        double bindersRequired,
        double boxesNeeded,
        double inkEstimationLiters,
        double timeToPrintMinutes,
        double estimatedPrintingCost,

        // Pages
        long totalPages,
        
        // Memory & Complexity Estimations
        long estimatedUtf8Size,
        long estimatedUtf16Size,
        long estimatedTokenCount,
        long estimatedAstNodes
) {}
