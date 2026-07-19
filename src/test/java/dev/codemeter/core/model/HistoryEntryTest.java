package dev.codemeter.core.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class HistoryEntryTest {

    @Test
    void fromScanResult_copiesAllFields() {
        ScanResult result = new ScanResult(
                "/test/project", "my-project", 1700000000000L,
                150, 20, 10000, 2000, 1000, 13000,
                500000, 100000, 500000,
                "main.java", 500, 86.7, 40.0,
                List.of(
                        new LanguageStats("Java", 100, 8000, 1500, 800, 10300, 400000, 0),
                        new LanguageStats("XML", 50, 2000, 500, 200, 2700, 100000, 0)
                ),
                Map.of(".java", 100L, ".xml", 50L)
        );

        HistoryEntry entry = HistoryEntry.from(result);

        assertThat(entry.projectPath()).isEqualTo("/test/project");
        assertThat(entry.projectName()).isEqualTo("my-project");
        assertThat(entry.totalFiles()).isEqualTo(150);
        assertThat(entry.totalCodeLines()).isEqualTo(10000);
        assertThat(entry.totalCommentLines()).isEqualTo(2000);
        assertThat(entry.totalBlankLines()).isEqualTo(1000);
        assertThat(entry.totalCharacters()).isEqualTo(500000);
        assertThat(entry.totalBytes()).isEqualTo(500000);
        assertThat(entry.languageCount()).isEqualTo(2);
        assertThat(entry.dominantLanguage()).isEqualTo("Java");
    }

    @Test
    void totalLoc_calculatesCorrectly() {
        HistoryEntry entry = new HistoryEntry(
                "/test", "test", Instant.now(),
                50, 5000, 1000, 500, 250000, 250000, 3, "Java"
        );

        assertThat(entry.totalLoc()).isEqualTo(6500);
    }
}
