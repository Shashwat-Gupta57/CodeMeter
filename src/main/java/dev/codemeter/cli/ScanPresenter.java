package dev.codemeter.cli;

import dev.codemeter.core.model.LanguageStats;
import dev.codemeter.core.model.PhysicalMetrics;
import dev.codemeter.core.model.ScanResult;
import dev.codemeter.core.model.Settings;
import dev.codemeter.core.model.Achievement;
import dev.codemeter.core.metrics.PhysicalCalculator;
import picocli.CommandLine.Help.Ansi;

import java.util.List;

public class ScanPresenter {

    private static final String FULL_BLOCK = "████████████████████████████████████████████████";
    private static final String HALF_BLOCK = "████████████████████████████████";
    private static final String LINE = "────────────────────────────────────────────────";

    public static void printHeader(String path) {
        System.out.println(Ansi.AUTO.string("@|bold,cyan " + FULL_BLOCK + "|@"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("                @|bold,cyan CODEMETER|@"));
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
        String bar = "█".repeat(filled) + "░".repeat(width - filled);
        System.out.print(Ansi.AUTO.string("\r@|cyan " + bar + "|@ @|bold " + percent + "%|@"));
    }

    public static void printProgressComplete() {
        int width = 33;
        String bar = "█".repeat(width);
        System.out.println(Ansi.AUTO.string("\r@|cyan " + bar + "|@ @|bold 100%|@"));
    }

    public static void printResults(ScanResult result, Settings settings, long durationMs, List<Achievement> unlockedAchievements) {
        System.out.println(Ansi.AUTO.string("@|faint Completed in " + (durationMs / 1000.0) + " seconds|@"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("@|faint " + LINE + "|@"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("You just scanned"));
        System.out.println(Ansi.AUTO.string("@|bold,cyan " + HALF_BLOCK + "|@"));
        System.out.println(Ansi.AUTO.string("@|bold " + result.projectName() + "|@"));
        System.out.println(Ansi.AUTO.string("@|bold,cyan " + HALF_BLOCK + "|@"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("@|bold,yellow " + PhysicalCalculator.formatNumber(result.totalCodeLines()) + "|@"));
        System.out.println(Ansi.AUTO.string("@|faint LINES OF CODE|@"));
        System.out.println();
        System.out.println(Ansi.AUTO.string(PhysicalCalculator.formatNumber(result.totalFiles()) + " files"));
        System.out.println(Ansi.AUTO.string(result.languageCount() + " languages"));
        System.out.println();
        
        System.out.println(Ansi.AUTO.string("@|faint " + LINE + "|@"));
        System.out.println();

        // Paper Stack & Weight
        PhysicalMetrics pm = PhysicalCalculator.calculate(result, settings);
        System.out.println(Ansi.AUTO.string("If printed"));
        System.out.println(Ansi.AUTO.string("@|bold " + PhysicalCalculator.formatNumber(pm.totalPages()) + " pages|@"));
        System.out.println(Ansi.AUTO.string("@|magenta " + HALF_BLOCK + "|@"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("Stack height"));
        System.out.println(Ansi.AUTO.string("@|bold " + String.format("%.1f cm", pm.verticalStackMeters() * 100) + "|@"));
        System.out.println(Ansi.AUTO.string("@|faint " + DynamicComparisons.getBestHeightComparison(pm) + "|@"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("Weight"));
        System.out.println(Ansi.AUTO.string("@|bold " + String.format("%.1f kg", pm.estimatedWeightKg()) + "|@"));
        System.out.println(Ansi.AUTO.string("@|faint " + DynamicComparisons.getBestWeightComparison(pm) + "|@"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("Shelf width"));
        System.out.println(Ansi.AUTO.string("@|bold " + String.format("%.1f cm", pm.shelfWidthMeters() * 100) + "|@"));
        System.out.println(Ansi.AUTO.string("@|faint " + DynamicComparisons.getBestShelfComparison(pm) + "|@"));
        System.out.println();
        
        System.out.println(Ansi.AUTO.string("@|faint " + LINE + "|@"));
        System.out.println();

        // Distance & Characters
        System.out.println(Ansi.AUTO.string("Characters"));
        System.out.println(Ansi.AUTO.string("@|bold " + PhysicalCalculator.formatNumber(result.totalCharacters()) + "|@"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("If every character touched the next"));
        if (pm.characterLengthKm() >= 1.0) {
            System.out.println(Ansi.AUTO.string("@|bold " + String.format("%.1f km", pm.characterLengthKm()) + "|@"));
        } else {
            System.out.println(Ansi.AUTO.string("@|bold " + String.format("%.1f m", pm.characterLengthKm() * 1000) + "|@"));
        }
        System.out.println(Ansi.AUTO.string("@|faint ═══════════════════════════════|@"));
        System.out.println(Ansi.AUTO.string("Equivalent to"));
        for (String comp : DynamicComparisons.getBestDistanceComparisons(pm)) {
            System.out.println(Ansi.AUTO.string("@|bold,green " + comp + "|@"));
        }
        System.out.println();
        
        System.out.println(Ansi.AUTO.string("@|faint " + LINE + "|@"));
        System.out.println();
        
        // Typing / Reading
        System.out.println(Ansi.AUTO.string("Time"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("Typing"));
        System.out.println(Ansi.AUTO.string("@|faint 60 WPM|@"));
        System.out.println(Ansi.AUTO.string("@|bold " + HumanEffortCalculator.formatTimeExact(result, true) + "|@"));
        System.out.println(Ansi.AUTO.string("@|faint " + HumanEffortCalculator.calculateTypingTime(result) + "|@"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("Reading"));
        System.out.println(Ansi.AUTO.string("@|bold " + HumanEffortCalculator.formatTimeExact(result, false) + "|@"));
        System.out.println();
        
        System.out.println(Ansi.AUTO.string("@|faint " + LINE + "|@"));
        System.out.println();

        // Languages
        List<LanguageStats> topLangs = result.languages().stream()
                .sorted((a, b) -> Long.compare(b.codeLines(), a.codeLines()))
                .toList();
        
        if (!topLangs.isEmpty()) {
            LanguageStats lang = topLangs.get(0);
            System.out.println(Ansi.AUTO.string("Largest language"));
            System.out.println(Ansi.AUTO.string("@|bold " + lang.language() + "|@"));
            double pct = lang.percentageOf(result.totalCodeLines());
            System.out.println(Ansi.AUTO.string("@|faint " + String.format("%.1f%%", pct) + "|@"));
            
            int barWidth = Math.min((int) (pct / 2.5), 23); // 23 max length for 100%
            String bar = "█".repeat(barWidth);
            System.out.println(Ansi.AUTO.string("@|blue " + bar + "|@"));
            System.out.println();
            System.out.println(Ansi.AUTO.string("@|faint " + LINE + "|@"));
            System.out.println();
        }

        // Fun Fact
        System.out.println(Ansi.AUTO.string("Fun fact"));
        System.out.println(Ansi.AUTO.string("Your project is now larger than"));
        System.out.println(Ansi.AUTO.string("@|bold " + Benchmarks.getClosestBenchmark(result) + "|@"));
        System.out.println(Ansi.AUTO.string("@|faint (Approximate)|@"));
        System.out.println();
        
        System.out.println(Ansi.AUTO.string("@|faint " + LINE + "|@"));
        System.out.println();
        
        // Achievements
        if (unlockedAchievements != null && !unlockedAchievements.isEmpty()) {
            for (Achievement ach : unlockedAchievements) {
                System.out.println(Ansi.AUTO.string("Achievement unlocked"));
                System.out.println(Ansi.AUTO.string("@|bold,yellow " + ach.icon() + " " + ach.displayName() + "|@"));
                System.out.println(Ansi.AUTO.string("@|faint " + ach.description() + "|@"));
                System.out.println();
                System.out.println(Ansi.AUTO.string("@|faint " + LINE + "|@"));
                System.out.println();
            }
        }
        
        // Footer
        System.out.println(Ansi.AUTO.string("@|faint Generated by CodeMeter|@"));
        System.out.println(Ansi.AUTO.string("@|faint Powered by SCC|@"));
        System.out.println(Ansi.AUTO.string("@|faint " + (durationMs / 1000.0) + " seconds|@"));
        System.out.println(Ansi.AUTO.string("@|cyan " + FULL_BLOCK + "|@"));
        System.out.println();
    }
}
