package dev.codemeter.cli;

import dev.codemeter.core.model.LanguageStats;
import dev.codemeter.core.model.ScanResult;
import dev.codemeter.core.model.Settings;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;

class ScanPresenterTest {

    @Test
    void printResults_doesNotThrowExceptions() {
        ScanResult result = new ScanResult(
                "p", "p", 0, 1, 1, 1, 1, 1, 1, 1000, 200, 1000, "f", 1, 1, 1,
                List.of(new LanguageStats("Java", 1,1,1,1,1,1000,0)),
                Map.of(".java", 1L)
        );
        Settings settings = new Settings();

        assertThatCode(() -> {
            ScanPresenter.printResults(result, settings, 100, List.of(), ScanCommand.Theme.story);
        }).doesNotThrowAnyException();
    }
}
