package dev.codemeter.tui.screens;

import com.googlecode.lanterna.graphics.TextGraphics;
import dev.codemeter.core.model.Achievement;
import dev.codemeter.core.model.AchievementType;
import dev.codemeter.tui.Renderer;
import dev.codemeter.tui.Theme;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Achievements panel showing gamified milestones.
 */
public final class AchievementsPanel {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());

    private AchievementsPanel() {}

    public static void render(TextGraphics g, int x, int y, int width, int height,
                              Map<String, Achievement> achievements, int scrollOffset) {
        int row = y + 1;

        g.setForegroundColor(Theme.ACCENT_PRIMARY);
        g.putString(x + 2, row, "🏆 ACHIEVEMENTS");
        row += 2;

        if (achievements == null || achievements.isEmpty()) {
            g.setForegroundColor(Theme.TEXT_MUTED);
            g.putString(x + 4, row, "No achievements tracked.");
            return;
        }

        // Count unlocked
        long unlocked = achievements.values().stream().filter(Achievement::unlocked).count();
        long total = achievements.size();

        g.setForegroundColor(Theme.ACCENT_SUCCESS);
        g.putString(x + 3, row, String.format("%d / %d unlocked", unlocked, total));
        row++;

        // Overall progress bar
        double overallPct = total > 0 ? (unlocked * 100.0 / total) : 0;
        Renderer.drawProgressBar(g, x + 3, row, Math.min(width - 8, 40), overallPct, Theme.ACCENT_PRIMARY);
        g.setForegroundColor(Theme.TEXT_MUTED);
        g.putString(x + 3 + Math.min(width - 8, 40) + 2, row, String.format("%.0f%%", overallPct));
        row += 2;

        // Separator
        Renderer.drawSeparator(g, x + 2, row, width - 4, Theme.BORDER);
        row += 2;

        // Achievement list
        int cardWidth = Math.min(width - 6, 60);

        for (AchievementType type : AchievementType.values()) {
            int displayRow = row - scrollOffset * 4;
            if (displayRow >= y + height - 1) break;
            if (displayRow < y + 2) {
                row += 4;
                continue;
            }

            Achievement achievement = achievements.get(type.name());
            if (achievement == null) achievement = Achievement.locked(type);

            // Icon and name
            if (achievement.unlocked()) {
                g.setForegroundColor(Theme.ACCENT_SUCCESS);
                g.putString(x + 3, displayRow, achievement.icon() + " " + Theme.CHECK);
            } else {
                g.setForegroundColor(Theme.TEXT_MUTED);
                g.putString(x + 3, displayRow, achievement.icon() + " " + Theme.BULLET_EMPTY);
            }

            g.setForegroundColor(achievement.unlocked() ? Theme.TEXT_PRIMARY : Theme.TEXT_MUTED);
            g.putString(x + 8, displayRow, achievement.displayName());

            // Unlock date or progress
            if (achievement.unlocked() && achievement.unlockDate() != null) {
                g.setForegroundColor(Theme.ACCENT_SUCCESS);
                g.putString(x + cardWidth - 12, displayRow, DATE_FMT.format(achievement.unlockDate()));
            }

            // Description
            g.setForegroundColor(Theme.TEXT_MUTED);
            g.putString(x + 8, displayRow + 1, achievement.description());

            // Progress bar
            if (!achievement.unlocked()) {
                double pct = achievement.progressPercent();
                int barWidth = Math.min(20, cardWidth - 20);
                Renderer.drawProgressBar(g, x + 8, displayRow + 2, barWidth, pct, Theme.PROGRESS_FILL);
                g.setForegroundColor(Theme.TEXT_MUTED);
                g.putString(x + 8 + barWidth + 1, displayRow + 2,
                        String.format("%d/%d", achievement.currentProgress(), type.threshold()));
            }

            row += 4;
        }
    }
}
