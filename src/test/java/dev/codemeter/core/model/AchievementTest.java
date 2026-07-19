package dev.codemeter.core.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class AchievementTest {

    @Test
    void locked_createsUnlockedFalse() {
        Achievement a = Achievement.locked(AchievementType.FIRST_SCAN);
        assertThat(a.unlocked()).isFalse();
        assertThat(a.currentProgress()).isEqualTo(0);
        assertThat(a.unlockDate()).isNull();
    }

    @Test
    void unlocked_createsUnlockedTrue() {
        Achievement a = Achievement.unlocked(AchievementType.FIRST_SCAN, 1);
        assertThat(a.unlocked()).isTrue();
        assertThat(a.unlockDate()).isNotNull();
    }

    @Test
    void progressPercent_calculatesCorrectly() {
        Achievement a = new Achievement(AchievementType.HUNDRED_FILES, false, 50, null);
        assertThat(a.progressPercent()).isCloseTo(50.0, within(0.01));
    }

    @Test
    void progressPercent_unlockedReturns100() {
        Achievement a = Achievement.unlocked(AchievementType.FIRST_SCAN, 1);
        assertThat(a.progressPercent()).isEqualTo(100.0);
    }

    @Test
    void withProgress_unlocksWhenThresholdReached() {
        Achievement a = Achievement.locked(AchievementType.HUNDRED_FILES);
        Achievement updated = a.withProgress(100);
        assertThat(updated.unlocked()).isTrue();
        assertThat(updated.unlockDate()).isNotNull();
    }

    @Test
    void withProgress_staysLockedBelowThreshold() {
        Achievement a = Achievement.locked(AchievementType.HUNDRED_FILES);
        Achievement updated = a.withProgress(50);
        assertThat(updated.unlocked()).isFalse();
        assertThat(updated.currentProgress()).isEqualTo(50);
    }

    @Test
    void withProgress_noChangeIfAlreadyUnlocked() {
        Achievement a = Achievement.unlocked(AchievementType.FIRST_SCAN, 1);
        Achievement updated = a.withProgress(100);
        assertThat(updated).isSameAs(a);
    }
}
