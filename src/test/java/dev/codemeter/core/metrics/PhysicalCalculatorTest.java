package dev.codemeter.core.metrics;

import dev.codemeter.core.model.LanguageStats;
import dev.codemeter.core.model.PhysicalMetrics;
import dev.codemeter.core.model.ScanResult;
import dev.codemeter.core.model.Settings;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class PhysicalCalculatorTest {

    private ScanResult createSampleResult(long codeLines, long totalChars, long totalLines) {
        return new ScanResult(
                "/test/project", "test-project", System.currentTimeMillis(),
                100, 10,
                codeLines, 500, 200, totalLines,
                totalChars, totalChars / 5, totalChars,
                "main.java", 1000, 50.0, 40.0,
                List.of(new LanguageStats("Java", 50, codeLines, 300, 100, totalLines, totalChars, 0)),
                Map.of(".java", 50L)
        );
    }

    @Test
    void calculate_withTypicalProject_returnsPositiveMetrics() {
        ScanResult result = createSampleResult(10_000, 500_000, 10_700);
        Settings settings = new Settings();

        PhysicalMetrics pm = PhysicalCalculator.calculate(result, settings);

        assertThat(pm.characterLengthKm()).isGreaterThan(0);
        assertThat(pm.verticalStackMeters()).isGreaterThan(0);
        assertThat(pm.footballFields()).isGreaterThan(0);
        assertThat(pm.totalPages()).isGreaterThan(0);
        assertThat(pm.treesRequired()).isGreaterThanOrEqualTo(0);
        assertThat(pm.estimatedWeightKg()).isGreaterThan(0);
    }

    @Test
    void calculate_characterLength_correctFormula() {
        // 1,000,000 chars * 2.5mm = 2500m = 2.5km
        ScanResult result = createSampleResult(10_000, 1_000_000, 10_700);
        Settings settings = new Settings();
        settings.setCharacterWidthMm(2.5);

        PhysicalMetrics pm = PhysicalCalculator.calculate(result, settings);

        assertThat(pm.characterLengthKm()).isCloseTo(2.5, within(0.01));
    }

    @Test
    void calculate_pages_basedOnLineCount() {
        // 5500 lines / 55 lines per page = 100 pages
        ScanResult result = createSampleResult(5000, 250_000, 5500);
        Settings settings = new Settings();

        PhysicalMetrics pm = PhysicalCalculator.calculate(result, settings);

        assertThat(pm.totalPages()).isEqualTo(100);
    }

    @Test
    void calculate_withZeroInput_returnsZeroMetrics() {
        ScanResult result = createSampleResult(0, 0, 0);
        Settings settings = new Settings();

        PhysicalMetrics pm = PhysicalCalculator.calculate(result, settings);

        assertThat(pm.characterLengthKm()).isEqualTo(0);
        assertThat(pm.totalPages()).isEqualTo(0);
        assertThat(pm.footballFields()).isEqualTo(0);
    }

    @Test
    void formatNumber_withVariousValues() {
        assertThat(PhysicalCalculator.formatNumber(0)).isEqualTo("0");
        assertThat(PhysicalCalculator.formatNumber(1000)).isEqualTo("1,000");
        assertThat(PhysicalCalculator.formatNumber(1_234_567)).isEqualTo("1,234,567");
    }

    @Test
    void formatMetric_withVariousScales() {
        assertThat(PhysicalCalculator.formatMetric(0, "km")).isEqualTo("0 km");
        assertThat(PhysicalCalculator.formatMetric(1.5, "km")).isEqualTo("1.5 km");
        assertThat(PhysicalCalculator.formatMetric(1500, "m")).contains("1.5K");
        assertThat(PhysicalCalculator.formatMetric(1_500_000, "m")).contains("1.50M");
    }
}
