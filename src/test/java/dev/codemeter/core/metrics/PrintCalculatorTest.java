package dev.codemeter.core.metrics;

import dev.codemeter.core.model.LanguageStats;
import dev.codemeter.core.model.PrintedMetrics;
import dev.codemeter.core.model.ScanResult;
import dev.codemeter.core.model.Settings;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class PrintCalculatorTest {

    private ScanResult createSampleResult(long totalLines, long totalChars) {
        return new ScanResult(
                "/test/project", "test-project", System.currentTimeMillis(),
                100, 10,
                totalLines - 200, 100, 100, totalLines,
                totalChars, totalChars / 5, totalChars,
                "main.java", 1000, 50.0, 40.0,
                List.of(new LanguageStats("Java", 100, totalLines - 200, 100, 100, totalLines, totalChars, 0)),
                Map.of(".java", 100L)
        );
    }

    @Test
    void calculate_withDefaultSettings_returnsValidMetrics() {
        ScanResult result = createSampleResult(10_000, 500_000);
        Settings settings = new Settings();

        PrintedMetrics pm = PrintCalculator.calculate(result, settings);

        assertThat(pm.totalPages()).isGreaterThan(0);
        assertThat(pm.linesPerPage()).isGreaterThan(0);
        assertThat(pm.charsPerLine()).isGreaterThan(0);
        assertThat(pm.stackHeightCm()).isGreaterThan(0);
        assertThat(pm.totalWeightKg()).isGreaterThan(0);
        assertThat(pm.inkMl()).isGreaterThan(0);
        assertThat(pm.totalCostUsd()).isGreaterThan(0);
        assertThat(pm.readingTimeHours()).isGreaterThan(0);
        assertThat(pm.typingTimeHours()).isGreaterThan(0);
    }

    @Test
    void calculate_changingPaperSize_affectsPages() {
        ScanResult result = createSampleResult(10_000, 500_000);

        Settings a4Settings = new Settings();
        a4Settings.setPaperSize(Settings.PaperSize.A4);
        PrintedMetrics a4 = PrintCalculator.calculate(result, a4Settings);

        Settings letterSettings = new Settings();
        letterSettings.setPaperSize(Settings.PaperSize.LETTER);
        PrintedMetrics letter = PrintCalculator.calculate(result, letterSettings);

        // A4 is taller than Letter, so should fit more lines per page
        assertThat(a4.linesPerPage()).isNotEqualTo(letter.linesPerPage());
    }

    @Test
    void calculate_changingFontSize_affectsLayout() {
        ScanResult result = createSampleResult(10_000, 500_000);

        Settings smallFont = new Settings();
        smallFont.setFontSize(8);
        PrintedMetrics small = PrintCalculator.calculate(result, smallFont);

        Settings largeFont = new Settings();
        largeFont.setFontSize(14);
        PrintedMetrics large = PrintCalculator.calculate(result, largeFont);

        // Smaller font = more lines per page = fewer total pages
        assertThat(small.linesPerPage()).isGreaterThan(large.linesPerPage());
        assertThat(small.totalPages()).isLessThan(large.totalPages());
    }

    @Test
    void calculate_inkTypes_differentUsage() {
        ScanResult result = createSampleResult(10_000, 500_000);

        Settings laserSettings = new Settings();
        laserSettings.setInkType(Settings.InkType.LASER);
        PrintedMetrics laser = PrintCalculator.calculate(result, laserSettings);

        Settings inkjetSettings = new Settings();
        inkjetSettings.setInkType(Settings.InkType.INKJET);
        PrintedMetrics inkjet = PrintCalculator.calculate(result, inkjetSettings);

        // Inkjet uses more ink per page
        assertThat(inkjet.inkMl()).isGreaterThan(laser.inkMl());
    }

    @Test
    void calculate_withZeroLines_returnsZeroPages() {
        ScanResult result = createSampleResult(0, 0);
        Settings settings = new Settings();

        PrintedMetrics pm = PrintCalculator.calculate(result, settings);

        assertThat(pm.totalPages()).isEqualTo(0);
    }
}
