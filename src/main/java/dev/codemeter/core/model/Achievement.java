package dev.codemeter.core.model;

import java.time.Instant;

/**
 * Represents an unlocked or in-progress achievement.
 */
public record Achievement(
        AchievementType type,
        boolean unlocked,
        long currentProgress,
        Instant unlockDate
) {
    /**
     * Creates a new locked achievement with zero progress.
     */
    public static Achievement locked(AchievementType type) {
        return new Achievement(type, false, 0, null);
    }

    /**
     * Creates an unlocked achievement.
     */
    public static Achievement unlocked(AchievementType type, long progress) {
        return new Achievement(type, true, progress, Instant.now());
    }

    /**
     * Returns progress as a percentage (0-100).
     */
    public double progressPercent() {
        if (unlocked) return 100.0;
        if (type.threshold() == 0) return 0.0;
        return Math.min(100.0, (currentProgress * 100.0) / type.threshold());
    }

    /**
     * Returns the achievement with updated progress.
     */
    public Achievement withProgress(long newProgress) {
        if (unlocked) return this;
        if (newProgress >= type.threshold()) {
            return new Achievement(type, true, newProgress, Instant.now());
        }
        return new Achievement(type, false, newProgress, null);
    }

    public String icon() { return type.icon(); }
    public String displayName() { return type.displayName(); }
    public String description() { return type.description(); }
}
