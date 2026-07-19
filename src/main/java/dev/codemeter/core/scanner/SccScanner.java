package dev.codemeter.core.scanner;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.codemeter.core.model.LanguageStats;
import dev.codemeter.core.model.ScanResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Scanner implementation using scc (Sloc Cloc and Code).
 * scc provides fast, accurate code counting with JSON output.
 */
public class SccScanner implements CodeScanner {

    private static final Gson GSON = new Gson();

    @Override
    public String name() {
        return "scc";
    }

    @Override
    public boolean isAvailable() {
        try {
            ProcessBuilder pb = new ProcessBuilder("scc", "--version");
            pb.redirectErrorStream(true);
            Process process = pb.start();
            boolean finished = process.waitFor(5, TimeUnit.SECONDS);
            if (finished) {
                return process.exitValue() == 0;
            }
            process.destroyForcibly();
            return false;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    @Override
    public ScanResult scan(Path directory, Consumer<Integer> progressCallback) throws ScanException {
        try {
            if (progressCallback != null) progressCallback.accept(10);

            ProcessBuilder pb = new ProcessBuilder(
                    "scc", "--format", "json", "--no-cocomo", directory.toAbsolutePath().toString()
            );
            pb.redirectErrorStream(true);

            Process process = pb.start();

            if (progressCallback != null) progressCallback.accept(30);

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            boolean finished = process.waitFor(300, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                throw new ScanException("scc timed out after 5 minutes");
            }

            if (progressCallback != null) progressCallback.accept(70);

            if (process.exitValue() != 0) {
                throw new ScanException("scc exited with code " + process.exitValue() + ": " + output);
            }

            ScanResult result = parseSccOutput(output.toString(), directory);

            if (progressCallback != null) progressCallback.accept(90);

            // Augment with file-system level stats
            result = augmentWithFileStats(result, directory);

            if (progressCallback != null) progressCallback.accept(100);

            return result;

        } catch (IOException e) {
            throw new ScanException("Failed to run scc: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ScanException("Scan interrupted", e);
        }
    }

    private ScanResult parseSccOutput(String json, Path directory) throws ScanException {
        try {
            JsonArray languages = GSON.fromJson(json.trim(), JsonArray.class);
            if (languages == null || languages.isEmpty()) {
                String projectName = directory.getFileName() != null ? directory.getFileName().toString() : directory.toString();
                return new ScanResult(directory.toAbsolutePath().toString(), projectName, System.currentTimeMillis(),
                        0, 0, 0, 0, 0, 0, 0, 0, 0, "", 0, 0.0, 0.0, new ArrayList<>(), new HashMap<>());
            }

            List<LanguageStats> langStats = new ArrayList<>();
            long totalFiles = 0, totalCode = 0, totalComments = 0, totalBlanks = 0;
            long totalBytes = 0;
            double totalComplexity = 0;

            for (JsonElement element : languages) {
                JsonObject lang = element.getAsJsonObject();
                String name = lang.get("Name").getAsString();
                long files = lang.has("Count") ? lang.get("Count").getAsLong() : 0;
                long code = lang.has("Code") ? lang.get("Code").getAsLong() : 0;
                long comments = lang.has("Comment") ? lang.get("Comment").getAsLong() : 0;
                long blanks = lang.has("Blank") ? lang.get("Blank").getAsLong() : 0;
                long bytes_ = lang.has("Bytes") ? lang.get("Bytes").getAsLong() : 0;
                double complexity = lang.has("Complexity") ? lang.get("Complexity").getAsDouble() : 0;
                long lines = code + comments + blanks;

                langStats.add(new LanguageStats(name, files, code, comments, blanks, lines, bytes_, complexity));

                totalFiles += files;
                totalCode += code;
                totalComments += comments;
                totalBlanks += blanks;
                totalBytes += bytes_;
                totalComplexity += complexity;
            }

            long totalLines = totalCode + totalComments + totalBlanks;
            String projectName = directory.getFileName() != null
                    ? directory.getFileName().toString()
                    : directory.toString();

            return new ScanResult(
                    directory.toAbsolutePath().toString(),
                    projectName,
                    System.currentTimeMillis(),
                    totalFiles,
                    0, // directories - will be augmented
                    totalCode,
                    totalComments,
                    totalBlanks,
                    totalLines,
                    0, // characters - will be augmented
                    0, // words - will be augmented
                    totalBytes,
                    "", // largest file - will be augmented
                    0,  // largest file lines
                    totalFiles > 0 ? (double) totalLines / totalFiles : 0,
                    0, // avg line length - will be augmented
                    langStats,
                    new HashMap<>()
            );

        } catch (Exception e) {
            if (e instanceof ScanException) throw (ScanException) e;
            throw new ScanException("Failed to parse scc output: " + e.getMessage(), e);
        }
    }

    private ScanResult augmentWithFileStats(ScanResult result, Path directory) {
        try {
            long[] dirCount = {0};
            long[] charCount = {0};
            long[] wordCount = {0};
            String[] largestFile = {""};
            long[] largestFileSize = {0};
            long[] totalLineLength = {0};
            long[] lineCount = {0};
            Map<String, Long> byExtension = new HashMap<>();

            Files.walkFileTree(directory, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    String dirName = dir.getFileName() != null ? dir.getFileName().toString() : "";
                    if (dir.equals(directory)) {
                        dirCount[0]++;
                        return FileVisitResult.CONTINUE;
                    }
                    if (dirName.startsWith(".") || dirName.equals("node_modules")
                            || dirName.equals("vendor") || dirName.equals("target")
                            || dirName.equals("build") || dirName.equals("dist")) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    dirCount[0]++;
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (!attrs.isRegularFile()) return FileVisitResult.CONTINUE;

                    String fileName = file.getFileName().toString();
                    int dotIndex = fileName.lastIndexOf('.');
                    if (dotIndex > 0) {
                        String ext = fileName.substring(dotIndex);
                        byExtension.merge(ext, 1L, Long::sum);
                    }

                    long size = attrs.size();
                    if (size > largestFileSize[0]) {
                        largestFileSize[0] = size;
                        largestFile[0] = directory.relativize(file).toString();
                    }

                    // Sample character/word counting (for performance, only process text files < 1MB)
                    if (size < 1_048_576 && isTextFile(fileName)) {
                        try {
                            String content = Files.readString(file);
                            charCount[0] += content.length();
                            wordCount[0] += content.split("\\s+").length;
                            String[] lines = content.split("\n", -1);
                            for (String line : lines) {
                                totalLineLength[0] += line.length();
                                lineCount[0]++;
                            }
                        } catch (IOException ignored) {
                            // Skip unreadable files
                        }
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            });

            double avgLineLen = lineCount[0] > 0 ? (double) totalLineLength[0] / lineCount[0] : 40;
            // If we couldn't read characters, estimate from bytes
            long chars = charCount[0] > 0 ? charCount[0] : result.totalBytes();
            long words = wordCount[0] > 0 ? wordCount[0] : chars / 5; // average word length ~5

            return new ScanResult(
                    result.projectPath(),
                    result.projectName(),
                    result.timestamp(),
                    result.totalFiles(),
                    dirCount[0],
                    result.totalCodeLines(),
                    result.totalCommentLines(),
                    result.totalBlankLines(),
                    result.totalLines(),
                    chars,
                    words,
                    result.totalBytes(),
                    largestFile[0],
                    largestFileSize[0],
                    result.averageFileSize(),
                    avgLineLen,
                    result.languages(),
                    byExtension,
                    GitHelper.getGitStats(directory)
            );
        } catch (IOException e) {
            return result; // Return unaugmented result if walk fails
        }
    }

    private boolean isTextFile(String fileName) {
        String lower = fileName.toLowerCase();
        return lower.endsWith(".java") || lower.endsWith(".py") || lower.endsWith(".js")
                || lower.endsWith(".ts") || lower.endsWith(".tsx") || lower.endsWith(".jsx")
                || lower.endsWith(".c") || lower.endsWith(".cpp") || lower.endsWith(".h")
                || lower.endsWith(".cs") || lower.endsWith(".go") || lower.endsWith(".rs")
                || lower.endsWith(".rb") || lower.endsWith(".php") || lower.endsWith(".swift")
                || lower.endsWith(".kt") || lower.endsWith(".scala") || lower.endsWith(".r")
                || lower.endsWith(".sql") || lower.endsWith(".sh") || lower.endsWith(".bash")
                || lower.endsWith(".html") || lower.endsWith(".css") || lower.endsWith(".xml")
                || lower.endsWith(".json") || lower.endsWith(".yaml") || lower.endsWith(".yml")
                || lower.endsWith(".md") || lower.endsWith(".txt") || lower.endsWith(".toml")
                || lower.endsWith(".cfg") || lower.endsWith(".ini") || lower.endsWith(".properties")
                || lower.endsWith(".gradle") || lower.endsWith(".kts") || lower.endsWith(".vue")
                || lower.endsWith(".svelte") || lower.endsWith(".dart") || lower.endsWith(".lua")
                || lower.endsWith(".ex") || lower.endsWith(".exs") || lower.endsWith(".erl")
                || lower.endsWith(".hs") || lower.endsWith(".ml") || lower.endsWith(".clj");
    }
}
