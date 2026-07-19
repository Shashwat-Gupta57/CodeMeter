package dev.codemeter.tui.screens;

import com.googlecode.lanterna.graphics.TextGraphics;
import dev.codemeter.core.metrics.ComparisonResult;
import dev.codemeter.core.metrics.PhysicalCalculator;
import dev.codemeter.core.model.HistoryEntry;
import dev.codemeter.core.model.ScanResult;
import dev.codemeter.tui.Renderer;
import dev.codemeter.tui.Theme;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Comparisons panel showing diff between current and previous scans.
 */
public final class ComparisonsPanel {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault());

    private ComparisonsPanel() {}

    public static void render(TextGraphics g, int x, int y, int width, int height,
                              ScanResult current, List<HistoryEntry> history, int scrollOffset) {
        int row = y + 1;

        g.setForegroundColor(Theme.ACCENT_PRIMARY);
        g.putString(x + 2, row, "⚖ COMPARISON");
        row += 2;

        if (current == null) {
            g.setForegroundColor(Theme.TEXT_MUTED);
            g.putString(x + 4, row, "No current scan data available.");
            return;
        }

        if (history == null || history.size() < 2) {
            g.setForegroundColor(Theme.TEXT_MUTED);
            g.putString(x + 4, row, "Need at least 2 scans to compare.");
            g.putString(x + 4, row + 1, "Scan this project again to see comparisons.");
            return;
        }

        // Compare current with most recent previous
        HistoryEntry previous = history.size() > 1 ? history.get(1) : history.get(0);

        long addedLoc = current.totalCodeLines() - previous.totalCodeLines();
        long addedChars = current.totalCharacters() - previous.totalCharacters();
        long addedFiles = current.totalFiles() - previous.totalFiles();
        double growthPct = previous.totalCodeLines() > 0
                ? ((double) addedLoc / previous.totalCodeLines()) * 100 : 0;

        int cardWidth = Math.min(width - 4, 60);

        // Current vs Previous header
        g.setForegroundColor(Theme.ACCENT_CYAN);
        g.putString(x + 2, row, Theme.sectionTitle("CURRENT vs PREVIOUS"));
        row += 2;

        // Side by side comparison
        int halfWidth = (cardWidth - 4) / 2;

        g.setForegroundColor(Theme.ACCENT_SUCCESS);
        g.putString(x + 3, row, Renderer.fit("Current", halfWidth));
        g.setForegroundColor(Theme.TEXT_MUTED);
        g.putString(x + 3 + halfWidth + 2, row, Renderer.fit("Previous", halfWidth));
        row++;
        Renderer.drawSeparator(g, x + 3, row, cardWidth - 2, Theme.BORDER);
        row++;

        // Code lines
        g.setForegroundColor(Theme.TEXT_PRIMARY);
        g.putString(x + 3, row, "Code Lines");
        g.setForegroundColor(Theme.ACCENT_SUCCESS);
        g.putString(x + 3 + 14, row, PhysicalCalculator.formatNumber(current.totalCodeLines()));
        g.setForegroundColor(Theme.TEXT_MUTED);
        g.putString(x + 3 + halfWidth + 2, row, PhysicalCalculator.formatNumber(previous.totalCodeLines()));
        row++;

        // Files
        g.setForegroundColor(Theme.TEXT_PRIMARY);
        g.putString(x + 3, row, "Files");
        g.setForegroundColor(Theme.ACCENT_SUCCESS);
        g.putString(x + 3 + 14, row, PhysicalCalculator.formatNumber(current.totalFiles()));
        g.setForegroundColor(Theme.TEXT_MUTED);
        g.putString(x + 3 + halfWidth + 2, row, PhysicalCalculator.formatNumber(previous.totalFiles()));
        row++;

        // Characters
        g.setForegroundColor(Theme.TEXT_PRIMARY);
        g.putString(x + 3, row, "Characters");
        g.setForegroundColor(Theme.ACCENT_SUCCESS);
        g.putString(x + 3 + 14, row, PhysicalCalculator.formatNumber(current.totalCharacters()));
        g.setForegroundColor(Theme.TEXT_MUTED);
        g.putString(x + 3 + halfWidth + 2, row, PhysicalCalculator.formatNumber(previous.totalCharacters()));
        row++;

        // Languages
        g.setForegroundColor(Theme.TEXT_PRIMARY);
        g.putString(x + 3, row, "Languages");
        g.setForegroundColor(Theme.ACCENT_SUCCESS);
        g.putString(x + 3 + 14, row, String.valueOf(current.languageCount()));
        g.setForegroundColor(Theme.TEXT_MUTED);
        g.putString(x + 3 + halfWidth + 2, row, String.valueOf(previous.languageCount()));
        row += 3;

        // Delta section
        g.setForegroundColor(Theme.ACCENT_WARNING);
        g.putString(x + 2, row, Theme.sectionTitle("CHANGES"));
        row += 2;

        // Net changes with color coding
        renderDelta(g, x + 3, row, cardWidth, "Net LOC Change", addedLoc);
        row++;
        renderDelta(g, x + 3, row, cardWidth, "Net Character Change", addedChars);
        row++;
        renderDelta(g, x + 3, row, cardWidth, "Net File Change", addedFiles);
        row++;

        g.setForegroundColor(Theme.TEXT_PRIMARY);
        g.putString(x + 3, row, "Growth");
        g.setForegroundColor(growthPct >= 0 ? Theme.ACCENT_SUCCESS : Theme.ACCENT_DANGER);
        g.putString(x + 3 + cardWidth - 15, row, String.format("%+.1f%%", growthPct));
        row += 2;

        // Growth visualization
        if (row < y + height - 3) {
            double absPct = Math.min(100, Math.abs(growthPct));
            int barWidth = Math.min(30, cardWidth - 10);
            Renderer.drawProgressBar(g, x + 3, row, barWidth, absPct,
                    growthPct >= 0 ? Theme.ACCENT_SUCCESS : Theme.ACCENT_DANGER);
        }
    }

    private static void renderDelta(TextGraphics g, int x, int y, int width, String label, long delta) {
        g.setForegroundColor(Theme.TEXT_PRIMARY);
        g.putString(x, y, label);

        g.setForegroundColor(delta > 0 ? Theme.ACCENT_SUCCESS :
                (delta < 0 ? Theme.ACCENT_DANGER : Theme.TEXT_MUTED));
        String value = (delta > 0 ? "+" : "") + PhysicalCalculator.formatNumber(delta);
        g.putString(x + width - value.length() - 2, y, value);
    }
}
