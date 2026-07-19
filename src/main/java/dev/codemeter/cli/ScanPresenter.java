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

    private static final String FULL_BLOCK = "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";

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
        String bar = "█".repeat(filled) + "░".repeat(width - filled);
        System.out.print(Ansi.AUTO.string("\r@|cyan " + bar + "|@ @|bold " + percent + "%|@"));
    }

    public static void printProgressComplete() {
        int width = 33;
        String bar = "█".repeat(width);
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

        printStory(result, settings, durationMs, unlockedAchievements);
    }

    private static void printStory(ScanResult result, Settings settings, long durationMs, List<Achievement> unlockedAchievements) {
        PhysicalMetrics pm = PhysicalCalculator.calculate(result, settings);
        
        System.out.println();
        System.out.println(Ansi.AUTO.string("@|faint Completed in " + (durationMs / 1000.0) + " seconds.|@"));
        System.out.println();
        
        // SECTION 1: THE HEADLINE
        System.out.println(Ansi.AUTO.string("@|bold,cyan " + getFullBlock() + "|@"));
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
        System.out.println(Ansi.AUTO.string("@|bold,cyan " + getFullBlock() + "|@"));
        System.out.println();

        // SECTION 2: WHAT YOU JUST SCANNED
        System.out.println(Ansi.AUTO.string("WHAT YOU JUST SCANNED"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("@|bold " + result.projectName() + "|@"));
        System.out.println();
        System.out.println(Ansi.AUTO.string(PhysicalCalculator.formatNumber(result.totalDirectories()) + " directories"));
        System.out.println(Ansi.AUTO.string(PhysicalCalculator.formatNumber(result.totalFiles()) + " files"));
        System.out.println(Ansi.AUTO.string(result.languageCount() + " languages"));
        System.out.println();
        System.out.println(Ansi.AUTO.string(PhysicalCalculator.formatNumber(result.totalCodeLines()) + " lines of code"));
        System.out.println(Ansi.AUTO.string(PhysicalCalculator.formatNumber(result.totalCommentLines()) + " comments"));
        System.out.println(Ansi.AUTO.string(PhysicalCalculator.formatNumber(result.totalBlankLines()) + " blank lines"));
        System.out.println();
        System.out.println(Ansi.AUTO.string(PhysicalCalculator.formatNumber(result.totalCharacters()) + " characters"));
        System.out.println(Ansi.AUTO.string(PhysicalCalculator.formatNumber(result.totalWords()) + " words"));
        System.out.println(Ansi.AUTO.string(PhysicalCalculator.formatMetric(result.totalBytes(), "B")));
        System.out.println();
        System.out.println(Ansi.AUTO.string("@|faint " + getFullBlock() + "|@"));
        System.out.println();

        // SECTION 3: LANGUAGE BREAKDOWN
        System.out.println(Ansi.AUTO.string("LANGUAGE BREAKDOWN"));
        System.out.println();
        List<LanguageStats> langs = result.languages().stream()
                .sorted((a, b) -> Long.compare(b.codeLines(), a.codeLines()))
                .toList();
        for (LanguageStats lang : langs) {
            double pct = lang.percentageOf(result.totalCodeLines());
            System.out.println(Ansi.AUTO.string(String.format("%-15s @|bold %5.1f%%|@", lang.language(), pct)));
            int barWidth = Math.min((int) (pct / 2.5), 30);
            char fillChar = '█';
            if (barWidth > 0) {
                System.out.println(Ansi.AUTO.string("@|blue " + String.valueOf(fillChar).repeat(barWidth) + "|@"));
            } else {
                System.out.println(Ansi.AUTO.string("@|faint -|@"));
            }
            System.out.println();
        }
        System.out.println(Ansi.AUTO.string("@|faint " + getFullBlock() + "|@"));
        System.out.println();

        // SECTION 4: PHYSICAL PRINT
        System.out.println(Ansi.AUTO.string("PHYSICAL PRINT"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("Printing your project would require"));
        System.out.println(Ansi.AUTO.string("@|bold " + PhysicalCalculator.formatNumber(pm.totalPages()) + " single-sided pages|@"));
        System.out.println(Ansi.AUTO.string("or @|bold " + PhysicalCalculator.formatNumber((long)pm.sheetsRequired()) + " sheets|@ if double-sided."));
        System.out.println();
        System.out.println(Ansi.AUTO.string("You would need @|bold " + (long)Math.ceil(pm.printerTrays()) + " standard printer trays|@ to hold this paper."));
        System.out.println(Ansi.AUTO.string("It would fill @|bold " + (long)pm.bindersRequired() + " massive ring binders|@"));
        System.out.println(Ansi.AUTO.string("and require @|bold " + (long)pm.boxesNeeded() + " cardboard moving boxes|@ to transport."));
        System.out.println();
        System.out.println(Ansi.AUTO.string("The paper stack would weigh"));
        System.out.println(Ansi.AUTO.string("@|bold " + String.format("%.1f kilograms|@", pm.estimatedWeightKg())));
        System.out.println(Ansi.AUTO.string("@|faint " + DynamicComparisons.getBestWeightComparison(pm) + "|@"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("Estimated print time: @|bold " + String.format("%.1f hours|@", pm.timeToPrintMinutes() / 60.0)));
        System.out.println(Ansi.AUTO.string("@|faint (Estimated using " + settings.getAveragePrintSpeedPpm() + " pages per minute)|@"));
        System.out.println(Ansi.AUTO.string("Estimated printing cost: @|bold $" + String.format("%.2f|@", pm.estimatedPrintingCost())));
        System.out.println(Ansi.AUTO.string("@|faint (Estimated using $" + String.format("%.2f", settings.getPrintingCostPerPage()) + " per page)|@"));
        System.out.println(Ansi.AUTO.string("Estimated paper production consumed @|bold " + String.format("%.3f trees|@", pm.treesRequired())));
        System.out.println(Ansi.AUTO.string("@|faint (Calculated using " + settings.getTreePagesPerTree() + " pages per tree)|@"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("@|faint " + getFullBlock() + "|@"));
        System.out.println();

        // SECTION 5: IF PRINTED AS BOOKS
        System.out.println(Ansi.AUTO.string("IF PRINTED AS BOOKS"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("Your code is thick enough to bind into"));
        System.out.println(Ansi.AUTO.string("@|bold " + (long)pm.booksRequired() + " standard books.|@"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("That is equivalent to:"));
        for (String comp : DynamicComparisons.getBookComparisons(pm)) {
            System.out.println(Ansi.AUTO.string("@|bold,magenta " + comp + "|@"));
        }
        System.out.println();
        System.out.println(Ansi.AUTO.string("It would take up @|bold " + String.format("%.1f metres|@", pm.shelfWidthMeters()) + " of shelf space."));
        System.out.println();
        System.out.println(Ansi.AUTO.string("@|faint " + getFullBlock() + "|@"));
        System.out.println();

        // SECTION 6: LINE STACK
        System.out.println(Ansi.AUTO.string("LINE STACK"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("If you stacked every single sheet of paper vertically,"));
        System.out.println(Ansi.AUTO.string("the tower would be"));
        System.out.println(Ansi.AUTO.string("@|bold " + String.format("%.1f centimetres tall.|@", pm.verticalStackMeters() * 100)));
        System.out.println();
        System.out.println(Ansi.AUTO.string(DynamicComparisons.getBestHeightComparison(pm)));
        System.out.println();
        System.out.println(Ansi.AUTO.string("@|faint " + getFullBlock() + "|@"));
        System.out.println();

        // SECTION 7: CHARACTER DISTANCE
        System.out.println(Ansi.AUTO.string("CHARACTER DISTANCE"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("If you placed every character in your codebase"));
        System.out.println(Ansi.AUTO.string("side by side, it would stretch"));
        if (pm.characterLengthKm() >= 1.0) {
            System.out.println(Ansi.AUTO.string("@|bold " + String.format("%.1f kilometres.|@", pm.characterLengthKm())));
        } else {
            System.out.println(Ansi.AUTO.string("@|bold " + String.format("%.1f metres.|@", pm.characterLengthKm() * 1000)));
        }
        System.out.println(Ansi.AUTO.string("@|faint (Calculated using " + String.format("%.2f", settings.getCharacterWidthMm()) + " mm character width)|@"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("Equivalent to:"));
        for (String comp : DynamicComparisons.getBestDistanceComparisons(pm)) {
            System.out.println(Ansi.AUTO.string("@|bold,green " + comp + "|@"));
        }
        System.out.println();
        System.out.println(Ansi.AUTO.string("@|faint " + getFullBlock() + "|@"));
        System.out.println();

        // SECTION 8: READING & TYPING
        System.out.println("READING & TYPING");
        System.out.println();
        double totalWords = result.totalCharacters() / settings.getAverageWordLength();
        double readHrs = totalWords / (settings.getReadingSpeedWpm() * 60);
        System.out.println(Ansi.AUTO.string("Reading every line continuously would take"));
        System.out.println(Ansi.AUTO.string("@|bold " + HumanEffortCalculator.formatTimeExact(result, false, settings) + ".|@"));
        System.out.println(Ansi.AUTO.string("@|faint (Estimated using " + settings.getReadingSpeedWpm() + " words per minute)|@"));
        System.out.println(Ansi.AUTO.string("@|faint That's " + String.format("%.1f", readHrs / settings.getWorkingHoursPerDay()) + " working days (at " + settings.getWorkingHoursPerDay() + "hrs/day).|@"));
        System.out.println();
        double typeHrs = totalWords / (settings.getTypingSpeedWpm() * 60);
        System.out.println(Ansi.AUTO.string("Typing everything again from scratch would require"));
        System.out.println(Ansi.AUTO.string("@|bold " + HumanEffortCalculator.formatTimeExact(result, true, settings) + ".|@"));
        System.out.println(Ansi.AUTO.string("@|faint (Estimated using " + settings.getTypingSpeedWpm() + " WPM)|@"));
        System.out.println(Ansi.AUTO.string("@|faint Equivalent to " + String.format("%.1f", typeHrs / settings.getWorkingHoursPerDay()) + " working days.|@"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("@|faint " + getFullBlock() + "|@"));
        System.out.println();

        // SECTION 9: MEMORY
        System.out.println(Ansi.AUTO.string("MEMORY FOOTPRINT"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("Estimated raw UTF-8 footprint: @|bold " + PhysicalCalculator.formatMetric(pm.estimatedUtf8Size(), "B") + "|@"));
        System.out.println(Ansi.AUTO.string("Estimated raw UTF-16 footprint: @|bold " + PhysicalCalculator.formatMetric(pm.estimatedUtf16Size(), "B") + "|@"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("Estimated LLM Token Count: @|bold " + PhysicalCalculator.formatNumber(pm.estimatedTokenCount()) + "|@"));
        System.out.println(Ansi.AUTO.string("Estimated AST Nodes: @|bold " + PhysicalCalculator.formatNumber(pm.estimatedAstNodes()) + "|@"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("@|faint " + getFullBlock() + "|@"));
        System.out.println();

        // SECTION 10: PROJECT DENSITY
        System.out.println(Ansi.AUTO.string("PROJECT DENSITY"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("Average file size: @|bold " + PhysicalCalculator.formatMetric(result.averageFileSize(), "B") + "|@"));
        System.out.println(Ansi.AUTO.string("Average lines per file: @|bold " + (result.totalFiles() > 0 ? result.totalLines() / result.totalFiles() : 0) + "|@"));
        System.out.println(Ansi.AUTO.string("Average line length: @|bold " + String.format("%.1f chars", result.averageLineLength()) + "|@"));
        double commentRatio = result.totalCodeLines() > 0 ? (double)result.totalCommentLines() / result.totalCodeLines() * 100 : 0;
        System.out.println(Ansi.AUTO.string("Comment ratio: @|bold " + String.format("%.1f%%", commentRatio) + "|@"));
        if (!result.largestFile().isEmpty()) {
            System.out.println();
            System.out.println(Ansi.AUTO.string("The largest monolithic file is"));
            System.out.println(Ansi.AUTO.string("@|bold " + result.largestFile() + "|@"));
            System.out.println(Ansi.AUTO.string("weighing in at " + PhysicalCalculator.formatMetric(result.largestFileLines(), "B")));
        }
        System.out.println();
        System.out.println(Ansi.AUTO.string("@|faint " + getFullBlock() + "|@"));
        System.out.println();

        // SECTION 11: REPOSITORY SCALE (If applicable)
        String scale = DynamicComparisons.getRepositoryScaleBenchmark(result);
        if (scale != null) {
            System.out.println(Ansi.AUTO.string("REPOSITORY SCALE"));
            System.out.println();
            System.out.println(Ansi.AUTO.string("Your codebase has surpassed the scale of"));
            System.out.println(Ansi.AUTO.string("@|bold,cyan " + scale + "|@"));
            System.out.println();
            System.out.println(Ansi.AUTO.string("@|faint " + getFullBlock() + "|@"));
            System.out.println();
        }

        // SECTION 12: TIMELINE (If Git is present)
        if (result.gitStats() != null) {
            System.out.println(Ansi.AUTO.string("TIMELINE"));
            System.out.println();
            System.out.println(Ansi.AUTO.string("First commit: @|bold " + result.gitStats().creationDate() + "|@"));
            System.out.println(Ansi.AUTO.string("Latest commit: @|bold " + result.gitStats().lastCommitDate() + "|@"));
            System.out.println();
            System.out.println(Ansi.AUTO.string("Repository age: @|bold " + result.gitStats().ageInDays() + " days|@"));
            System.out.println(Ansi.AUTO.string("Total commits: @|bold " + PhysicalCalculator.formatNumber(result.gitStats().totalCommits()) + "|@"));
            double commitsPerMonth = result.gitStats().totalCommits() / (result.gitStats().ageInDays() / 30.0);
            System.out.println(Ansi.AUTO.string("Average commits per month: @|bold " + String.format("%.1f", commitsPerMonth) + "|@"));
            System.out.println();
            System.out.println(Ansi.AUTO.string("@|faint " + getFullBlock() + "|@"));
            System.out.println();
        }

        // SECTION 13: FUN FACTS
        System.out.println(Ansi.AUTO.string("FUN FACTS"));
        System.out.println();
        for (String fact : DynamicComparisons.getFunFacts(result, pm)) {
            System.out.println(Ansi.AUTO.string("• " + fact));
        }
        System.out.println();
        
        // SECTION 14: ACHIEVEMENTS
        if (unlockedAchievements != null && !unlockedAchievements.isEmpty()) {
            System.out.println(Ansi.AUTO.string("@|faint " + getFullBlock() + "|@"));
            System.out.println();
            System.out.println(Ansi.AUTO.string("ACHIEVEMENTS UNLOCKED"));
            System.out.println();
            for (Achievement ach : unlockedAchievements) {
                System.out.println(Ansi.AUTO.string("@|bold,yellow " + ach.icon() + " " + ach.displayName() + "|@"));
                System.out.println(Ansi.AUTO.string("@|faint " + ach.description() + "|@"));
                System.out.println();
            }
        }

        // ASSUMPTIONS BLOCK
        System.out.println(Ansi.AUTO.string("@|faint " + getFullBlock() + "|@"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("PRINTING ASSUMPTIONS"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("@|faint Font: " + settings.getFontName() + " " + settings.getFontSizePt() + "pt|@"));
        System.out.println(Ansi.AUTO.string("@|faint Paper: " + settings.getPaperSize() + "|@"));
        System.out.println(Ansi.AUTO.string("@|faint Character Width: " + String.format("%.2f", settings.getCharacterWidthMm()) + " mm|@"));
        System.out.println(Ansi.AUTO.string("@|faint Character Height: " + String.format("%.2f", settings.getCharacterHeightMm()) + " mm|@"));
        System.out.println(Ansi.AUTO.string("@|faint Line Spacing: " + settings.getLineSpacing() + "|@"));
        System.out.println(Ansi.AUTO.string("@|faint Paper Thickness: " + String.format("%.2f", settings.getPaperThicknessMm()) + " mm|@"));
        System.out.println(Ansi.AUTO.string("@|faint Paper Weight: " + settings.getPaperWeightGsm() + " GSM|@"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("@|faint These assumptions were used to calculate every physical metric in this Story.|@"));
        System.out.println(Ansi.AUTO.string("@|faint Run 'codemeter config' to customise them.|@"));
        System.out.println();

        // SUMMARY
        System.out.println(Ansi.AUTO.string("@|faint " + getFullBlock() + "|@"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("SUMMARY"));
        System.out.println();
        
        String sum1 = pm.characterLengthKm() >= 1.0 ? String.format("%.1f km", pm.characterLengthKm()) : String.format("%.1f m", pm.characterLengthKm() * 1000);
        System.out.println(Ansi.AUTO.string("@|bold " + sum1 + "|@"));
        System.out.println(Ansi.AUTO.string("@|bold " + PhysicalCalculator.formatNumber(pm.totalPages()) + " pages|@"));
        System.out.println(Ansi.AUTO.string("@|bold " + PhysicalCalculator.formatNumber(result.totalCodeLines()) + " lines|@"));
        
        System.out.println();
    }

    private static String getFullBlock() {
        return "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";
    }

    private static void printCompact(ScanResult result, Settings settings, long durationMs) {
        PhysicalMetrics pm = PhysicalCalculator.calculate(result, settings);
        System.out.println();
        System.out.println(Ansi.AUTO.string("@|bold,cyan CODEMETER|@"));
        System.out.println(Ansi.AUTO.string(result.projectName() + " • " + PhysicalCalculator.formatNumber(result.totalCodeLines()) + " lines • " + result.totalFiles() + " files"));
        System.out.println(Ansi.AUTO.string("@|faint " + getFullBlock() + "|@"));
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
