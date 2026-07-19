package dev.codemeter.cli;

import dev.codemeter.core.model.Achievement;
import dev.codemeter.core.model.HistoryEntry;
import dev.codemeter.core.storage.StorageManager;
import picocli.CommandLine.Command;


import java.util.List;

@Command(
        name = "wrapped",
        description = "Celebrate your coding journey"
)
public class WrappedCommand implements Runnable {

    @Override
    public void run() {
        StorageManager storage = new StorageManager();
        storage.load();

        List<HistoryEntry> history = storage.getGlobalHistory();
        List<Achievement> achievements = storage.getUnlockedAchievements();
        
        WrappedPresenter.printWrapped(history, achievements);
    }
}
