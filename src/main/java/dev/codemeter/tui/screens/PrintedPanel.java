package dev.codemeter.tui.screens;

import com.googlecode.lanterna.graphics.TextGraphics;
import dev.codemeter.core.model.PrintedMetrics;
import dev.codemeter.core.model.Settings;
import dev.codemeter.tui.Renderer;
import dev.codemeter.tui.Theme;

/**
 * Printed metrics panel with live-updating configurable settings.
 */
public final class PrintedPanel {

    private PrintedPanel() {}

    public static void render(TextGraphics g, int x, int y, int width, int height,
                              PrintedMetrics pm, Settings settings, int scrollOffset) {
        if (pm == null) {
            Renderer.drawCentered(g, y + height / 2, x + width, "No print metrics available", Theme.TEXT_MUTED);
            return;
        }

        int row = y + 1 - scrollOffset;
        int leftWidth = Math.min(35, width / 2 - 2);
        int rightX = x + leftWidth + 4;
        int rightWidth = width - leftWidth - 6;

        // Title
        if (row >= y && row < y + height) {
            g.setForegroundColor(Theme.ACCENT_PRIMARY);
            g.putString(x + 2, row, "🖨 PRINT ANALYSIS");
        }
        row += 2;

        // ── Left: Current Config ──
        if (row >= y && row < y + height) {
            g.setForegroundColor(Theme.ACCENT_CYAN);
            g.putString(x + 2, row, Theme.sectionTitle("CURRENT CONFIG"));
        }
        row += 2;

        String[][] config = {
                {"Paper Size", pm.paperSize()},
                {"Font", pm.fontName()},
                {"Font Size", pm.fontSize() + "pt"},
                {"Line Spacing", String.format("%.2f", pm.lineSpacing())},
                {"Margins", pm.marginType()},
                {"Paper Thickness", String.format("%.1fmm", pm.paperThicknessMm())},
                {"Ink Type", pm.inkType()},
        };

        for (String[] item : config) {
            if (row >= y && row < y + height) {
                Renderer.drawLabelValue(g, x + 3, row, leftWidth - 2, item[0], item[1]);
            }
            row++;
        }
        row += 2;

        // ── Calculated Layout ──
        if (row >= y && row < y + height) {
            g.setForegroundColor(Theme.ACCENT_INFO);
            g.putString(x + 2, row, Theme.sectionTitle("PAGE LAYOUT"));
        }
        row += 2;

        String[][] layout = {
                {"Lines Per Page", String.valueOf(pm.linesPerPage())},
                {"Chars Per Line", String.valueOf(pm.charsPerLine())},
                {"Total Pages", String.format("%,d", pm.totalPages())},
        };

        for (String[] item : layout) {
            if (row >= y && row < y + height) {
                Renderer.drawLabelValue(g, x + 3, row, leftWidth - 2, item[0], item[1]);
            }
            row++;
        }
        row += 2;

        // ── Physical Output ──
        if (row >= y && row < y + height) {
            g.setForegroundColor(Theme.ACCENT_SUCCESS);
            g.putString(x + 2, row, Theme.sectionTitle("PHYSICAL OUTPUT"));
        }
        row += 2;

        boolean metric = settings == null || settings.getMeasurement() == Settings.MeasurementSystem.METRIC;
        String[][] physical = {
                {"Stack Height", metric ?
                        String.format("%.1f cm", pm.stackHeightCm()) :
                        String.format("%.1f in", pm.stackHeightInches())},
                {"Total Weight", metric ?
                        String.format("%.2f kg", pm.totalWeightKg()) :
                        String.format("%.2f lbs", pm.totalWeightLbs())},
        };

        for (String[] item : physical) {
            if (row >= y && row < y + height) {
                Renderer.drawLabelValue(g, x + 3, row, leftWidth - 2, item[0], item[1]);
            }
            row++;
        }
        row += 2;

        // ── Ink Usage ──
        if (row >= y && row < y + height) {
            g.setForegroundColor(Theme.ACCENT_WARNING);
            g.putString(x + 2, row, Theme.sectionTitle("INK & COST"));
        }
        row += 2;

        String[][] ink = {
                {"Ink Usage", String.format("%.1f ml", pm.inkMl())},
                {"Ink Cartridges", String.format("%.1f", pm.inkCartridges())},
                {"Paper Cost", String.format("$%.2f", pm.paperCostUsd())},
                {"Ink Cost", String.format("$%.2f", pm.inkCostUsd())},
                {"Total Cost", String.format("$%.2f", pm.totalCostUsd())},
        };

        for (String[] item : ink) {
            if (row >= y && row < y + height) {
                Renderer.drawLabelValue(g, x + 3, row, leftWidth - 2, item[0], item[1]);
            }
            row++;
        }
        row += 2;

        // ── Time Estimates ──
        if (row >= y && row < y + height) {
            g.setForegroundColor(Theme.ACCENT_DANGER);
            g.putString(x + 2, row, Theme.sectionTitle("TIME ESTIMATES"));
        }
        row += 2;

        String[][] time = {
                {"Print Time", formatDuration(pm.printTimeMinutes())},
                {"Reading Time", formatHours(pm.readingTimeHours())},
                {"Typing Time", formatHours(pm.typingTimeHours())},
        };

        for (String[] item : time) {
            if (row >= y && row < y + height) {
                Renderer.drawLabelValue(g, x + 3, row, leftWidth - 2, item[0], item[1]);
            }
            row++;
        }
    }

    private static String formatDuration(double minutes) {
        if (minutes < 1) return String.format("%.0f seconds", minutes * 60);
        if (minutes < 60) return String.format("%.0f minutes", minutes);
        double hours = minutes / 60;
        if (hours < 24) return String.format("%.1f hours", hours);
        double days = hours / 24;
        return String.format("%.1f days", days);
    }

    private static String formatHours(double hours) {
        if (hours < 1) return String.format("%.0f minutes", hours * 60);
        if (hours < 24) return String.format("%.1f hours", hours);
        double days = hours / 24;
        if (days < 30) return String.format("%.1f days", days);
        double months = days / 30;
        if (months < 12) return String.format("%.1f months", months);
        double years = days / 365;
        return String.format("%.1f years", years);
    }
}
