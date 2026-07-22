package dev.codemeter.cli;

import dev.codemeter.core.model.ScanResult;
import dev.codemeter.core.scanner.CodeScanner;
import dev.codemeter.core.scanner.ScanException;
import dev.codemeter.core.scanner.ScannerFactory;
import dev.codemeter.export.ExportService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;

/**
 * Export command for generating reports in various formats.
 */
@Command(
        name = "export",
        description = "Export scan results to various formats (JSON, CSV, Markdown, PDF)"
)
public class ExportCommand implements java.util.concurrent.Callable<Integer> {

    @Parameters(index = "0", description = "Path to scan", defaultValue = ".")
    private Path scanPath;

    @Option(names = {"-f", "--format"}, description = "Export format: json, csv, markdown, pdf, svg, png",
            defaultValue = "json")
    private String format;

    @Option(names = {"-o", "--output"}, description = "Output file path")
    private Path outputPath;

    @Override
    public Integer call() {
        try {
            System.out.println("⚡ Scanning " + scanPath.toAbsolutePath() + "...");
            CodeScanner scanner = ScannerFactory.create();
            ScanResult result = scanner.scan(scanPath, null);

            Path output = outputPath;
            if (output == null) {
                output = Path.of("codemeter-report." + format.toLowerCase());
            }

            ExportService exportService = new ExportService();
            exportService.export(result, format, output);

            System.out.println("✓ Exported to " + output.toAbsolutePath());
            return 0;

        } catch (ScanException e) {
            System.err.println("❌ Scan failed: " + e.getMessage());
            return 1;
        } catch (Exception e) {
            System.err.println("❌ Export failed: " + e.getMessage());
            return 1;
        }
    }
}
