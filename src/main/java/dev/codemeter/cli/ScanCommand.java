package dev.codemeter.cli;

import dev.codemeter.core.metrics.PhysicalCalculator;
import dev.codemeter.core.model.*;
import dev.codemeter.core.scanner.CodeScanner;
import dev.codemeter.core.scanner.ScanException;
import dev.codemeter.core.scanner.ScannerFactory;
import dev.codemeter.core.storage.StorageManager;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.util.List;

/**
 * Headless scan command for CI/automation usage.
 */
@Command(
        name = "scan",
        description = "Scan a directory and display results (headless mode)"
)
public class ScanCommand implements Runnable {

    @Parameters(index = "0", description = "Path to scan", defaultValue = ".")
    private Path path;

    @Option(names = {"-t", "--theme"}, description = "Presentation theme: ${COMPLETION-CANDIDATES} (default: ${DEFAULT-VALUE})", defaultValue = "story")
    private Theme theme;

    public enum Theme {
        story, compact, minimal, ci, wrapped
    }

    @Override
    public void run() {
        try {
            ScanPresenter.printHeader(path.toAbsolutePath().toString());

            long start = System.currentTimeMillis();
            CodeScanner scanner = ScannerFactory.create();

            ScanResult result = scanner.scan(path, progress -> {
                ScanPresenter.printProgress(progress);
            });
            ScanPresenter.printProgressComplete();
            long duration = System.currentTimeMillis() - start;

            // Save to storage
            StorageManager storage = new StorageManager();
            storage.load();
            storage.addOrUpdateProject(Project.from(result));
            storage.addHistoryEntry(HistoryEntry.from(result));
            storage.addRecentPath(result.projectPath());
            List<Achievement> unlocked = storage.checkAndUpdateAchievements(result);
            storage.save();

            // Display results
            ScanPresenter.printResults(result, storage.getSettings(), duration, unlocked, theme);
            
            // For story/wrapped themes, also print achievements inline if any were unlocked
            if (theme == Theme.story || theme == Theme.wrapped) {
                ScanPresenter.printAchievements(unlocked);
            }
            
            ScanPresenter.printFooter(duration);

        } catch (ScanException e) {
            System.err.println("❌ Scan failed: " + e.getMessage());
            System.exit(1);
        }
    }
}
