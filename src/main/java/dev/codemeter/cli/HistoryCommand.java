package dev.codemeter.cli;

import dev.codemeter.core.metrics.PhysicalCalculator;
import dev.codemeter.core.model.HistoryEntry;
import dev.codemeter.core.model.PhysicalMetrics;
import dev.codemeter.core.model.ScanResult;
import dev.codemeter.core.model.Settings;
import dev.codemeter.core.storage.StorageManager;
import picocli.CommandLine.Command;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "history", description = "Display previous scans stored locally.")
public class HistoryCommand implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
        StorageManager storage = new StorageManager();
        storage.load();

        List<HistoryEntry> history = storage.getGlobalHistory();

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        System.out.println("History\n");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        if (history.isEmpty()) {
            System.out.println("No history available.");
            System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return CodeMeterExceptionHandler.EXIT_SUCCESS;
        }

        Settings settings = storage.getSettings();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm").withZone(ZoneId.systemDefault());

        for (HistoryEntry entry : history) {
            System.out.println(formatter.format(entry.timestamp()));
            System.out.println(entry.projectName());

            ScanResult dummyResult = new ScanResult(
                    entry.projectPath(), entry.projectName(), entry.timestamp().toEpochMilli(),
                    entry.totalFiles(), 0, entry.totalCodeLines(), entry.totalCommentLines(), entry.totalBlankLines(),
                    entry.totalLoc(), entry.totalCharacters(), 0, entry.totalBytes(),
                    "", 0, 0, 0, List.of(), java.util.Map.of()
            );

            PhysicalMetrics pm = PhysicalCalculator.calculate(dummyResult, settings);

            System.out.printf("%,d LOC\n", entry.totalLoc());
            System.out.printf("%.1f km\n", pm.characterLengthKm());
            System.out.printf("%,d pages\n", pm.totalPages());

            System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        }

        return CodeMeterExceptionHandler.EXIT_SUCCESS;
    }
}
