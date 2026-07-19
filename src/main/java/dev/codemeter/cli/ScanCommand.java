package dev.codemeter.cli;

import dev.codemeter.core.metrics.PhysicalCalculator;
import dev.codemeter.core.model.*;
import dev.codemeter.core.scanner.CodeScanner;
import dev.codemeter.core.scanner.ScanException;
import dev.codemeter.core.scanner.ScannerFactory;
import dev.codemeter.core.storage.StorageManager;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;

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

    @Override
    public void run() {
        try {
            System.out.println("⚡ CodeMeter — Scanning " + path.toAbsolutePath() + "...");
            System.out.println();

            CodeScanner scanner = ScannerFactory.create();
            System.out.println("  Using scanner: " + scanner.name());

            ScanResult result = scanner.scan(path, progress -> {
                System.out.print("\r  Progress: " + progress + "%");
            });
            System.out.println("\r  Progress: 100% ✓");
            System.out.println();

            // Save to storage
            StorageManager storage = new StorageManager();
            storage.load();
            storage.addOrUpdateProject(Project.from(result));
            storage.addHistoryEntry(HistoryEntry.from(result));
            storage.addRecentPath(result.projectPath());
            storage.checkAndUpdateAchievements(result);
            storage.save();

            // Display results
            printResults(result, storage.getSettings());

        } catch (ScanException e) {
            System.err.println("❌ Scan failed: " + e.getMessage());
            System.exit(1);
        }
    }

    private void printResults(ScanResult result, Settings settings) {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║                    CODE OVERVIEW                     ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        System.out.printf("║  Project:     %-38s ║%n", result.projectName());
        System.out.printf("║  Files:       %-38s ║%n", PhysicalCalculator.formatNumber(result.totalFiles()));
        System.out.printf("║  Languages:   %-38s ║%n", result.languageCount());
        System.out.printf("║  Code:        %-38s ║%n", PhysicalCalculator.formatNumber(result.totalCodeLines()));
        System.out.printf("║  Comments:    %-38s ║%n", PhysicalCalculator.formatNumber(result.totalCommentLines()));
        System.out.printf("║  Blank:       %-38s ║%n", PhysicalCalculator.formatNumber(result.totalBlankLines()));
        System.out.printf("║  Characters:  %-38s ║%n", PhysicalCalculator.formatNumber(result.totalCharacters()));
        System.out.printf("║  Bytes:       %-38s ║%n", PhysicalCalculator.formatNumber(result.totalBytes()));
        System.out.println("╠══════════════════════════════════════════════════════╣");
        System.out.println("║                  PHYSICAL METRICS                    ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");

        PhysicalMetrics pm = PhysicalCalculator.calculate(result, settings);
        System.out.printf("║  Character Length:  %-32s ║%n", PhysicalCalculator.formatMetric(pm.characterLengthKm(), "km"));
        System.out.printf("║  Stack Height:      %-32s ║%n", PhysicalCalculator.formatMetric(pm.verticalStackMeters(), "m"));
        System.out.printf("║  Football Fields:   %-32s ║%n", PhysicalCalculator.formatMetric(pm.footballFields(), ""));
        System.out.printf("║  Burj Khalifas:     %-32s ║%n", PhysicalCalculator.formatMetric(pm.burjKhalifas(), ""));
        System.out.printf("║  Pages:             %-32s ║%n", PhysicalCalculator.formatNumber(pm.totalPages()));
        System.out.printf("║  Trees Required:    %-32s ║%n", PhysicalCalculator.formatMetric(pm.treesRequired(), ""));
        System.out.printf("║  Weight:            %-32s ║%n", PhysicalCalculator.formatMetric(pm.estimatedWeightKg(), "kg"));
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println();

        // Language breakdown
        System.out.println("  Languages:");
        for (LanguageStats lang : result.languages().stream()
                .sorted((a, b) -> Long.compare(b.codeLines(), a.codeLines()))
                .limit(10)
                .toList()) {
            double pct = lang.percentageOf(result.totalCodeLines());
            String bar = "█".repeat((int) (pct / 5));
            System.out.printf("    %-20s %6s lines  %5.1f%%  %s%n",
                    lang.language(),
                    PhysicalCalculator.formatNumber(lang.codeLines()),
                    pct, bar);
        }
        System.out.println();
    }
}
