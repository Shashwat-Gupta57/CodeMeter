package dev.codemeter.core.storage;

import dev.codemeter.core.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class StorageManagerTest {

    @Test
    void newStorageManager_hasDefaults() {
        StorageManager sm = new StorageManager();

        assertThat(sm.getSettings()).isNotNull();
        assertThat(sm.getSettings().getTheme()).isEqualTo(Settings.ThemeMode.DARK);
        assertThat(sm.getProjects()).isEmpty();
        assertThat(sm.getGlobalHistory()).isEmpty();
        assertThat(sm.getRecentPaths()).isEmpty();
        assertThat(sm.getAchievements()).isNotEmpty(); // Pre-populated with all types
    }

    @Test
    void achievements_initializedForAllTypes() {
        StorageManager sm = new StorageManager();

        Map<String, Achievement> achievements = sm.getAchievements();
        for (AchievementType type : AchievementType.values()) {
            assertThat(achievements).containsKey(type.name());
            assertThat(achievements.get(type.name()).unlocked()).isFalse();
        }
    }

    @Test
    void addRecentPath_maintainsOrder() {
        StorageManager sm = new StorageManager();

        sm.addRecentPath("/path/a");
        sm.addRecentPath("/path/b");
        sm.addRecentPath("/path/c");

        List<String> recent = sm.getRecentPaths();
        assertThat(recent).hasSize(3);
        assertThat(recent.get(0)).isEqualTo("/path/c");
        assertThat(recent.get(1)).isEqualTo("/path/b");
        assertThat(recent.get(2)).isEqualTo("/path/a");
    }

    @Test
    void addRecentPath_removeDuplicates() {
        StorageManager sm = new StorageManager();

        sm.addRecentPath("/path/a");
        sm.addRecentPath("/path/b");
        sm.addRecentPath("/path/a"); // duplicate

        List<String> recent = sm.getRecentPaths();
        assertThat(recent).hasSize(2);
        assertThat(recent.get(0)).isEqualTo("/path/a");
    }

    @Test
    void addRecentPath_maxCapacity() {
        StorageManager sm = new StorageManager();

        for (int i = 0; i < 25; i++) {
            sm.addRecentPath("/path/" + i);
        }

        assertThat(sm.getRecentPaths()).hasSize(20);
    }

    @Test
    void checkAchievements_unlocksFirstScan() {
        StorageManager sm = new StorageManager();

        ScanResult result = new ScanResult(
                "/test", "test", System.currentTimeMillis(),
                50, 5, 5000, 500, 200, 5700,
                250000, 50000, 250000,
                "main.java", 100, 50.0, 40.0,
                List.of(new LanguageStats("Java", 50, 5000, 500, 200, 5700, 250000, 0)),
                Map.of(".java", 50L)
        );

        List<Achievement> newlyUnlocked = sm.checkAndUpdateAchievements(result);

        assertThat(newlyUnlocked).isNotEmpty();
        boolean hasFirstScan = newlyUnlocked.stream()
                .anyMatch(a -> a.type() == AchievementType.FIRST_SCAN);
        assertThat(hasFirstScan).isTrue();
    }

    @Test
    void projectCount_tracksCorrectly() {
        StorageManager sm = new StorageManager();
        assertThat(sm.getProjectCount()).isEqualTo(0);

        ScanResult result = new ScanResult(
                "/test", "test", System.currentTimeMillis(),
                50, 5, 5000, 500, 200, 5700,
                250000, 50000, 250000,
                "main.java", 100, 50.0, 40.0,
                List.of(), Map.of()
        );

        sm.addOrUpdateProject(Project.from(result));
        assertThat(sm.getProjectCount()).isEqualTo(1);
    }
}
