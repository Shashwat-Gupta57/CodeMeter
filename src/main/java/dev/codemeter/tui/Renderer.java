package dev.codemeter.tui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;

/**
 * Reusable rendering primitives for TUI components.
 * Provides card borders, progress bars, tables, and layout helpers.
 */
public final class Renderer {

    private Renderer() {}

    /**
     * Draw a rounded card border with optional title.
     */
    public static void drawCard(TextGraphics g, int x, int y, int width, int height, String title) {
        drawCard(g, x, y, width, height, title, Theme.BORDER, Theme.BG_CARD);
    }

    public static void drawCard(TextGraphics g, int x, int y, int width, int height,
                                String title, TextColor borderColor, TextColor bgColor) {
        g.setForegroundColor(borderColor);
        g.setBackgroundColor(bgColor);

        // Fill background
        for (int row = y; row < y + height; row++) {
            g.putString(x, row, " ".repeat(width));
        }

        // Top border
        g.setForegroundColor(borderColor);
        g.putString(x, y, Theme.BOX_TL + Theme.BOX_H.repeat(width - 2) + Theme.BOX_TR);

        // Bottom border
        g.putString(x, y + height - 1, Theme.BOX_BL + Theme.BOX_H.repeat(width - 2) + Theme.BOX_BR);

        // Side borders
        for (int row = y + 1; row < y + height - 1; row++) {
            g.putString(x, row, Theme.BOX_V);
            g.putString(x + width - 1, row, Theme.BOX_V);
        }

        // Title
        if (title != null && !title.isEmpty()) {
            int titleX = x + 2;
            g.setForegroundColor(Theme.TEXT_PRIMARY);
            g.putString(titleX, y, " " + title + " ");
        }
    }

    /**
     * Draw a horizontal progress bar.
     */
    public static void drawProgressBar(TextGraphics g, int x, int y, int width,
                                       double percent, TextColor fillColor) {
        int filled = (int) (percent / 100.0 * width);
        int empty = width - filled;

        g.setForegroundColor(fillColor);
        g.putString(x, y, Theme.BLOCK_FULL.repeat(Math.max(0, filled)));

        g.setForegroundColor(Theme.PROGRESS_BG);
        g.putString(x + filled, y, Theme.BLOCK_LIGHT.repeat(Math.max(0, empty)));
    }

    /**
     * Draw a small inline progress bar with percentage.
     */
    public static void drawInlineProgress(TextGraphics g, int x, int y, int barWidth,
                                          double percent, TextColor color) {
        drawProgressBar(g, x, y, barWidth, percent, color);
        g.setForegroundColor(Theme.TEXT_SECONDARY);
        g.putString(x + barWidth + 1, y, String.format("%5.1f%%", percent));
    }

    /**
     * Draw a labeled value (label on left, value on right).
     */
    public static void drawLabelValue(TextGraphics g, int x, int y, int width,
                                      String label, String value) {
        drawLabelValue(g, x, y, width, label, value, Theme.TEXT_SECONDARY, Theme.TEXT_PRIMARY);
    }

    public static void drawLabelValue(TextGraphics g, int x, int y, int width,
                                      String label, String value,
                                      TextColor labelColor, TextColor valueColor) {
        g.setForegroundColor(labelColor);
        g.putString(x, y, label);

        g.setForegroundColor(valueColor);
        int valueX = x + width - value.length();
        if (valueX > x + label.length()) {
            // Dots between label and value
            g.setForegroundColor(Theme.TEXT_MUTED);
            String dots = Theme.SEPARATOR.repeat(valueX - x - label.length() - 1);
            g.putString(x + label.length() + 1, y, dots);
        }
        g.setForegroundColor(valueColor);
        g.putString(Math.max(x + label.length() + 1, valueX), y, value);
    }

    /**
     * Draw centered text.
     */
    public static void drawCentered(TextGraphics g, int y, int screenWidth, String text, TextColor color) {
        int x = (screenWidth - text.length()) / 2;
        g.setForegroundColor(color);
        g.putString(Math.max(0, x), y, text);
    }

    /**
     * Draw a horizontal separator line.
     */
    public static void drawSeparator(TextGraphics g, int x, int y, int width, TextColor color) {
        g.setForegroundColor(color);
        g.putString(x, y, Theme.BOX_H.repeat(width));
    }

    /**
     * Draw a badge/pill with text.
     */
    public static void drawBadge(TextGraphics g, int x, int y, String text,
                                 TextColor bgColor, TextColor textColor) {
        g.setBackgroundColor(bgColor);
        g.setForegroundColor(textColor);
        g.putString(x, y, " " + text + " ");
        g.setBackgroundColor(Theme.BG_PRIMARY);
    }

    /**
     * Draw a mini bar chart.
     */
    public static void drawMiniBar(TextGraphics g, int x, int y, int maxWidth,
                                   double value, double maxValue, TextColor color) {
        int barLen = maxValue > 0 ? (int) ((value / maxValue) * maxWidth) : 0;
        g.setForegroundColor(color);
        g.putString(x, y, Theme.BLOCK_FULL.repeat(Math.max(1, barLen)));
    }

    /**
     * Draw a sparkline from data points.
     */
    public static void drawSparkline(TextGraphics g, int x, int y, int width,
                                     long[] data, TextColor color) {
        if (data == null || data.length == 0) return;

        String[] blocks = {"▁", "▂", "▃", "▄", "▅", "▆", "▇", "█"};
        long min = Long.MAX_VALUE, max = Long.MIN_VALUE;
        for (long v : data) {
            min = Math.min(min, v);
            max = Math.max(max, v);
        }

        g.setForegroundColor(color);
        double range = max - min;
        int step = Math.max(1, data.length / width);

        for (int i = 0; i < width && i * step < data.length; i++) {
            int idx = i * step;
            double normalized = range > 0 ? (double) (data[idx] - min) / range : 0.5;
            int blockIdx = (int) (normalized * (blocks.length - 1));
            g.putString(x + i, y, blocks[Math.max(0, Math.min(blockIdx, blocks.length - 1))]);
        }
    }

    /**
     * Clear a rectangular area.
     */
    public static void clearArea(TextGraphics g, int x, int y, int width, int height, TextColor bgColor) {
        g.setBackgroundColor(bgColor);
        for (int row = y; row < y + height; row++) {
            g.putString(x, row, " ".repeat(width));
        }
    }

    /**
     * Truncate text to fit width, adding ellipsis if needed.
     */
    public static String truncate(String text, int maxWidth) {
        if (text == null) return "";
        if (text.length() <= maxWidth) return text;
        if (maxWidth <= 3) return text.substring(0, maxWidth);
        return text.substring(0, maxWidth - 3) + "...";
    }

    /**
     * Pad or truncate text to exact width.
     */
    public static String fit(String text, int width) {
        if (text == null) text = "";
        if (text.length() > width) return truncate(text, width);
        return text + " ".repeat(width - text.length());
    }

    /**
     * Right-align text within width.
     */
    public static String rightAlign(String text, int width) {
        if (text == null) text = "";
        if (text.length() >= width) return text.substring(0, width);
        return " ".repeat(width - text.length()) + text;
    }
}
