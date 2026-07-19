package dev.codemeter.core.metrics;

import dev.codemeter.core.model.PhysicalMetrics;
import dev.codemeter.core.model.ScanResult;
import dev.codemeter.core.model.Settings;

/**
 * Calculates physical-world metrics from scan results.
 * Converts abstract code quantities into tangible real-world equivalents.
 */
public final class PhysicalCalculator {

    // Real-world reference measurements
    private static final double FOOTBALL_FIELD_M = 100.0; // FIFA standard length
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

    private PhysicalCalculator() {}

    /**
     * Calculate all physical metrics from a scan result.
     */
    public static PhysicalMetrics calculate(ScanResult result, Settings settings) {
        long totalChars = result.totalCharacters() > 0 ? result.totalCharacters() : result.totalBytes();
        long totalLines = result.totalLines();
        double avgLineLength = result.averageLineLength() > 0 ? result.averageLineLength() : 40;

        // Character length: total characters * char width
        double charWidthMm = settings != null ? settings.getCharacterWidthMm() : 1.5;
        double charLengthMm = totalChars * charWidthMm;
        double charLengthKm = charLengthMm / 1_000_000.0;
        double charLengthMiles = charLengthKm * 0.621371;

        // Vertical stack: pages stacked
        double printableHeightMm = settings != null ? settings.getPrintableHeightMm() : 246.2;
        double lineHeightMm = settings != null ? (settings.getFontSizePt() * 0.3528 * settings.getLineSpacing()) : 4.5;
        double linesPerPage = Math.max(1.0, Math.floor(printableHeightMm / lineHeightMm));
        
        long totalPages = (long) Math.ceil((double) totalLines / linesPerPage);
        
        boolean doubleSided = settings != null ? settings.isDoubleSidedPrinting() : true;
        double sheetsRequired = doubleSided ? Math.ceil(totalPages / 2.0) : totalPages;
        double doubleSidedPages = doubleSided ? Math.ceil(totalPages / 2.0) : totalPages;
        
        double thickness = settings != null ? settings.getPaperThicknessMm() : 0.1;
        double verticalStackMm = sheetsRequired * thickness;
        double verticalStackMeters = verticalStackMm / 1000.0;
        double verticalStackFeet = verticalStackMeters * 3.28084;

        // Horizontal length: all lines end to end
        double horizontalLengthMm = totalLines * avgLineLength * charWidthMm;
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
        double treePages = settings != null ? settings.getTreePagesPerTree() : 8333.0;
        double treesRequired = sheetsRequired / treePages;
        double shelfWidthMm = sheetsRequired * thickness;
        double shelfWidthMeters = shelfWidthMm / 1000.0;
        double shelfWidthFeet = shelfWidthMeters * 3.28084;
        
        double sheetWeightGrams = settings != null ? settings.getSheetWeightGrams() : 5.0;
        double weightKg = (sheetsRequired * sheetWeightGrams) / 1000.0;
        double weightLbs = weightKg * 2.20462;
        
        double printerTrayPages = settings != null ? settings.getPagesPerPrinterTray() : 500.0;
        double printerTrays = sheetsRequired / printerTrayPages;
        
        double bookshelfWidthM = 0.9;
        double bookshelves = shelfWidthMeters / bookshelfWidthM;
        
        // Extended printing metrics
        double pagesPerBook = settings != null ? settings.getPagesPerBook() : 300.0;
        double booksRequired = Math.ceil(sheetsRequired / (pagesPerBook / (doubleSided ? 2 : 1)));
        double bindersRequired = Math.ceil(sheetsRequired / 500.0);
        
        double pagesPerBox = settings != null ? settings.getPagesPerBox() : 2500.0;
        double boxesNeeded = Math.ceil(sheetsRequired / pagesPerBox);
        
        double inkCoverage = settings != null ? settings.getInkCoveragePercent() : 5.0;
        // A typical ink cartridge is ~15ml for ~800k characters at standard 5% coverage
        double inkEstimationLiters = totalChars * (0.015 / 800000.0) * (inkCoverage / 5.0); 
        
        double printSpeed = settings != null ? settings.getAveragePrintSpeedPpm() : 30.0;
        double timeToPrintMinutes = totalPages / printSpeed; // Print speed is usually in pages/images per minute, not sheets
        
        double printCost = settings != null ? settings.getPrintingCostPerPage() : 0.05;
        // Cost is usually per page image, not per sheet
        double estimatedPrintingCost = totalPages * printCost;

        // Memory & Complexity
        long estimatedUtf8Size = totalChars; // Rough estimate assuming ASCII-heavy
        long estimatedUtf16Size = totalChars * 2;
        long estimatedTokenCount = result.totalWords() > 0 ? result.totalWords() : totalChars / 4;
        long estimatedAstNodes = totalLines * 5;

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
                sheetsRequired, doubleSidedPages, booksRequired, bindersRequired,
                boxesNeeded, inkEstimationLiters, timeToPrintMinutes, estimatedPrintingCost,
                totalPages,
                estimatedUtf8Size, estimatedUtf16Size, estimatedTokenCount, estimatedAstNodes
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
