package dev.codemeter.tui.screens;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import dev.codemeter.core.metrics.PhysicalCalculator;
import dev.codemeter.core.model.HistoryEntry;
import dev.codemeter.core.storage.StorageManager;
import dev.codemeter.tui.Renderer;
import dev.codemeter.tui.Theme;

import java.time.*;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Wrapped screen — Spotify Wrapped-style code year review.
 * Multi-page narrative experience with animated transitions.
 */
public final class WrappedScreen {

    private static final int TOTAL_PAGES = 8;

    private WrappedScreen() {}

    public static void render(TextGraphics g, TerminalSize size, StorageManager storage, int page) {
        int w = size.getColumns();
        int h = size.getRows();

        Renderer.clearArea(g, 0, 0, w, h, Theme.BG_PRIMARY);

        List<HistoryEntry> history = storage.getGlobalHistory();

        if (history.isEmpty()) {
            Renderer.drawCentered(g, h / 2 - 1, w, "No scan history for Wrapped.", Theme.TEXT_MUTED);
            Renderer.drawCentered(g, h / 2 + 1, w, "Scan some projects first!", Theme.TEXT_SECONDARY);
            Renderer.drawCentered(g, h - 2, w, "Press q to go back", Theme.TEXT_MUTED);
            return;
        }

        // Clamp page
        int safePage = Math.max(0, Math.min(page, TOTAL_PAGES - 1));

        switch (safePage) {
            case 0 -> renderTitlePage(g, w, h, history);
            case 1 -> renderTotalLinesPage(g, w, h, history);
            case 2 -> renderPhysicalPage(g, w, h, history);
            case 3 -> renderMostProductiveMonth(g, w, h, history);
            case 4 -> renderLanguageOfTheYear(g, w, h, history);
            case 5 -> renderAchievementsPage(g, w, h, storage);
            case 6 -> renderGrowthPage(g, w, h, history);
            case 7 -> renderSharePage(g, w, h, history);
        }

        // Page indicator
        renderPageIndicator(g, w, h, safePage);

        // Navigation hint
        g.setForegroundColor(Theme.TEXT_MUTED);
        g.putString(2, h - 1, "← →  Navigate    q  Exit");
    }

    private static void renderTitlePage(TextGraphics g, int w, int h, List<HistoryEntry> history) {
        int cy = h / 2 - 4;

        Renderer.drawCentered(g, cy, w, "✦", Theme.ACCENT_PRIMARY);
        cy += 2;
        Renderer.drawCentered(g, cy, w, "Your Code", Theme.TEXT_MUTED);
        cy += 1;
        Renderer.drawCentered(g, cy, w, "WRAPPED", Theme.ACCENT_PRIMARY);
        cy += 2;

        int year = Year.now().getValue();
        Renderer.drawCentered(g, cy, w, String.valueOf(year), Theme.ACCENT_CYAN);
        cy += 3;

        Renderer.drawCentered(g, cy, w, history.size() + " scans across " +
                countUniqueProjects(history) + " projects", Theme.TEXT_SECONDARY);
        cy += 2;

        Renderer.drawCentered(g, cy, w, "Press → to begin", Theme.TEXT_MUTED);
    }

    private static void renderTotalLinesPage(TextGraphics g, int w, int h, List<HistoryEntry> history) {
        int cy = h / 2 - 5;

        Renderer.drawCentered(g, cy, w, "This year, you wrote", Theme.TEXT_SECONDARY);
        cy += 3;

        // Find max LOC across all scans
        long maxLoc = history.stream()
                .mapToLong(HistoryEntry::totalCodeLines)
                .max().orElse(0);

        g.setForegroundColor(Theme.ACCENT_PRIMARY);
        String locStr = PhysicalCalculator.formatNumber(maxLoc);
        // Big number
        Renderer.drawCentered(g, cy, w, locStr, Theme.ACCENT_PRIMARY);
        cy += 2;
        Renderer.drawCentered(g, cy, w, "lines of code", Theme.TEXT_SECONDARY);
        cy += 3;

        // Fun fact
        long pages = maxLoc / 55;
        Renderer.drawCentered(g, cy, w, "That's " + PhysicalCalculator.formatNumber(pages) + " printed pages!", Theme.ACCENT_CYAN);
    }

