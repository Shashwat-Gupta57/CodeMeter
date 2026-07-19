package dev.codemeter.core.metrics;

import dev.codemeter.core.model.PrintedMetrics;
import dev.codemeter.core.model.ScanResult;
import dev.codemeter.core.model.Settings;

/**
 * Calculates print-related metrics based on configurable paper/font settings.
 * All print calculations update LIVE when settings change.
 */
public final class PrintCalculator {

    // Ink usage estimates (ml per page)
    private static final double LASER_INK_ML_PER_PAGE = 0.02;
    private static final double INKJET_INK_ML_PER_PAGE = 0.05;
    private static final double DRAFT_INK_ML_PER_PAGE = 0.01;

    // Ink cartridge capacity in ml
    private static final double CARTRIDGE_ML = 10.0;

    // Cost estimates
    private static final double PAPER_COST_PER_PAGE_USD = 0.01;
    private static final double INK_COST_PER_ML_USD = 2.50;

    // Print speed (pages per minute for standard printer)
    private static final double PAGES_PER_MINUTE = 30.0;

    // Reading speed (words per minute)
    private static final double READING_WPM = 200.0;

    // Typing speed (characters per minute for average developer)
    private static final double TYPING_CPM = 400.0;

    private PrintCalculator() {}

    /**
     * Calculate print metrics based on scan result and current settings.
     */
    public static PrintedMetrics calculate(ScanResult result, Settings settings) {
        // Calculate printable area
        double printableWidthMm = settings.getPrintableWidthMm();
        double printableHeightMm = settings.getPrintableHeightMm();

        double charWidthMm = settings.getCharacterWidthMm();
        double lineHeightMm = settings.getFontSizePt() * 0.3528 * settings.getLineSpacing();

        // Characters per line and lines per page
        int charsPerLine = (int) Math.floor(printableWidthMm / charWidthMm);
        int linesPerPage = (int) Math.floor(printableHeightMm / lineHeightMm);

        if (charsPerLine <= 0) charsPerLine = 80;
        if (linesPerPage <= 0) linesPerPage = 55;

        // Total pages needed
        long totalLines = result.totalLines();
        long totalPages = (long) Math.ceil((double) totalLines / linesPerPage);

        // Physical measurements
        double thickness = settings.getPaperThicknessMm();
        double stackHeightCm = (totalPages * thickness) / 10.0;
        double stackHeightInches = stackHeightCm / 2.54;

        // Weight
        double sheetWeight = settings.getSheetWeightGrams();
        double totalWeightKg = (totalPages * sheetWeight) / 1000.0;
        double totalWeightLbs = totalWeightKg * 2.20462;

        double inkMlPerPage = LASER_INK_ML_PER_PAGE;
        double inkMl = totalPages * inkMlPerPage;
        double inkCartridges = inkMl / CARTRIDGE_ML;

        // Cost estimates
        double paperCost = totalPages * PAPER_COST_PER_PAGE_USD;
        double inkCost = inkMl * INK_COST_PER_ML_USD;
        double totalCost = paperCost + inkCost;

        // Time estimates
        double printTimeMinutes = totalPages / PAGES_PER_MINUTE;
        long totalWords = result.totalWords() > 0 ? result.totalWords() : result.totalCharacters() / 5;
        double readingTimeHours = (totalWords / READING_WPM) / 60.0;
        long totalChars = result.totalCharacters() > 0 ? result.totalCharacters() : result.totalBytes();
        double typingTimeHours = (totalChars / TYPING_CPM) / 60.0;

        return new PrintedMetrics(
                settings.getPaperSize().name(),
                settings.getFontName(),
                settings.getFontSizePt(),
                settings.getLineSpacing(),
                "NORMAL",
                settings.getPaperThicknessMm(),
                "LASER",
                totalPages,
                linesPerPage,
                charsPerLine,
                stackHeightCm,
                stackHeightInches,
                totalWeightKg,
                totalWeightLbs,
                inkMl,
                inkCartridges,
                paperCost,
                inkCost,
                totalCost,
                printTimeMinutes,
                readingTimeHours,
                typingTimeHours
        );
    }
}
