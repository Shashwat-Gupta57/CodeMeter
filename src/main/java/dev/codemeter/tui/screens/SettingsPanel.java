package dev.codemeter.tui.screens;

import com.googlecode.lanterna.graphics.TextGraphics;
import dev.codemeter.core.model.Settings;
import dev.codemeter.tui.Renderer;
import dev.codemeter.tui.Theme;

/**
 * Settings panel for configuring CodeMeter from the TUI.
 */
public final class SettingsPanel {

    private SettingsPanel() {}

    public static void render(TextGraphics g, int x, int y, int width, int height,
                              Settings settings, int scrollOffset) {
        int row = y + 1;
        int cardWidth = Math.min(width - 4, 55);

        g.setForegroundColor(Theme.ACCENT_PRIMARY);
        g.putString(x + 2, row, "⚙ SETTINGS");
        row += 2;

        // ── Theme ──
        g.setForegroundColor(Theme.ACCENT_CYAN);
        g.putString(x + 2, row, Theme.sectionTitle("THEME"));
        row += 2;

        renderOption(g, x + 3, row, cardWidth, "Theme Mode",
                settings.getTheme().name().toLowerCase(),
                "dark / light / system");
        row++;
        renderOption(g, x + 3, row, cardWidth, "Animations",
                settings.isAnimationsEnabled() ? "enabled" : "disabled",
                "enable / disable");
        row += 3;

        // ── Measurement ──
        g.setForegroundColor(Theme.ACCENT_INFO);
        g.putString(x + 2, row, Theme.sectionTitle("MEASUREMENT"));
        row += 2;

        renderOption(g, x + 3, row, cardWidth, "System",
                settings.getMeasurement().name().toLowerCase(),
                "metric / imperial");
        row += 3;

        // ── Print Settings ──
        g.setForegroundColor(Theme.ACCENT_SUCCESS);
        g.putString(x + 2, row, Theme.sectionTitle("PRINT SETTINGS"));
        row += 2;

        renderOption(g, x + 3, row, cardWidth, "Paper Size",
                settings.getPaperSize().name(), "A4 / Letter / Legal / A3");
        row++;
        renderOption(g, x + 3, row, cardWidth, "Margins",
                settings.getMarginType().name().toLowerCase(), "normal / narrow / custom");
        row++;
        renderOption(g, x + 3, row, cardWidth, "Font",
                settings.getFontName(), "JetBrains Mono / Fira Code / ...");
        row++;
        renderOption(g, x + 3, row, cardWidth, "Font Size",
                settings.getFontSize() + "pt", "6-30pt");
        row++;
        renderOption(g, x + 3, row, cardWidth, "Line Spacing",
                String.format("%.2f", settings.getLineSpacing()), "");
        row++;
        renderOption(g, x + 3, row, cardWidth, "Ink Type",
                settings.getInkType().name().toLowerCase(), "laser / inkjet / draft");
        row++;
        renderOption(g, x + 3, row, cardWidth, "Paper Thickness",
                String.format("%.1fmm", settings.getPaperThicknessMm()), "");
        row += 3;

        // ── Features ──
        if (row < y + height - 4) {
            g.setForegroundColor(Theme.ACCENT_WARNING);
            g.putString(x + 2, row, Theme.sectionTitle("FEATURES"));
            row += 2;

            renderOption(g, x + 3, row, cardWidth, "Comparison Objects",
                    settings.isComparisonObjectsEnabled() ? "enabled" : "disabled",
                    "enable / disable");
            row++;
            renderOption(g, x + 3, row, cardWidth, "History Tracking",
                    settings.isHistoryEnabled() ? "enabled" : "disabled",
                    "enable / disable");
        }

        // Footer hint
        if (y + height - 2 > row) {
            g.setForegroundColor(Theme.TEXT_MUTED);
            g.putString(x + 3, y + height - 2,
                    "Settings are saved to " + dev.codemeter.core.storage.StoragePaths.configFile());
        }
    }

    private static void renderOption(TextGraphics g, int x, int y, int width,
                                     String label, String currentValue, String hint) {
        g.setForegroundColor(Theme.TEXT_SECONDARY);
        g.putString(x, y, label);

        g.setForegroundColor(Theme.ACCENT_PRIMARY);
        String value = currentValue;
        g.putString(x + width - value.length() - 2, y, value);
    }
}
