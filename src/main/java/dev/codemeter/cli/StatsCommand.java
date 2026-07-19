package dev.codemeter.cli;

import dev.codemeter.core.metrics.PhysicalCalculator;
import dev.codemeter.core.model.PhysicalMetrics;
import dev.codemeter.core.model.ScanResult;
import dev.codemeter.core.model.Settings;
import dev.codemeter.core.scanner.ScannerFactory;
import dev.codemeter.core.storage.StorageManager;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.concurrent.Callable;

@Command(name = "stats", description = "Machine-friendly summary. No storytelling.")
public class StatsCommand implements Callable<Integer> {

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
        
        ScanResult result = ScannerFactory.create().scan(targetDirectory.toPath(), p -> {});
        PhysicalMetrics pm = PhysicalCalculator.calculate(result, settings);

        System.out.printf("LOC\t%d\n", result.totalCodeLines());
        System.out.printf("Files\t%d\n", result.totalFiles());
        System.out.printf("Languages\t%d\n", result.languages().size());
        System.out.printf("Characters\t%d\n", result.totalCharacters());
        System.out.printf("Words\t%d\n", result.totalWords());
        System.out.printf("Pages\t%d\n", pm.totalPages());
        System.out.printf("Character Distance\t%.1f km\n", pm.characterLengthKm());

        return CodeMeterExceptionHandler.EXIT_SUCCESS;
    }
}
