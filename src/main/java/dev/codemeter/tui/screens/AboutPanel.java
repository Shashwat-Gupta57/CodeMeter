package dev.codemeter.tui.screens;

import com.googlecode.lanterna.graphics.TextGraphics;
import dev.codemeter.CodeMeter;
import dev.codemeter.core.scanner.ScannerFactory;
import dev.codemeter.tui.Renderer;
import dev.codemeter.tui.Theme;

/**
 * About panel displaying application information.
 */
public final class AboutPanel {

    private AboutPanel() {}

    public static void render(TextGraphics g, int x, int y, int width, int height) {
        int row = y + 1;
        int contentWidth = Math.min(width - 4, 60);
        int cx = x + (width - contentWidth) / 2;

        // Logo
        g.setForegroundColor(Theme.ACCENT_PRIMARY);
        Renderer.drawCentered(g, row, x + width, "◆ CodeMeter", Theme.ACCENT_PRIMARY);
        row += 2;

        Renderer.drawCentered(g, row, x + width, CodeMeter.TAGLINE, Theme.TEXT_SECONDARY);
        row += 2;

        Renderer.drawSeparator(g, cx, row, contentWidth, Theme.BORDER);
        row += 2;

        // Info
        String[][] info = {
                {"Version", CodeMeter.VERSION},
                {"Language", "Java 21"},
                {"Build", "Gradle Kotlin DSL"},
                {"CLI Framework", "Picocli"},
                {"TUI Library", "Lanterna"},
                {"Scanner", ScannerFactory.availableScannerName()},
                {"License", "MIT"},
        };

        g.setForegroundColor(Theme.ACCENT_CYAN);
        g.putString(cx, row, Theme.sectionTitle("APPLICATION"));
        row += 2;

        for (String[] item : info) {
            if (row >= y + height - 2) break;
            Renderer.drawLabelValue(g, cx + 2, row, contentWidth - 4, item[0], item[1]);
            row++;
        }
        row += 2;

        // Description
        if (row < y + height - 8) {
            g.setForegroundColor(Theme.ACCENT_INFO);
            g.putString(cx, row, Theme.sectionTitle("WHAT IS CODEMETER?"));
            row += 2;

            String[] desc = {
                    "Traditional LOC counters tell you how much code exists.",
                    "CodeMeter tells you what that code looks like in the",
                    "real world.",
                    "",
                    "Kilometers of characters. Stack height if printed.",
                    "Football fields. Burj Khalifas. Mount Everests.",
                    "Printer paper weight. Shelf width. Ink usage.",
                    "Typing time. Reading time. Yearly growth.",
            };

            for (String line : desc) {
                if (row >= y + height - 4) break;
                g.setForegroundColor(Theme.TEXT_SECONDARY);
                g.putString(cx + 2, row, line);
                row++;
            }
        }

        row += 2;

        // Credits
        if (row < y + height - 3) {
            g.setForegroundColor(Theme.ACCENT_WARNING);
            g.putString(cx, row, Theme.sectionTitle("LINKS"));
            row += 2;

            g.setForegroundColor(Theme.TEXT_SECONDARY);
            g.putString(cx + 2, row, "GitHub: github.com/codemeter/codemeter");
            row++;
            g.putString(cx + 2, row, "Issues: github.com/codemeter/codemeter/issues");
        }
    }
}
