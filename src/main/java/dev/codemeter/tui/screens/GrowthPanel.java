package dev.codemeter.tui.screens;

import com.googlecode.lanterna.graphics.TextGraphics;
import dev.codemeter.core.metrics.PhysicalCalculator;
import dev.codemeter.core.model.HistoryEntry;
import dev.codemeter.tui.Renderer;
import dev.codemeter.tui.Theme;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Growth panel showing code growth trends over time.
 */
public final class GrowthPanel {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault());

    private GrowthPanel() {}

    public static void render(TextGraphics g, int x, int y, int width, int height,
                              List<HistoryEntry> history, int scrollOffset) {
        int row = y + 1;

        g.setForegroundColor(Theme.ACCENT_PRIMARY);
        g.putString(x + 2, row, "📈 GROWTH ANALYSIS");
        row += 2;

        if (history == null || history.isEmpty()) {
            g.setForegroundColor(Theme.TEXT_MUTED);
            g.putString(x + 4, row, "No history data yet. Scan a project to start tracking growth.");
            return;
        }

        // Summary cards
        g.setForegroundColor(Theme.ACCENT_CYAN);
        g.putString(x + 2, row, Theme.sectionTitle("SUMMARY"));
        row += 2;

        HistoryEntry latest = history.get(0);
        HistoryEntry earliest = history.get(history.size() - 1);

        long locGrowth = latest.totalCodeLines() - earliest.totalCodeLines();
        long fileGrowth = latest.totalFiles() - earliest.totalFiles();
        double growthPct = earliest.totalCodeLines() > 0
                ? ((double) locGrowth / earliest.totalCodeLines()) * 100 : 0;

        int cardWidth = Math.min(width - 4, 55);
        Renderer.drawLabelValue(g, x + 3, row, cardWidth, "Total Scans", String.valueOf(history.size()));
        row++;
        Renderer.drawLabelValue(g, x + 3, row, cardWidth, "First Scan", DATE_FMT.format(earliest.timestamp()));
        row++;
        Renderer.drawLabelValue(g, x + 3, row, cardWidth, "Latest Scan", DATE_FMT.format(latest.timestamp()));
        row++;
        Renderer.drawLabelValue(g, x + 3, row, cardWidth, "LOC Growth",
                String.format("%+,d (%+.1f%%)", locGrowth, growthPct));
        row++;
        Renderer.drawLabelValue(g, x + 3, row, cardWidth, "File Growth",
                String.format("%+,d", fileGrowth));
        row += 3;

        // Sparkline graph
        if (history.size() >= 2 && row < y + height - 5) {
            g.setForegroundColor(Theme.ACCENT_INFO);
            g.putString(x + 2, row, Theme.sectionTitle("LOC TREND"));
            row += 2;

            long[] locData = history.stream()
                    .mapToLong(HistoryEntry::totalCodeLines)
                    .toArray();
            // Reverse for chronological order
            long[] reversed = new long[locData.length];
            for (int i = 0; i < locData.length; i++) {
                reversed[i] = locData[locData.length - 1 - i];
            }

            int sparkWidth = Math.min(width - 8, 50);
            Renderer.drawSparkline(g, x + 3, row, sparkWidth, reversed, Theme.ACCENT_SUCCESS);
            row++;

            // Min/Max labels
            long min = Long.MAX_VALUE, max = Long.MIN_VALUE;
            for (long v : reversed) { min = Math.min(min, v); max = Math.max(max, v); }
            g.setForegroundColor(Theme.TEXT_MUTED);
            g.putString(x + 3, row, "min: " + PhysicalCalculator.formatNumber(min));
            g.putString(x + 3 + sparkWidth - 20, row,
                    "max: " + PhysicalCalculator.formatNumber(max));
            row += 3;
        }

        // Recent changes table
        if (row < y + height - 3) {
            g.setForegroundColor(Theme.ACCENT_WARNING);
            g.putString(x + 2, row, Theme.sectionTitle("RECENT CHANGES"));
            row += 2;

            // Header
            g.setForegroundColor(Theme.TEXT_MUTED);
            g.putString(x + 3, row, Renderer.fit("Date", 18) +
                    Renderer.rightAlign("LOC", 10) +
                    Renderer.rightAlign("Change", 10) +
                    Renderer.rightAlign("Files", 8));
            row++;
            Renderer.drawSeparator(g, x + 3, row, Math.min(width - 6, 50), Theme.BORDER);
            row++;

            for (int i = 0; i < history.size() && row < y + height - 1; i++) {
                int displayRow = i - scrollOffset;
                if (displayRow < 0) continue;

                HistoryEntry entry = history.get(i);
                String date = DATE_FMT.format(entry.timestamp());

                String change = "";
                if (i < history.size() - 1) {
                    long diff = entry.totalCodeLines() - history.get(i + 1).totalCodeLines();
                    change = String.format("%+,d", diff);
                }

                g.setForegroundColor(Theme.TEXT_SECONDARY);
                g.putString(x + 3, row, Renderer.fit(date, 18) +
                        Renderer.rightAlign(PhysicalCalculator.formatNumber(entry.totalCodeLines()), 10) +
                        Renderer.rightAlign(change, 10) +
                        Renderer.rightAlign(PhysicalCalculator.formatNumber(entry.totalFiles()), 8));
                row++;
            }
        }
    }
}
