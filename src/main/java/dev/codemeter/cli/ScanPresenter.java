package dev.codemeter.cli;

import dev.codemeter.core.model.LanguageStats;
import dev.codemeter.core.model.PhysicalMetrics;
import dev.codemeter.core.model.ScanResult;
import dev.codemeter.core.model.Settings;
import dev.codemeter.core.model.Achievement;
import dev.codemeter.core.metrics.PhysicalCalculator;
import dev.codemeter.cli.ScanCommand.Theme;
import picocli.CommandLine.Help.Ansi;

import java.util.List;

public class ScanPresenter {

    private static final String FULL_BLOCK = "==============================================";

    public static void printHeader(String path) {
        System.out.println(Ansi.AUTO.string("@|bold,cyan " + FULL_BLOCK + "|@"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("                @|bold,cyan CodeMeter|@"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("        @|faint Measure your code. Physically.|@"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("@|bold,cyan " + FULL_BLOCK + "|@"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("@|faint Scanning|@"));
        System.out.println(Ansi.AUTO.string("@|bold " + path + "|@"));
    }

    public static void printProgress(int percent) {
        int width = 33;
        int filled = (percent * width) / 100;
        String bar = "#".repeat(filled) + "-".repeat(width - filled);
        System.out.print(Ansi.AUTO.string("\r@|cyan " + bar + "|@ @|bold " + percent + "%|@"));
    }

    public static void printProgressComplete() {
        int width = 33;
        String bar = "#".repeat(width);
        System.out.print(Ansi.AUTO.string("\r@|cyan " + bar + "|@ @|bold 100%|@\n"));
    }

    public static void printResults(ScanResult result, Settings settings, long durationMs, List<Achievement> unlockedAchievements, Theme theme) {
        if (theme == Theme.ci) {
            printCI(result, durationMs);
            return;
        } else if (theme == Theme.minimal) {
            printMinimal(result, settings, durationMs);
            return;
        } else if (theme == Theme.compact) {
            printCompact(result, settings, durationMs);
            return;
        }

        // Default: Story (or Wrapped fallback)
        printStory(result, settings, durationMs, unlockedAchievements);
    }

    private static void printStory(ScanResult result, Settings settings, long durationMs, List<Achievement> unlockedAchievements) {
        PhysicalMetrics pm = PhysicalCalculator.calculate(result, settings);
        
        System.out.println();
        System.out.println(Ansi.AUTO.string("@|faint Completed in " + (durationMs / 1000.0) + " seconds.|@"));
        System.out.println();
        
        // SECTION 2: THE HEADLINE
        System.out.println(Ansi.AUTO.string("@|bold,cyan " + FULL_BLOCK + "|@"));
        System.out.println();
        
        if (pm.characterLengthKm() >= 1.0) {
            System.out.println(Ansi.AUTO.string("Your code stretches"));
            System.out.println(Ansi.AUTO.string("@|bold,yellow " + String.format("%.1f kilometres|@", pm.characterLengthKm())));
        } else if (pm.totalPages() > 1000) {
            System.out.println(Ansi.AUTO.string("Your project fills"));
            System.out.println(Ansi.AUTO.string("@|bold,yellow " + PhysicalCalculator.formatNumber(pm.totalPages()) + " pages|@"));
        } else if (pm.estimatedWeightKg() > 10.0) {
            System.out.println(Ansi.AUTO.string("Your printed code weighs"));
            System.out.println(Ansi.AUTO.string("@|bold,yellow " + String.format("%.1f kilograms|@", pm.estimatedWeightKg())));
        } else {
            System.out.println(Ansi.AUTO.string("Your codebase contains"));
            System.out.println(Ansi.AUTO.string("@|bold,yellow " + PhysicalCalculator.formatNumber(result.totalCodeLines()) + " lines of code|@"));
        }
        System.out.println();
        System.out.println(Ansi.AUTO.string("@|bold,cyan " + FULL_BLOCK + "|@"));
        System.out.println();

        // SECTION 3: YOUR PROJECT
        System.out.println(Ansi.AUTO.string("You just scanned"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("@|bold " + result.projectName() + "|@"));
        System.out.println();
        System.out.println(Ansi.AUTO.string(PhysicalCalculator.formatNumber(result.totalCodeLines()) + " lines"));
        System.out.println(Ansi.AUTO.string(PhysicalCalculator.formatNumber(result.totalFiles()) + " files"));
        System.out.println(Ansi.AUTO.string(result.languageCount() + " languages"));
        System.out.println();
        
        List<LanguageStats> topLangs = result.languages().stream()
                .sorted((a, b) -> Long.compare(b.codeLines(), a.codeLines()))
                .toList();
        
        if (!topLangs.isEmpty()) {
            LanguageStats lang = topLangs.get(0);
            System.out.println(Ansi.AUTO.string("Largest language"));
            System.out.println();
            System.out.println(Ansi.AUTO.string("@|bold " + lang.language() + "|@"));
            double pct = lang.percentageOf(result.totalCodeLines());
            System.out.println(Ansi.AUTO.string("@|faint " + String.format("%.1f%%", pct) + "|@"));
            
            int barWidth = Math.min((int) (pct / 2.5), 23); // 23 max length for 100%
            String bar = "#".repeat(barWidth);
            System.out.println(Ansi.AUTO.string("@|blue " + bar + "|@"));
            System.out.println();
        }
        System.out.println(Ansi.AUTO.string("@|faint " + FULL_BLOCK + "|@"));
        System.out.println();

        // SECTION 4: IF PRINTED
        System.out.println(Ansi.AUTO.string("Printing your project would require"));
        System.out.println(Ansi.AUTO.string("@|bold " + PhysicalCalculator.formatNumber(pm.totalPages()) + " sheets of paper.|@"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("Those pages would form a stack"));
        System.out.println(Ansi.AUTO.string("@|bold " + String.format("%.1f centimetres tall,|@", pm.verticalStackMeters() * 100)));
        System.out.println(Ansi.AUTO.string("@|faint " + DynamicComparisons.getBestHeightComparison(pm) + "|@"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("The printed code would weigh"));
        System.out.println(Ansi.AUTO.string("@|bold " + String.format("%.1f kilograms,|@", pm.estimatedWeightKg())));
        System.out.println(Ansi.AUTO.string("@|faint " + DynamicComparisons.getBestWeightComparison(pm) + "|@"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("@|faint " + FULL_BLOCK + "|@"));
        System.out.println();

        // SECTION 5: IF LAID END TO END
        System.out.println(Ansi.AUTO.string("Every character placed beside the next"));
        System.out.println(Ansi.AUTO.string("would stretch"));
        if (pm.characterLengthKm() >= 1.0) {
            System.out.println(Ansi.AUTO.string("@|bold " + String.format("%.1f kilometres.|@", pm.characterLengthKm())));
        } else {
            System.out.println(Ansi.AUTO.string("@|bold " + String.format("%.1f metres.|@", pm.characterLengthKm() * 1000)));
        }
        System.out.println();
        System.out.println(Ansi.AUTO.string("Equivalent to"));
        for (String comp : DynamicComparisons.getBestDistanceComparisons(pm)) {
            System.out.println(Ansi.AUTO.string("@|bold,green " + comp + "|@"));
        }
        System.out.println();
        System.out.println(Ansi.AUTO.string("@|faint " + FULL_BLOCK + "|@"));
        System.out.println();

        // SECTION 6: TIME
        System.out.println(Ansi.AUTO.string("Reading"));
        System.out.println(Ansi.AUTO.string("@|bold " + HumanEffortCalculator.formatTimeExact(result, false) + "|@"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("Typing"));
        System.out.println(Ansi.AUTO.string("@|bold " + HumanEffortCalculator.formatTimeExact(result, true) + "|@"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("@|faint (Assuming a typing speed of 60 WPM)|@"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("@|faint " + FULL_BLOCK + "|@"));
        System.out.println();

        // SECTION 7: FUN FACT
        System.out.println(Ansi.AUTO.string("Fun fact"));
        System.out.println(Ansi.AUTO.string("Your project is now larger than @|bold " + Benchmarks.getClosestBenchmark(result) + "|@."));
        System.out.println();
    }

    private static void printCompact(ScanResult result, Settings settings, long durationMs) {
        PhysicalMetrics pm = PhysicalCalculator.calculate(result, settings);
        System.out.println();
        System.out.println(Ansi.AUTO.string("@|bold,cyan CODEMETER|@"));
        System.out.println(Ansi.AUTO.string(result.projectName() + " • " + PhysicalCalculator.formatNumber(result.totalCodeLines()) + " lines • " + result.totalFiles() + " files"));
        System.out.println(Ansi.AUTO.string("@|faint " + FULL_BLOCK + "|@"));
        System.out.println(Ansi.AUTO.string("@|bold Stack:|@ " + String.format("%.1f cm", pm.verticalStackMeters() * 100) + " (" + DynamicComparisons.getBestHeightComparison(pm).replace("≈ ", "") + ")"));
        System.out.println(Ansi.AUTO.string("@|bold Weight:|@ " + String.format("%.1f kg", pm.estimatedWeightKg()) + " (" + DynamicComparisons.getBestWeightComparison(pm).replace("≈ ", "") + ")"));
        
        String lengthStr = pm.characterLengthKm() >= 1.0 ? String.format("%.1f km", pm.characterLengthKm()) : String.format("%.1f m", pm.characterLengthKm() * 1000);
        System.out.println(Ansi.AUTO.string("@|bold Length:|@ " + lengthStr + " (" + DynamicComparisons.getBestDistanceComparisons(pm).get(0).replace("✓ ", "") + ")"));
        System.out.println(Ansi.AUTO.string("@|faint " + FULL_BLOCK + "|@"));
    }

    private static void printMinimal(ScanResult result, Settings settings, long durationMs) {
        System.out.println(Ansi.AUTO.string(result.projectName() + ": " + PhysicalCalculator.formatNumber(result.totalCodeLines()) + " lines, " + PhysicalCalculator.formatNumber(result.totalFiles()) + " files."));
    }

    private static void printCI(ScanResult result, long durationMs) {
        System.out.println("Scan completed in " + durationMs + "ms");
        System.out.println("Project: " + result.projectName());
        System.out.println("Lines: " + result.totalCodeLines());
        System.out.println("Files: " + result.totalFiles());
        System.out.println("Characters: " + result.totalCharacters());
    }

    public static void printAchievements(List<Achievement> unlockedAchievements) {
        if (unlockedAchievements != null && !unlockedAchievements.isEmpty()) {
            System.out.println(Ansi.AUTO.string("@|faint " + FULL_BLOCK + "|@"));
            System.out.println();
            for (Achievement ach : unlockedAchievements) {
                System.out.println(Ansi.AUTO.string("Achievement unlocked"));
                System.out.println();
                System.out.println(Ansi.AUTO.string("@|bold,yellow " + ach.icon() + " " + ach.displayName() + "|@"));
                System.out.println(Ansi.AUTO.string(ach.description()));
                System.out.println();
            }
        }
    }

    public static void printFooter(long durationMs) {
        System.out.println(Ansi.AUTO.string("@|faint " + FULL_BLOCK + "|@"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("@|faint Story generated by CodeMeter|@"));
        System.out.println(Ansi.AUTO.string("@|faint Powered by SCC|@"));
        System.out.println(Ansi.AUTO.string("@|faint Generated in " + (durationMs / 1000.0) + " seconds.|@"));
        System.out.println();
    }
}
