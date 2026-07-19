package dev.codemeter.core.metrics;

import dev.codemeter.core.model.PhysicalMetrics;
import dev.codemeter.core.model.ScanResult;
import dev.codemeter.core.model.Settings;

/**
 * Calculates physical-world metrics from scan results.
 * Converts abstract code quantities into tangible real-world equivalents.
 */
public final class PhysicalCalculator {

    // Character dimensions (monospace font ~2.5mm per character)
    private static final double CHAR_WIDTH_MM = 2.5;

    // Paper dimensions (A4 default)
    private static final double DEFAULT_LINES_PER_PAGE = 55;
    private static final double SHEET_THICKNESS_MM = 0.1;
    private static final double SHEET_WEIGHT_GRAMS = 5.0; // ~80 gsm A4

    // Real-world reference measurements
    private static final double FOOTBALL_FIELD_M = 100.0; // FIFA standard length
    private static final double FOOTBALL_FIELD_AREA_SQM = 7140.0; // 105m x 68m
    private static final double CRICKET_GROUND_AREA_SQM = 15393.0; // ~70m radius circle
    private static final double BASKETBALL_COURT_AREA_SQM = 420.0; // 28m x 15m
    private static final double TENNIS_COURT_AREA_SQM = 260.87; // 23.77m x 10.97m
    private static final double OLYMPIC_POOL_AREA_SQM = 1250.0; // 50m x 25m

    private static final double BURJ_KHALIFA_M = 828.0;
    private static final double EMPIRE_STATE_M = 443.0;
    private static final double EIFFEL_TOWER_M = 330.0;
    private static final double MOUNT_EVEREST_M = 8848.86;

    private static final double EARTH_CIRCUMFERENCE_KM = 40075.0;
    private static final double MOON_DISTANCE_KM = 384400.0;
    private static final double MARATHON_KM = 42.195;
    private static final double CENTRAL_PARK_LOOP_KM = 10.0;

    // Paper and printing
    private static final double TREE_PAGES = 8333.0; // One tree ≈ 8,333 pages
    private static final double SHELF_WIDTH_PER_PAGE_MM = 0.1; // Sheet thickness
    private static final double PRINTER_TRAY_PAGES = 500.0;
    private static final double BOOKSHELF_WIDTH_M = 0.9; // Standard bookshelf width

    private PhysicalCalculator() {}

    /**
     * Calculate all physical metrics from a scan result.
     */
    public static PhysicalMetrics calculate(ScanResult result, Settings settings) {
        long totalChars = result.totalCharacters() > 0 ? result.totalCharacters() : result.totalBytes();
        long totalLines = result.totalLines();
        double avgLineLength = result.averageLineLength() > 0 ? result.averageLineLength() : 40;

        // Character length: total characters * char width
        double charLengthMm = totalChars * CHAR_WIDTH_MM;
        double charLengthKm = charLengthMm / 1_000_000.0;
        double charLengthMiles = charLengthKm * 0.621371;

        // Vertical stack: pages stacked
        double linesPerPage = DEFAULT_LINES_PER_PAGE;
        long totalPages = (long) Math.ceil((double) totalLines / linesPerPage);
        double thickness = settings != null ? settings.getPaperThicknessMm() : SHEET_THICKNESS_MM;
        double verticalStackMm = totalPages * thickness;
        double verticalStackMeters = verticalStackMm / 1000.0;
        double verticalStackFeet = verticalStackMeters * 3.28084;

        // Horizontal length: all lines end to end
        double horizontalLengthMm = totalLines * avgLineLength * CHAR_WIDTH_MM;
        double horizontalLengthKm = horizontalLengthMm / 1_000_000.0;
        double horizontalLengthMiles = horizontalLengthKm * 0.621371;

        // Area comparisons (using character length as a linear measure)
        double totalLengthM = charLengthKm * 1000;
        double footballFields = totalLengthM / FOOTBALL_FIELD_M;
        double cricketGrounds = totalLengthM / Math.sqrt(CRICKET_GROUND_AREA_SQM);
        double basketballCourts = totalLengthM / 28.0;
        double tennisCourts = totalLengthM / 23.77;
        double olympicPools = totalLengthM / 50.0;

        // Height comparisons (using vertical stack)
        double burjKhalifas = verticalStackMeters / BURJ_KHALIFA_M;
        double empireStates = verticalStackMeters / EMPIRE_STATE_M;
        double eiffelTowers = verticalStackMeters / EIFFEL_TOWER_M;
        double mountEverests = verticalStackMeters / MOUNT_EVEREST_M;

        // Distance comparisons (using character length)
        double earthPercent = (charLengthKm / EARTH_CIRCUMFERENCE_KM) * 100;
        double moonPercent = (charLengthKm / MOON_DISTANCE_KM) * 100;
        double marathons = charLengthKm / MARATHON_KM;
        double centralParkLoops = charLengthKm / CENTRAL_PARK_LOOP_KM;

        // Paper & printing
        double treesRequired = totalPages / TREE_PAGES;
        double shelfWidthMm = totalPages * SHELF_WIDTH_PER_PAGE_MM;
        double shelfWidthMeters = shelfWidthMm / 1000.0;
        double shelfWidthFeet = shelfWidthMeters * 3.28084;
        double weightKg = (totalPages * SHEET_WEIGHT_GRAMS) / 1000.0;
        double weightLbs = weightKg * 2.20462;
        double printerTrays = totalPages / PRINTER_TRAY_PAGES;
        double bookshelves = shelfWidthMeters / BOOKSHELF_WIDTH_M;

        return new PhysicalMetrics(
                charLengthKm, charLengthMiles,
                verticalStackMeters, verticalStackFeet,
                horizontalLengthKm, horizontalLengthMiles,
                footballFields, cricketGrounds, basketballCourts,
                tennisCourts, olympicPools,
                burjKhalifas, empireStates, eiffelTowers, mountEverests,
                earthPercent, moonPercent, marathons, centralParkLoops,
                treesRequired, shelfWidthMeters, shelfWidthFeet,
                weightKg, weightLbs, printerTrays, bookshelves,
                totalPages
        );
    }

    /**
     * Format a value with appropriate unit prefix.
     */
    public static String formatMetric(double value, String unit) {
        if (value >= 1_000_000) {
            return String.format("%.2fM %s", value / 1_000_000, unit);
        } else if (value >= 1_000) {
            return String.format("%.1fK %s", value / 1_000, unit);
        } else if (value >= 1) {
            return String.format("%.1f %s", value, unit);
        } else if (value >= 0.01) {
            return String.format("%.3f %s", value, unit);
        } else if (value > 0) {
            return String.format("%.6f %s", value, unit);
        } else {
            return "0 " + unit;
        }
    }

    /**
     * Format a large number with commas.
     */
    public static String formatNumber(long value) {
        return String.format("%,d", value);
    }
}
