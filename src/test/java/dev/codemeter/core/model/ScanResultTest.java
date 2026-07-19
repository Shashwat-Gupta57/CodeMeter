package dev.codemeter.core.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class ScanResultTest {

    @Test
    void totalLoc_sumOfCodeCommentsAndBlanks() {
        ScanResult result = new ScanResult(
                "/test", "test", System.currentTimeMillis(),
                10, 1, 1000, 200, 100, 1300,
                50000, 10000, 50000,
                "main.java", 500, 130.0, 40.0,
                List.of(), Map.of()
        );

        assertThat(result.totalLoc()).isEqualTo(1300);
    }

    @Test
    void dominantLanguage_returnsMostCodeLines() {
        ScanResult result = new ScanResult(
                "/test", "test", System.currentTimeMillis(),
                100, 10, 5000, 500, 200, 5700,
                250000, 50000, 250000,
                "main.java", 500, 57.0, 40.0,
                List.of(
                        new LanguageStats("Java", 40, 3000, 200, 100, 3300, 150000, 0),
                        new LanguageStats("Python", 30, 1500, 200, 80, 1780, 75000, 0),
                        new LanguageStats("XML", 30, 500, 100, 20, 620, 25000, 0)
                ),
                Map.of()
        );

        assertThat(result.dominantLanguage()).isEqualTo("Java");
    }

    @Test
    void languageCount_returnsCorrectCount() {
        ScanResult result = new ScanResult(
                "/test", "test", System.currentTimeMillis(),
                100, 10, 5000, 500, 200, 5700,
                250000, 50000, 250000,
                "main.java", 500, 57.0, 40.0,
                List.of(
                        new LanguageStats("Java", 40, 3000, 200, 100, 3300, 150000, 0),
                        new LanguageStats("Python", 30, 1500, 200, 80, 1780, 75000, 0),
                        new LanguageStats("XML", 30, 500, 100, 20, 620, 25000, 0)
                ),
                Map.of()
        );

        assertThat(result.languageCount()).isEqualTo(3);
    }

    @Test
    void dominantLanguage_emptyLanguages_returnsUnknown() {
        ScanResult result = new ScanResult(
                "/test", "test", System.currentTimeMillis(),
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                "", 0, 0, 0,
                List.of(), Map.of()
        );

        assertThat(result.dominantLanguage()).isEqualTo("Unknown");
    }
}
