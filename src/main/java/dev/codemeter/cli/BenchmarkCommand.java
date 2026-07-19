package dev.codemeter.cli;

import dev.codemeter.core.metrics.PhysicalCalculator;
import dev.codemeter.core.model.ScanResult;
import dev.codemeter.core.model.Settings;
import dev.codemeter.core.scanner.CodeScanner;
import dev.codemeter.core.scanner.ScannerFactory;
import dev.codemeter.core.storage.StorageManager;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.concurrent.Callable;

@Command(name = "benchmark", description = "Measure performance of the scanning engine.")
public class BenchmarkCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "The directory to scan.", defaultValue = ".")
    private File targetDirectory;

    @Override
    public Integer call() throws Exception {
        if (!targetDirectory.exists() || !targetDirectory.isDirectory()) {
            throw new IllegalArgumentException("The directory\n\n" + targetDirectory.getAbsolutePath() + "\n\ndoes not exist.");
        }

        long tTotalStart = System.currentTimeMillis();

        StorageManager storage = new StorageManager();
        storage.load();
        Settings settings = storage.getSettings();

        CodeScanner scanner = ScannerFactory.create();
        String backendName = ScannerFactory.availableScannerName();

        long tScanStart = System.currentTimeMillis();
        ScanResult result = scanner.scan(targetDirectory.toPath(), p -> {});
        long tScanEnd = System.currentTimeMillis();

        long tMetricsStart = System.currentTimeMillis();
        PhysicalCalculator.calculate(result, settings);
        long tMetricsEnd = System.currentTimeMillis();
        
        long tTotalEnd = System.currentTimeMillis();

        double scanSecs = (tScanEnd - tScanStart) / 1000.0;
        double totalSecs = (tTotalEnd - tTotalStart) / 1000.0;

        double filesPerSec = scanSecs > 0 ? result.totalFiles() / scanSecs : 0;
        double locPerSec = scanSecs > 0 ? result.totalCodeLines() / scanSecs : 0;
        
        long memoryUsed = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        System.out.println("Benchmark Results\n");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        System.out.printf("Backend               %s\n", backendName.toUpperCase());
        System.out.printf("Scan duration         %.3f seconds\n", scanSecs);
        System.out.printf("Files per second      %,.0f\n", filesPerSec);
        System.out.printf("LOC per second        %,.0f\n", locPerSec);
        System.out.printf("Memory usage          %.2f MB\n", memoryUsed / (1024.0 * 1024.0));
        System.out.printf("Story generation time %d ms\n", (tMetricsEnd - tMetricsStart));
        System.out.printf("Total runtime         %.3f seconds\n", totalSecs);

        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        return CodeMeterExceptionHandler.EXIT_SUCCESS;
    }
}
