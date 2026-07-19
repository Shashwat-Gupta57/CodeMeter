package dev.codemeter.core.metrics;

import dev.codemeter.core.model.LanguageStats;
import dev.codemeter.core.model.ScanResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class ComparisonResultTest {

    private ScanResult createResult(long codeLines, long chars, long files, int langCount) {
        List<LanguageStats> langs = new java.util.ArrayList<>();
        for (int i = 0; i < langCount; i++) {
            langs.add(new LanguageStats("Lang" + i, files / langCount,
                    codeLines / langCount, 0, 0, codeLines / langCount, 0, 0));
        }
        return new ScanResult(
                "/test", "test", System.currentTimeMillis(),
                files, 10, codeLines, 100, 50, codeLines + 150,
                chars, chars / 5, chars,
                "main.java", 100, 50.0, 40.0,
                langs, Map.of()
        );
    }

    @Test
    void compare_showsGrowth() {
        ScanResult prev = createResult(1000, 50000, 10, 2);
        ScanResult curr = createResult(2000, 100000, 15, 3);

        ComparisonResult cmp = ComparisonResult.compare(curr, prev);

        assertThat(cmp.addedLoc()).isEqualTo(1000);
        assertThat(cmp.removedLoc()).isEqualTo(0);
        assertThat(cmp.netLoc()).isEqualTo(1000);
        assertThat(cmp.growthPercent()).isCloseTo(100.0, within(0.1));
        assertThat(cmp.addedFiles()).isEqualTo(5);
    }

    @Test
    void compare_showsReduction() {
        ScanResult prev = createResult(2000, 100000, 20, 3);
        ScanResult curr = createResult(1500, 75000, 18, 2);

        ComparisonResult cmp = ComparisonResult.compare(curr, prev);

        assertThat(cmp.netLoc()).isEqualTo(-500);
        assertThat(cmp.removedLoc()).isEqualTo(500);
        assertThat(cmp.addedLoc()).isEqualTo(0);
        assertThat(cmp.growthPercent()).isLessThan(0);
    }

    @Test
    void compare_noChange() {
        ScanResult same = createResult(1000, 50000, 10, 2);

        ComparisonResult cmp = ComparisonResult.compare(same, same);

        assertThat(cmp.netLoc()).isEqualTo(0);
        assertThat(cmp.growthPercent()).isCloseTo(0.0, within(0.01));
    }

    @Test
    void growthDescription_positive() {
        ScanResult prev = createResult(1000, 50000, 10, 2);
        ScanResult curr = createResult(1500, 75000, 15, 3);
        ComparisonResult cmp = ComparisonResult.compare(curr, prev);

        assertThat(cmp.growthDescription()).contains("+");
        assertThat(cmp.growthDescription()).contains("growth");
    }

    @Test
    void growthDescription_negative() {
        ScanResult prev = createResult(2000, 100000, 20, 3);
        ScanResult curr = createResult(1500, 75000, 18, 2);
        ComparisonResult cmp = ComparisonResult.compare(curr, prev);

        assertThat(cmp.growthDescription()).contains("reduction");
    }
}
