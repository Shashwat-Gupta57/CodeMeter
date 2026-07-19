package dev.codemeter.core.scanner;

import dev.codemeter.core.model.GitStats;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class GitHelper {

    public static GitStats getGitStats(Path directory) {
        if (!Files.exists(directory.resolve(".git"))) {
            return null;
        }

        try {
            // Get first commit date
            String firstCommit = runGitCommand(directory, "git", "log", "--reverse", "--format=%cd", "--date=short");
            if (firstCommit == null || firstCommit.isEmpty()) return null;
            firstCommit = firstCommit.split("\n")[0].trim();

            // Get last commit date
            String lastCommit = runGitCommand(directory, "git", "log", "-1", "--format=%cd", "--date=short");
            if (lastCommit == null || lastCommit.isEmpty()) return null;
            lastCommit = lastCommit.trim();

            // Get total commit count
            String commitCountStr = runGitCommand(directory, "git", "rev-list", "--count", "HEAD");
            if (commitCountStr == null || commitCountStr.isEmpty()) return null;
            long commitCount = Long.parseLong(commitCountStr.trim());

            LocalDate firstDate = LocalDate.parse(firstCommit, DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate lastDate = LocalDate.parse(lastCommit, DateTimeFormatter.ISO_LOCAL_DATE);
            long ageInDays = ChronoUnit.DAYS.between(firstDate, LocalDate.now());
            if (ageInDays < 1) ageInDays = 1;

            return new GitStats(firstCommit, lastCommit, ageInDays, commitCount);
        } catch (Exception e) {
            return null; // Graceful fallback if git isn't installed or command fails
        }
    }

    private static String runGitCommand(Path dir, String... command) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(dir.toFile());
        pb.redirectErrorStream(true);
        Process p = pb.start();
        
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        
        if (p.waitFor(10, TimeUnit.SECONDS) && p.exitValue() == 0) {
            return output.toString();
        }
        return null;
    }
}
