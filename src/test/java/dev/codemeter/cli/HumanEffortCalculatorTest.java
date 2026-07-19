package dev.codemeter.cli;

import dev.codemeter.core.model.LanguageStats;
import dev.codemeter.core.model.ScanResult;
import dev.codemeter.core.model.Settings;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class HumanEffortCalculatorTest {

    private ScanResult createResult(long chars) {
        return new ScanResult(
                "p", "p", 0, 1, 1, 1, 1, 1, 1, chars, chars/5, chars, "f", 1, 1, 1,
                List.of(new LanguageStats("Java", 1,1,1,1,1,chars,0)),
                Map.of(".java", 1L)
        );
    }

    @Test
    void formatTimeExact_reading() {
        Settings settings = new Settings();
        settings.setReadingSpeedWpm(250.0);
        settings.setAverageWordLength(5.0);

        // 10,000 chars = 2000 words. 2000 / 250 = 8 minutes = 0 hours
        ScanResult r = createResult(10_000);
        String formatted = HumanEffortCalculator.formatTimeExact(r, false, settings);
        assertThat(formatted).contains("minutes");
    }

    @Test
    void formatTimeExact_typing() {
        Settings settings = new Settings();
        settings.setTypingSpeedWpm(60.0);
        settings.setAverageWordLength(5.0);

        // 1,000,000 chars = 200,000 words. 200,000 / 60 = 3333 minutes = 55.5 hours
        ScanResult r = createResult(1_000_000);
        String formatted = HumanEffortCalculator.formatTimeExact(r, true, settings);
        assertThat(formatted).contains("hours");
    }
}
