package dev.codemeter.cli;

import dev.codemeter.core.metrics.PhysicalCalculator;
import dev.codemeter.core.model.HistoryEntry;
import dev.codemeter.core.model.PhysicalMetrics;
import dev.codemeter.core.model.ScanResult;
import dev.codemeter.core.model.Settings;
import dev.codemeter.core.scanner.ScannerFactory;
import dev.codemeter.core.storage.StorageManager;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "compare", description = "Compare two Stories.")
public class CompareCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "The directory to scan.", defaultValue = ".")
    private File targetDirectory;

    @Override
    public Integer call() throws Exception {
        if (!targetDirectory.exists() || !targetDirectory.isDirectory()) {
            throw new IllegalArgumentException("The directory\n\n" + targetDirectory.getAbsolutePath() + "\n\ndoes not exist.");
        }

        StorageManager storage = new StorageManager();
        storage.load();
        Settings settings = storage.getSettings();

        // Scan current directory
        ScanResult currentResult = ScannerFactory.create().scan(targetDirectory.toPath(), p -> {});
        PhysicalMetrics currentMetrics = PhysicalCalculator.calculate(currentResult, settings);

        // Find previous scan
        List<HistoryEntry> history = storage.getProjectHistory(currentResult.projectPath());
        if (history.isEmpty()) {
            System.out.println("No previous history found to compare against.");
            return CodeMeterExceptionHandler.EXIT_SUCCESS;
        }
        
        HistoryEntry previous = history.get(0); // Most recent
        ScanResult previousDummy = new ScanResult(
                previous.projectPath(), previous.projectName(), previous.timestamp().toEpochMilli(),
                previous.totalFiles(), 0, previous.totalCodeLines(), previous.totalCommentLines(), previous.totalBlankLines(),
                previous.totalLoc(), previous.totalCharacters(), 0, previous.totalBytes(),
                "", 0, 0, 0, List.of(), java.util.Map.of()
        );
        PhysicalMetrics previousMetrics = PhysicalCalculator.calculate(previousDummy, settings);

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        System.out.println("Project Growth\n");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        printDelta("Lines", currentResult.totalLines(), previous.totalLoc());
        printDelta("Files", currentResult.totalFiles(), previous.totalFiles());
        printDelta("Characters", currentResult.totalCharacters(), previous.totalCharacters());
        printDelta("Pages", currentMetrics.totalPages(), previousMetrics.totalPages());
        
        long weightDeltaGrams = Math.round((currentMetrics.estimatedWeightKg() - previousMetrics.estimatedWeightKg()) * 1000);
        printStringDelta("Paper Weight", weightDeltaGrams, "g");
        
        long distDeltaMeters = Math.round((currentMetrics.characterLengthKm() - previousMetrics.characterLengthKm()) * 1000);
        printStringDelta("Character Distance", distDeltaMeters, "metres");

        String currLang = currentResult.dominantLanguage();
        String prevLang = previous.dominantLanguage();
        if (currLang.equals(prevLang)) {
            System.out.printf("%-20s %s unchanged\n", "Largest Language", currLang);
        } else {
            System.out.printf("%-20s %s -> %s\n", "Largest Language", prevLang, currLang);
        }

        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        // We don't have the actual new achievements logic easily accessible between two scans without full state mapping,
        // so we'll omit the achievements section for now to keep it robust and not throw exceptions.

        return CodeMeterExceptionHandler.EXIT_SUCCESS;
    }

    private void printDelta(String label, long current, long previous) {
        long delta = current - previous;
        if (delta == 0) return; // Skip unchanged
        
        double percent = previous > 0 ? (double) delta / previous * 100.0 : 100.0;
        String sign = delta > 0 ? "+" : "";
        System.out.printf("%-20s %s%,d (%.1f%%)\n", label, sign, delta, percent);
    }
    
    private void printStringDelta(String label, long delta, String unit) {
        if (delta == 0) return;
        String sign = delta > 0 ? "+" : "";
        System.out.printf("%-20s %s%,d %s\n", label, sign, delta, unit);
    }
}
