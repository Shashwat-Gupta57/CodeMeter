package dev.codemeter.tui;

import com.googlecode.lanterna.TextColor;

/**
 * Theme system for CodeMeter TUI.
 * Provides consistent color palette across all screens.
 */
public final class Theme {

    // -- Dark Theme Colors --
    public static final TextColor BG_PRIMARY = new TextColor.RGB(22, 22, 30);
    public static final TextColor BG_SECONDARY = new TextColor.RGB(30, 30, 42);
    public static final TextColor BG_CARD = new TextColor.RGB(38, 38, 54);
    public static final TextColor BG_HOVER = new TextColor.RGB(48, 48, 68);
    public static final TextColor BG_SELECTED = new TextColor.RGB(55, 48, 90);

    // Accent colors
    public static final TextColor ACCENT_PRIMARY = new TextColor.RGB(139, 92, 246);   // Purple
    public static final TextColor ACCENT_SECONDARY = new TextColor.RGB(99, 102, 241); // Indigo
    public static final TextColor ACCENT_SUCCESS = new TextColor.RGB(52, 211, 153);   // Green
    public static final TextColor ACCENT_WARNING = new TextColor.RGB(251, 191, 36);   // Amber
    public static final TextColor ACCENT_DANGER = new TextColor.RGB(248, 113, 113);   // Red
    public static final TextColor ACCENT_INFO = new TextColor.RGB(96, 165, 250);      // Blue
    public static final TextColor ACCENT_CYAN = new TextColor.RGB(34, 211, 238);      // Cyan

    // Text colors
    public static final TextColor TEXT_PRIMARY = new TextColor.RGB(237, 237, 245);
    public static final TextColor TEXT_SECONDARY = new TextColor.RGB(148, 148, 168);
    public static final TextColor TEXT_MUTED = new TextColor.RGB(100, 100, 120);
    public static final TextColor TEXT_BRIGHT = new TextColor.RGB(255, 255, 255);

    // Borders
    public static final TextColor BORDER = new TextColor.RGB(55, 55, 75);
    public static final TextColor BORDER_FOCUSED = new TextColor.RGB(139, 92, 246);

    // Sidebar
    public static final TextColor SIDEBAR_BG = new TextColor.RGB(25, 25, 36);
    public static final TextColor SIDEBAR_ACTIVE = new TextColor.RGB(139, 92, 246);

    // Progress bar
    public static final TextColor PROGRESS_BG = new TextColor.RGB(45, 45, 60);
    public static final TextColor PROGRESS_FILL = new TextColor.RGB(139, 92, 246);

    // Language colors (for charts)
    public static final TextColor[] LANG_COLORS = {
            new TextColor.RGB(139, 92, 246),   // Purple
            new TextColor.RGB(96, 165, 250),   // Blue
            new TextColor.RGB(52, 211, 153),   // Green
            new TextColor.RGB(251, 191, 36),   // Amber
            new TextColor.RGB(248, 113, 113),  // Red
            new TextColor.RGB(34, 211, 238),   // Cyan
            new TextColor.RGB(244, 114, 182),  // Pink
            new TextColor.RGB(168, 162, 158),  // Gray
            new TextColor.RGB(253, 186, 116),  // Orange
            new TextColor.RGB(167, 139, 250),  // Light purple
    };

    // Box drawing characters (Unicode)
    public static final String BOX_TL = "╭";
    public static final String BOX_TR = "╮";
    public static final String BOX_BL = "╰";
    public static final String BOX_BR = "╯";
    public static final String BOX_H = "─";
    public static final String BOX_V = "│";
    public static final String BOX_H_THICK = "━";
    public static final String BOX_V_THICK = "┃";

    // Decorative
    public static final String BULLET = "●";
    public static final String BULLET_EMPTY = "○";
    public static final String ARROW_RIGHT = "→";
    public static final String ARROW_LEFT = "←";
    public static final String ARROW_UP = "↑";
    public static final String ARROW_DOWN = "↓";
    public static final String CHECK = "✓";
    public static final String CROSS = "✗";
    public static final String STAR = "★";
    public static final String STAR_EMPTY = "☆";
    public static final String DIAMOND = "◆";
    public static final String TRIANGLE_RIGHT = "▶";
    public static final String TRIANGLE_DOWN = "▼";
    public static final String BLOCK_FULL = "█";
    public static final String BLOCK_LIGHT = "░";
    public static final String BLOCK_MED = "▒";
    public static final String BLOCK_DARK = "▓";
    public static final String PROGRESS_FULL = "━";
    public static final String PROGRESS_EMPTY = "╌";
    public static final String SEPARATOR = "·";

    /**
     * Get a language color by index (cycles through palette).
     */
    public static TextColor langColor(int index) {
        return LANG_COLORS[index % LANG_COLORS.length];
    }

    /**
     * Format a section title with decorative borders.
     */
    public static String sectionTitle(String title) {
        return BOX_H.repeat(2) + " " + title + " " + BOX_H.repeat(Math.max(1, 40 - title.length()));
    }

    /**
     * Generate a progress bar string.
     */
    public static String progressBar(double percent, int width) {
        int filled = (int) (percent / 100.0 * width);
        int empty = width - filled;
        return PROGRESS_FULL.repeat(Math.max(0, filled)) + PROGRESS_EMPTY.repeat(Math.max(0, empty));
    }

    /**
     * Format a key hint.
     */
    public static String keyHint(String key, String action) {
        return "[" + key + "] " + action;
    }

    private Theme() {}
}
