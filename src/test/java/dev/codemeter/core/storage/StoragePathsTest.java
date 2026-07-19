package dev.codemeter.core.storage;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class StoragePathsTest {

    @Test
    void globalDir_notNull() {
        Path dir = StoragePaths.globalDir();
        assertThat(dir).isNotNull();
        assertThat(dir.toString()).isNotEmpty();
    }

    @Test
    void globalDir_containsCodeMeter() {
        Path dir = StoragePaths.globalDir();
        String pathStr = dir.toString().toLowerCase();
        assertThat(pathStr).containsIgnoringCase("codemeter");
    }

    @Test
    void projectDir_appendsCodemeterFolder() {
        Path project = Path.of("/home/user/myproject");
        Path result = StoragePaths.projectDir(project);
        assertThat(result.getFileName().toString()).isEqualTo(".codemeter");
    }

    @Test
    void configFile_hasTomlExtension() {
        Path config = StoragePaths.configFile();
        assertThat(config.getFileName().toString()).isEqualTo("config.toml");
    }

    @Test
    void projectsFile_hasJsonExtension() {
        Path projects = StoragePaths.projectsFile();
        assertThat(projects.getFileName().toString()).isEqualTo("projects.json");
    }

    @Test
    void achievementsFile_hasJsonExtension() {
        Path achievements = StoragePaths.achievementsFile();
        assertThat(achievements.getFileName().toString()).isEqualTo("achievements.json");
    }

    @Test
    void projectHistory_locatedInCodemeterDir() {
        Path project = Path.of("/home/user/myproject");
        Path history = StoragePaths.projectHistory(project);
        assertThat(history.getParent().getFileName().toString()).isEqualTo(".codemeter");
        assertThat(history.getFileName().toString()).isEqualTo("history.json");
    }
}