    private static void renderPhysicalPage(TextGraphics g, int w, int h, List<HistoryEntry> history) {
        int cy = h / 2 - 5;

        long maxChars = history.stream()
                .mapToLong(HistoryEntry::totalCharacters)
                .max().orElse(0);

        double charLengthKm = (maxChars * 2.5) / 1_000_000.0;

        Renderer.drawCentered(g, cy, w, "Equivalent to", Theme.TEXT_SECONDARY);
        cy += 3;

        Renderer.drawCentered(g, cy, w, String.format("%.1f km", charLengthKm), Theme.ACCENT_SUCCESS);
        cy += 2;
        Renderer.drawCentered(g, cy, w, "of characters laid end to end", Theme.TEXT_SECONDARY);
        cy += 3;

        if (charLengthKm > 42.195) {
            double marathons = charLengthKm / 42.195;
            Renderer.drawCentered(g, cy, w,
                    String.format("That's %.1f marathons! 🏅", marathons), Theme.ACCENT_WARNING);
        } else if (charLengthKm > 1) {
            Renderer.drawCentered(g, cy, w,
                    "Keep coding to reach a marathon!", Theme.ACCENT_INFO);
        }
    }

    private static void renderMostProductiveMonth(TextGraphics g, int w, int h, List<HistoryEntry> history) {
        int cy = h / 2 - 5;

        // Group scans by month
        Map<Month, Long> byMonth = history.stream()
                .collect(Collectors.groupingBy(
                        e -> e.timestamp().atZone(ZoneId.systemDefault()).getMonth(),
                        Collectors.counting()));

        Month bestMonth = byMonth.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(Month.JANUARY);

        long bestCount = byMonth.getOrDefault(bestMonth, 0L);

        Renderer.drawCentered(g, cy, w, "Your most productive month", Theme.TEXT_SECONDARY);
        cy += 3;

        Renderer.drawCentered(g, cy, w,
                bestMonth.getDisplayName(TextStyle.FULL, Locale.ENGLISH), Theme.ACCENT_PRIMARY);
        cy += 2;

        Renderer.drawCentered(g, cy, w, bestCount + " scans", Theme.ACCENT_CYAN);
        cy += 4;

        // Mini bar chart of all months
        int barMaxWidth = Math.min(w - 20, 40);
        long maxCount = byMonth.values().stream().mapToLong(Long::longValue).max().orElse(1);
        int startX = (w - barMaxWidth - 12) / 2;

        for (Month month : Month.values()) {
            if (cy >= h - 3) break;
            long count = byMonth.getOrDefault(month, 0L);

            g.setForegroundColor(Theme.TEXT_MUTED);
            g.putString(startX, cy, month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH));

            int barLen = maxCount > 0 ? (int) ((count * barMaxWidth) / maxCount) : 0;
            g.setForegroundColor(month == bestMonth ? Theme.ACCENT_PRIMARY : Theme.ACCENT_INFO);
            g.putString(startX + 5, cy, Theme.BLOCK_FULL.repeat(Math.max(0, barLen)));

            g.setForegroundColor(Theme.TEXT_SECONDARY);
            g.putString(startX + 5 + barLen + 1, cy, String.valueOf(count));

            cy++;
        }
    }

    private static void renderLanguageOfTheYear(TextGraphics g, int w, int h, List<HistoryEntry> history) {
        int cy = h / 2 - 4;

        // Find most common dominant language
        Map<String, Long> langCounts = history.stream()
                .collect(Collectors.groupingBy(
                        HistoryEntry::dominantLanguage,
                        Collectors.counting()));

        String topLang = langCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Unknown");

        Renderer.drawCentered(g, cy, w, "Language of the Year", Theme.TEXT_SECONDARY);
        cy += 3;

        Renderer.drawCentered(g, cy, w, topLang, Theme.ACCENT_PRIMARY);
        cy += 3;

        Renderer.drawCentered(g, cy, w,
                "appeared in " + langCounts.getOrDefault(topLang, 0L) + " of your scans",
                Theme.TEXT_SECONDARY);
        cy += 3;

        // Top 5 languages
        List<Map.Entry<String, Long>> sorted = langCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .toList();

        for (int i = 0; i < sorted.size() && cy < h - 3; i++) {
            Map.Entry<String, Long> entry = sorted.get(i);
            g.setForegroundColor(Theme.langColor(i));
            Renderer.drawCentered(g, cy, w,
                    (i + 1) + ". " + entry.getKey() + " (" + entry.getValue() + " scans)",
                    Theme.langColor(i));
            cy++;
        }
    }

    private static void renderAchievementsPage(TextGraphics g, int w, int h, StorageManager storage) {
        int cy = h / 2 - 4;

        var unlocked = storage.getUnlockedAchievements();

        Renderer.drawCentered(g, cy, w, "Achievements Unlocked", Theme.TEXT_SECONDARY);
        cy += 3;

        Renderer.drawCentered(g, cy, w, String.valueOf(unlocked.size()), Theme.ACCENT_SUCCESS);
        cy += 2;

        if (!unlocked.isEmpty()) {
            Renderer.drawCentered(g, cy, w, "Latest:", Theme.TEXT_MUTED);
            cy += 2;

            for (int i = 0; i < Math.min(5, unlocked.size()) && cy < h - 3; i++) {
                var achievement = unlocked.get(i);
                Renderer.drawCentered(g, cy, w,
                        achievement.icon() + " " + achievement.displayName(),
                        Theme.ACCENT_WARNING);
                cy++;
            }
        } else {
            Renderer.drawCentered(g, cy, w, "Start scanning to unlock achievements!", Theme.TEXT_MUTED);
        }
    }

    private static void renderGrowthPage(TextGraphics g, int w, int h, List<HistoryEntry> history) {
        int cy = h / 2 - 5;

        Renderer.drawCentered(g, cy, w, "Growth", Theme.TEXT_SECONDARY);
        cy += 3;

        if (history.size() >= 2) {
            HistoryEntry latest = history.get(0);
            HistoryEntry earliest = history.get(history.size() - 1);

            long growth = latest.totalCodeLines() - earliest.totalCodeLines();
            double pct = earliest.totalCodeLines() > 0
                    ? ((double) growth / earliest.totalCodeLines()) * 100 : 0;

            Renderer.drawCentered(g, cy, w,
                    String.format("%+,d lines", growth),
                    growth >= 0 ? Theme.ACCENT_SUCCESS : Theme.ACCENT_DANGER);
            cy += 2;

            Renderer.drawCentered(g, cy, w,
                    String.format("%+.1f%% change", pct), Theme.ACCENT_CYAN);
            cy += 4;

            // Sparkline
            long[] data = history.stream()
                    .mapToLong(HistoryEntry::totalCodeLines)
                    .toArray();
            long[] reversed = new long[data.length];
            for (int i = 0; i < data.length; i++) reversed[i] = data[data.length - 1 - i];

            int sparkWidth = Math.min(w - 10, 50);
            int sparkX = (w - sparkWidth) / 2;
            Renderer.drawSparkline(g, sparkX, cy, sparkWidth, reversed, Theme.ACCENT_PRIMARY);
        } else {
            Renderer.drawCentered(g, cy, w, "Need more scans to show growth", Theme.TEXT_MUTED);
        }
    }

    private static void renderSharePage(TextGraphics g, int w, int h, List<HistoryEntry> history) {
        int cy = h / 2 - 6;

        Renderer.drawCentered(g, cy, w, "Thanks for coding!", Theme.ACCENT_PRIMARY);
        cy += 3;

        Renderer.drawCentered(g, cy, w, "Share your CodeMeter Wrapped", Theme.TEXT_SECONDARY);
        cy += 3;

        // Share card preview
        int cardWidth = Math.min(40, w - 10);
        int cardX = (w - cardWidth) / 2;

        Renderer.drawCard(g, cardX, cy, cardWidth, 8, "CodeMeter Wrapped");
        cy++;

        long maxLoc = history.stream().mapToLong(HistoryEntry::totalCodeLines).max().orElse(0);

        g.setBackgroundColor(Theme.BG_CARD);
        Renderer.drawCentered(g, cy + 1, w, PhysicalCalculator.formatNumber(maxLoc) + " lines", Theme.ACCENT_PRIMARY);
        Renderer.drawCentered(g, cy + 2, w, countUniqueProjects(history) + " projects", Theme.TEXT_SECONDARY);
        Renderer.drawCentered(g, cy + 3, w, history.size() + " scans", Theme.TEXT_SECONDARY);
        Renderer.drawCentered(g, cy + 5, w, "codemeter.dev", Theme.TEXT_MUTED);
        g.setBackgroundColor(Theme.BG_PRIMARY);

        cy += 10;
        Renderer.drawCentered(g, cy, w, "Use 'codemeter export' to generate share images", Theme.TEXT_MUTED);
    }

    private static void renderPageIndicator(TextGraphics g, int w, int h, int currentPage) {
        StringBuilder indicator = new StringBuilder();
        for (int i = 0; i < TOTAL_PAGES; i++) {
            indicator.append(i == currentPage ? Theme.BULLET : Theme.BULLET_EMPTY);
            if (i < TOTAL_PAGES - 1) indicator.append(" ");
        }
        Renderer.drawCentered(g, h - 3, w, indicator.toString(), Theme.TEXT_MUTED);
    }

    private static long countUniqueProjects(List<HistoryEntry> history) {
        return history.stream()
                .map(HistoryEntry::projectPath)
                .distinct()
                .count();
    }
}
