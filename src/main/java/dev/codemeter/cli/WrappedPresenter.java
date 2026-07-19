package dev.codemeter.cli;

import dev.codemeter.core.metrics.PhysicalCalculator;
import dev.codemeter.core.model.Achievement;
import dev.codemeter.core.model.HistoryEntry;
import picocli.CommandLine.Help.Ansi;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class WrappedPresenter {

    private static final String FULL_BLOCK = "==============================================";
    private static final String LINE = "----------------------------------------------";

    public static void printWrapped(List<HistoryEntry> history, List<Achievement> achievements) {
        if (history == null || history.isEmpty()) {
            System.out.println(Ansi.AUTO.string("@|bold,yellow No history found.|@ Start by scanning a project with @|bold scan .|@"));
            return;
        }

        System.out.println(Ansi.AUTO.string("@|bold,magenta " + FULL_BLOCK + "|@"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("           @|bold,magenta YOUR YEAR IN CODE|@"));
        System.out.println();
        System.out.println(Ansi.AUTO.string("@|bold,magenta " + FULL_BLOCK + "|@"));
        System.out.println();

        // Total scanned
        long totalScans = history.size();
        long totalLinesSeen = history.stream().mapToLong(HistoryEntry::totalCodeLines).sum();
        long uniqueProjects = history.stream().map(HistoryEntry::projectName).distinct().count();

        System.out.println(Ansi.AUTO.string("This year, you scanned"));
        System.out.println(Ansi.AUTO.string("@|bold,magenta " + uniqueProjects + " projects|@"));
        System.out.println(Ansi.AUTO.string("a total of " + totalScans + " times."));
        System.out.println();
        System.out.println(Ansi.AUTO.string("In total, CodeMeter processed"));
        System.out.println(Ansi.AUTO.string("@|bold " + PhysicalCalculator.formatNumber(totalLinesSeen) + " lines of code|@"));
        System.out.println(Ansi.AUTO.string("across all your scans."));
        System.out.println();

        System.out.println(Ansi.AUTO.string("@|faint " + LINE + "|@"));
        System.out.println();

        // Largest repository
        Optional<HistoryEntry> largest = history.stream().max((a, b) -> Long.compare(a.totalCodeLines(), b.totalCodeLines()));
        if (largest.isPresent()) {
            System.out.println(Ansi.AUTO.string("Your largest repository was"));
            System.out.println(Ansi.AUTO.string("@|bold,cyan " + largest.get().projectName() + "|@"));
            System.out.println(Ansi.AUTO.string("weighing in at " + PhysicalCalculator.formatNumber(largest.get().totalCodeLines()) + " lines."));
            System.out.println();
            System.out.println(Ansi.AUTO.string("@|faint " + LINE + "|@"));
            System.out.println();
        }

        // Achievements
        if (achievements != null && !achievements.isEmpty()) {
            System.out.println(Ansi.AUTO.string("You unlocked"));
            System.out.println(Ansi.AUTO.string("@|bold,yellow " + achievements.size() + " achievements|@"));
            System.out.println();
            
            // Show up to 3 achievements
            List<Achievement> topAchievements = achievements.stream().limit(3).collect(Collectors.toList());
            for (Achievement ach : topAchievements) {
                System.out.println(Ansi.AUTO.string("@|bold " + ach.icon() + " " + ach.displayName() + "|@"));
                System.out.println(Ansi.AUTO.string("@|faint " + ach.description() + "|@"));
                System.out.println();
            }
            if (achievements.size() > 3) {
                System.out.println(Ansi.AUTO.string("@|faint ...and " + (achievements.size() - 3) + " more.|@"));
            }
            System.out.println(Ansi.AUTO.string("@|faint " + LINE + "|@"));
            System.out.println();
        }

        System.out.println(Ansi.AUTO.string("@|bold,magenta CodeMeter Wrapped|@"));
        System.out.println(Ansi.AUTO.string("@|faint Share your scale.|@"));
        System.out.println(Ansi.AUTO.string("@|magenta " + FULL_BLOCK + "|@"));
        System.out.println();
    }
}
