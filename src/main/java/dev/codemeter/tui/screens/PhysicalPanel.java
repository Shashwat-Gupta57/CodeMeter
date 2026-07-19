package dev.codemeter.tui.screens;

import com.googlecode.lanterna.graphics.TextGraphics;
import dev.codemeter.core.metrics.PhysicalCalculator;
import dev.codemeter.core.model.PhysicalMetrics;
import dev.codemeter.core.model.Settings;
import dev.codemeter.tui.Renderer;
import dev.codemeter.tui.Theme;

/**
 * Physical metrics panel showing real-world comparisons.
 */
public final class PhysicalPanel {

    private PhysicalPanel() {}

    public static void render(TextGraphics g, int x, int y, int width, int height,
                              PhysicalMetrics pm, Settings settings, int scrollOffset) {
        if (pm == null) {
            Renderer.drawCentered(g, y + height / 2, x + width, "No physical metrics available", Theme.TEXT_MUTED);
            return;
        }

        boolean metric = settings == null || settings.getMeasurement() == Settings.MeasurementSystem.METRIC;
        int row = y + 1 - scrollOffset;
        int cardWidth = Math.min(width - 4, 60);
        int cardX = x + 2;

        // Title
        if (row >= y && row < y + height) {
            g.setForegroundColor(Theme.ACCENT_PRIMARY);
            g.putString(cardX, row, "📐 PHYSICAL METRICS");
        }
        row += 2;

        // ── Character Dimensions ──
        row = renderSection(g, cardX, row, cardWidth, y, y + height,
                "CHARACTER DIMENSIONS", Theme.ACCENT_CYAN, new String[][]{
                        {"Total Character Length", metric ?
                                PhysicalCalculator.formatMetric(pm.characterLengthKm(), "km") :
                                PhysicalCalculator.formatMetric(pm.characterLengthMiles(), "mi")},
                        {"Horizontal Length", metric ?
                                PhysicalCalculator.formatMetric(pm.horizontalLengthKm(), "km") :
                                PhysicalCalculator.formatMetric(pm.horizontalLengthMiles(), "mi")},
                });
        row++;

        // ── Paper Stack ──
        row = renderSection(g, cardX, row, cardWidth, y, y + height,
                "PAPER STACK", Theme.ACCENT_INFO, new String[][]{
                        {"Stack Height", metric ?
                                PhysicalCalculator.formatMetric(pm.verticalStackMeters(), "m") :
                                PhysicalCalculator.formatMetric(pm.verticalStackFeet(), "ft")},
                        {"Total Pages", PhysicalCalculator.formatNumber(pm.totalPages())},
                });
        row++;

        // ── Sports Comparisons ──
        row = renderSection(g, cardX, row, cardWidth, y, y + height,
                "SPORTS COMPARISONS", Theme.ACCENT_SUCCESS, new String[][]{
                        {"⚽ Football Fields", String.format("%.1f", pm.footballFields())},
                        {"🏏 Cricket Grounds", String.format("%.1f", pm.cricketGrounds())},
                        {"🏀 Basketball Courts", String.format("%.1f", pm.basketballCourts())},
                        {"🎾 Tennis Courts", String.format("%.1f", pm.tennisCourts())},
                        {"🏊 Olympic Pools", String.format("%.1f", pm.olympicSwimmingPools())},
                });
        row++;

        // ── Landmark Comparisons ──
        row = renderSection(g, cardX, row, cardWidth, y, y + height,
                "LANDMARK COMPARISONS", Theme.ACCENT_WARNING, new String[][]{
                        {"🏗️ Burj Khalifas", String.format("%.3f", pm.burjKhalifas())},
                        {"🏢 Empire State Buildings", String.format("%.3f", pm.empireStateBuildings())},
                        {"🗼 Eiffel Towers", String.format("%.3f", pm.eiffelTowers())},
                        {"🏔️ Mount Everests", String.format("%.4f", pm.mountEverests())},
                });
        row++;

        // ── Distance Comparisons ──
        row = renderSection(g, cardX, row, cardWidth, y, y + height,
                "DISTANCE COMPARISONS", Theme.ACCENT_DANGER, new String[][]{
                        {"🌍 Earth Circumference", String.format("%.6f%%", pm.earthCircumferencePercent())},
                        {"🌙 Moon Distance", String.format("%.8f%%", pm.moonDistancePercent())},
                        {"🏅 Marathons", String.format("%.2f", pm.marathons())},
                        {"🌳 Central Park Loops", String.format("%.2f", pm.centralParkLoops())},
                });
        row++;

        // ── Paper & Printing ──
        row = renderSection(g, cardX, row, cardWidth, y, y + height,
                "PAPER & PRINTING", Theme.ACCENT_SECONDARY, new String[][]{
                        {"🌲 Trees Required", String.format("%.2f", pm.treesRequired())},
                        {"📏 Shelf Width", metric ?
                                PhysicalCalculator.formatMetric(pm.shelfWidthMeters(), "m") :
                                PhysicalCalculator.formatMetric(pm.shelfWidthFeet(), "ft")},
                        {"⚖️ Estimated Weight", metric ?
                                PhysicalCalculator.formatMetric(pm.estimatedWeightKg(), "kg") :
                                PhysicalCalculator.formatMetric(pm.estimatedWeightLbs(), "lbs")},
                        {"🖨️ Printer Trays", String.format("%.1f", pm.printerTrays())},
                        {"📚 Bookshelves", String.format("%.1f", pm.bookshelves())},
                });

        // Scroll hint
        if (row > y + height) {
            g.setForegroundColor(Theme.TEXT_MUTED);
            g.putString(x + width - 15, y + height - 1, "↓ scroll down");
        }
    }

    private static int renderSection(TextGraphics g, int x, int startRow, int width,
                                     int minY, int maxY, String title,
                                     com.googlecode.lanterna.TextColor titleColor,
                                     String[][] items) {
        int row = startRow;

        if (row >= minY && row < maxY) {
            g.setForegroundColor(titleColor);
            g.putString(x, row, Theme.sectionTitle(title));
        }
        row += 2;

        for (String[] item : items) {
            if (row >= minY && row < maxY) {
                Renderer.drawLabelValue(g, x + 2, row, width - 4, item[0], item[1]);
            }
            row++;
        }

        return row + 1;
    }
}
