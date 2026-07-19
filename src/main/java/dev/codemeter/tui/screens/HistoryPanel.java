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
 * History panel showing a timeline of all scans.
 */
public final class HistoryPanel {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault());

    private HistoryPanel() {}

    public static void render(TextGraphics g, int x, int y, int width, int height,
                              List<HistoryEntry> history, int scrollOffset) {
        int row = y + 1;

        g.setForegroundColor(Theme.ACCENT_PRIMARY);
        g.putString(x + 2, row, "📅 SCAN HISTORY");
        row += 2;

        if (history == null || history.isEmpty()) {
            g.setForegroundColor(Theme.TEXT_MUTED);
            g.putString(x + 4, row, "No scan history yet.");
            g.putString(x + 4, row + 1, "Scan a project to start recording history.");
            return;
        }

        // Summary
        g.setForegroundColor(Theme.TEXT_SECONDARY);
        g.putString(x + 3, row, "Total scans: " + history.size());
        row += 2;

        // Table header
        int colDate = 18;
        int colProject = Math.min(20, (width - 50) / 2);
        int colLoc = 12;
        int colFiles = 8;
        int colLang = 6;

        g.setForegroundColor(Theme.ACCENT_CYAN);
        g.putString(x + 3, row,
                Renderer.fit("Date", colDate) +
                Renderer.fit("Project", colProject) +
                Renderer.rightAlign("LOC", colLoc) +
                Renderer.rightAlign("Files", colFiles) +
                Renderer.rightAlign("Langs", colLang));
        row++;
        Renderer.drawSeparator(g, x + 3, row, Math.min(width - 6, colDate + colProject + colLoc + colFiles + colLang), Theme.BORDER);
        row++;

        // Entries
        for (int i = 0; i < history.size() && row < y + height - 1; i++) {
            int displayIdx = i - scrollOffset;
            if (displayIdx < 0) continue;

            HistoryEntry entry = history.get(i);

            // Alternate row colors
            if (i % 2 == 0) {
                g.setForegroundColor(Theme.TEXT_PRIMARY);
            } else {
                g.setForegroundColor(Theme.TEXT_SECONDARY);
            }

            String date = DATE_FMT.format(entry.timestamp());
            g.putString(x + 3, row,
                    Renderer.fit(date, colDate) +
                    Renderer.fit(Renderer.truncate(entry.projectName(), colProject - 1), colProject) +
                    Renderer.rightAlign(PhysicalCalculator.formatNumber(entry.totalCodeLines()), colLoc) +
                    Renderer.rightAlign(PhysicalCalculator.formatNumber(entry.totalFiles()), colFiles) +
                    Renderer.rightAlign(String.valueOf(entry.languageCount()), colLang));
            row++;
        }

        // Scroll indicator
        if (history.size() > height - 8) {
            g.setForegroundColor(Theme.TEXT_MUTED);
            g.putString(x + width - 15, y + height - 1, "↑↓ scroll");
        }
    }
}
