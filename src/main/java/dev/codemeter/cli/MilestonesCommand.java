package dev.codemeter.cli;

import dev.codemeter.core.model.Achievement;
import dev.codemeter.core.storage.StorageManager;
import picocli.CommandLine.Command;

import java.util.Collection;
import java.util.concurrent.Callable;

@Command(name = "milestones", description = "Display unlocked achievements and progress.")
public class MilestonesCommand implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
        StorageManager storage = new StorageManager();
        storage.load();

        Collection<Achievement> achievements = storage.getAchievements().values();

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        System.out.println("Achievements\n");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        for (Achievement a : achievements) {
            System.out.printf("%s %s\n\n", a.icon(), a.displayName());
            if (a.unlocked()) {
                System.out.println("Unlocked");
            } else {
                System.out.printf("%.0f%%\n", a.progressPercent());
                long remaining = a.type().threshold() - a.currentProgress();
                System.out.printf("(Requires %,d more %s)\n", remaining, getMetricName(a.type().name()));
            }
            System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        }

        return CodeMeterExceptionHandler.EXIT_SUCCESS;
    }

    private String getMetricName(String typeName) {
        if (typeName.contains("FILES")) return "files";
        if (typeName.contains("LINES")) return "lines";
        if (typeName.contains("CHARACTERS")) return "characters";
        if (typeName.contains("KM") || typeName.contains("MARATHON") || typeName.contains("PLANET")) return "km";
        if (typeName.contains("MOUNTAIN")) return "metres stack height";
        if (typeName.contains("CENTURY") || typeName.contains("FIVE_HUNDRED")) return "scans";
        if (typeName.contains("POLYGLOT") || typeName.contains("BABEL")) return "languages";
        return "units";
    }
}
