package dev.codemeter.tui.screens;

import com.googlecode.lanterna.graphics.TextGraphics;
import dev.codemeter.core.metrics.PhysicalCalculator;
import dev.codemeter.core.model.LanguageStats;
import dev.codemeter.core.model.ScanResult;
import dev.codemeter.tui.Renderer;
import dev.codemeter.tui.Theme;

import java.util.List;

/**
 * Overview panel showing project summary and language breakdown.
 */
public final class OverviewPanel {

    private OverviewPanel() {}

    public static void render(TextGraphics g, int x, int y, int width, int height, ScanResult result) {
        if (result == null) {
            Renderer.drawCentered(g, y + height / 2, x + width, "No scan data available", Theme.TEXT_MUTED);
            return;
        }

        int col1Width = Math.min(40, width / 2 - 2);
        int col2X = x + col1Width + 4;
        int col2Width = width - col1Width - 6;

        // Header
        g.setForegroundColor(Theme.ACCENT_PRIMARY);
        g.putString(x + 2, y + 1, "◈ " + result.projectName());
        g.setForegroundColor(Theme.TEXT_MUTED);
        g.putString(x + 2, y + 2, Renderer.truncate(result.projectPath(), col1Width - 2));

        // Separator
        Renderer.drawSeparator(g, x + 1, y + 3, width - 2, Theme.BORDER);

        // ── Left column: Code Stats ──
        int row = y + 5;

        g.setForegroundColor(Theme.ACCENT_CYAN);
        g.putString(x + 2, row, "CODE STATISTICS");
        row += 2;

        String[][] stats = {
                {"Files", PhysicalCalculator.formatNumber(result.totalFiles())},
                {"Directories", PhysicalCalculator.formatNumber(result.totalDirectories())},
                {"Languages", String.valueOf(result.languageCount())},
                {"Code Lines", PhysicalCalculator.formatNumber(result.totalCodeLines())},
                {"Comment Lines", PhysicalCalculator.formatNumber(result.totalCommentLines())},
                {"Blank Lines", PhysicalCalculator.formatNumber(result.totalBlankLines())},
                {"Total Lines", PhysicalCalculator.formatNumber(result.totalLines())},
                {"Characters", PhysicalCalculator.formatNumber(result.totalCharacters())},
                {"Words", PhysicalCalculator.formatNumber(result.totalWords())},
                {"Bytes", formatBytes(result.totalBytes())},
        };

        for (String[] stat : stats) {
            if (row >= y + height - 2) break;
            Renderer.drawLabelValue(g, x + 3, row, col1Width - 4, stat[0], stat[1]);
            row++;
        }

        row += 1;
        if (row < y + height - 4) {
            g.setForegroundColor(Theme.ACCENT_CYAN);
            g.putString(x + 2, row, "FILE METRICS");
            row += 2;

            if (!result.largestFile().isEmpty()) {
                Renderer.drawLabelValue(g, x + 3, row, col1Width - 4,
                        "Largest File", Renderer.truncate(result.largestFile(), 20));
                row++;
            }
            Renderer.drawLabelValue(g, x + 3, row, col1Width - 4,
                    "Avg File Size", String.format("%.0f lines", result.averageFileSize()));
            row++;
            Renderer.drawLabelValue(g, x + 3, row, col1Width - 4,
                    "Avg Line Length", String.format("%.0f chars", result.averageLineLength()));
        }

        // ── Right column: Language Breakdown ──
        if (col2Width < 20) return;

        row = y + 5;
        g.setForegroundColor(Theme.ACCENT_CYAN);
        g.putString(col2X, row, "LANGUAGE BREAKDOWN");
        row += 2;

        List<LanguageStats> sortedLangs = result.languages().stream()
                .sorted((a, b) -> Long.compare(b.codeLines(), a.codeLines()))
                .toList();

        int maxBarWidth = Math.min(20, col2Width - 30);
        long maxCode = sortedLangs.isEmpty() ? 1 : sortedLangs.get(0).codeLines();

        for (int i = 0; i < sortedLangs.size() && row < y + height - 2; i++) {
            LanguageStats lang = sortedLangs.get(i);
            double pct = lang.percentageOf(result.totalCodeLines());

            // Language name
            g.setForegroundColor(Theme.langColor(i));
            g.putString(col2X, row, Theme.BULLET);
            g.setForegroundColor(Theme.TEXT_PRIMARY);
            g.putString(col2X + 2, row, Renderer.fit(lang.language(), 16));

            // Code count
            g.setForegroundColor(Theme.TEXT_SECONDARY);
            String count = PhysicalCalculator.formatNumber(lang.codeLines());
            g.putString(col2X + 19, row, Renderer.rightAlign(count, 10));

            // Percentage
            g.setForegroundColor(Theme.TEXT_MUTED);
            g.putString(col2X + 30, row, String.format("%5.1f%%", pct));

            row++;

            // Mini bar
            if (row < y + height - 2 && maxBarWidth > 0) {
                Renderer.drawMiniBar(g, col2X + 2, row, maxBarWidth,
                        lang.codeLines(), maxCode, Theme.langColor(i));
                row += 2;
            }
        }
    }

    private static String formatBytes(long bytes) {
        if (bytes >= 1_073_741_824) return String.format("%.1f GB", bytes / 1_073_741_824.0);
        if (bytes >= 1_048_576) return String.format("%.1f MB", bytes / 1_048_576.0);
        if (bytes >= 1024) return String.format("%.1f KB", bytes / 1024.0);
        return bytes + " B";
    }
}
