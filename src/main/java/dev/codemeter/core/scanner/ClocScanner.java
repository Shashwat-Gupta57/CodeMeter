package dev.codemeter.core.scanner;

import dev.codemeter.core.model.LanguageStats;
import dev.codemeter.core.model.ScanResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scanner implementation using cloc (Count Lines of Code).
 * Fallback when scc is not available.
 */
public class ClocScanner implements CodeScanner {

    @Override
    public String name() {
        return "cloc";
    }

    @Override
    public boolean isAvailable() {
        try {
            ProcessBuilder pb = new ProcessBuilder("cloc", "--version");
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
                    "cloc", "--csv", "--quiet", directory.toAbsolutePath().toString()
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
                throw new ScanException("cloc timed out after 5 minutes");
            }

            if (progressCallback != null) progressCallback.accept(70);

            ScanResult result = parseClocCsvOutput(output.toString(), directory);

            if (progressCallback != null) progressCallback.accept(90);

            // Augment with file system stats
            result = augmentWithFileStats(result, directory);

            if (progressCallback != null) progressCallback.accept(100);

            return result;

        } catch (IOException e) {
            throw new ScanException("Failed to run cloc: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ScanException("Scan interrupted", e);
        }
    }

    private ScanResult parseClocCsvOutput(String csvOutput, Path directory) throws ScanException {
        List<LanguageStats> langStats = new ArrayList<>();
        long totalFiles = 0, totalCode = 0, totalComments = 0, totalBlanks = 0;

        String[] lines = csvOutput.split("\n");
        boolean inData = false;

        for (String line : lines) {
            if (line.trim().isEmpty()) continue;

            // cloc CSV format: files,language,blank,comment,code
            if (line.contains("files,language,blank,comment,code") ||
                    line.contains("language,filename")) {
                inData = true;
                continue;
            }

            if (!inData) continue;

            // Skip SUM line
            if (line.toLowerCase().contains("sum,") || line.toLowerCase().startsWith("sum")) {
                continue;
            }

            String[] parts = line.split(",");
            if (parts.length >= 5) {
                try {
                    long files = Long.parseLong(parts[0].trim());
                    String language = parts[1].trim();
                    long blanks = Long.parseLong(parts[2].trim());
                    long comments = Long.parseLong(parts[3].trim());
                    long code = Long.parseLong(parts[4].trim());
                    long total = code + comments + blanks;

                    langStats.add(new LanguageStats(language, files, code, comments, blanks, total, 0, 0));

                    totalFiles += files;
                    totalCode += code;
                    totalComments += comments;
                    totalBlanks += blanks;
                } catch (NumberFormatException ignored) {
                    // Skip malformed lines
                }
            }
        }

        if (langStats.isEmpty()) {
            String projectName = directory.getFileName() != null ? directory.getFileName().toString() : directory.toString();
            return new ScanResult(directory.toAbsolutePath().toString(), projectName, System.currentTimeMillis(),
                    0, 0, 0, 0, 0, 0, 0, 0, 0, "", 0, 0.0, 0.0, new ArrayList<>(), new HashMap<>());
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
                0,
                totalCode,
                totalComments,
                totalBlanks,
                totalLines,
                0, 0, 0,
                "", 0,
                totalFiles > 0 ? (double) totalLines / totalFiles : 0,
                0,
                langStats,
                new HashMap<>()
        );
    }

    private ScanResult augmentWithFileStats(ScanResult result, Path directory) {
        try {
            long[] dirCount = {0};
            long[] charCount = {0};
            long[] wordCount = {0};
            long[] totalBytes = {0};
            String[] largestFile = {""};
            long[] largestFileSize = {0};
            long[] totalLineLength = {0};
            long[] lineCount = {0};
            Map<String, Long> byExtension = new HashMap<>();

            Files.walkFileTree(directory, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    String dirName = dir.getFileName() != null ? dir.getFileName().toString() : "";
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

                    long size = attrs.size();
                    totalBytes[0] += size;

                    String fileName = file.getFileName().toString();
                    int dotIndex = fileName.lastIndexOf('.');
                    if (dotIndex > 0) {
                        String ext = fileName.substring(dotIndex);
                        byExtension.merge(ext, 1L, Long::sum);
                    }

                    if (size > largestFileSize[0]) {
                        largestFileSize[0] = size;
                        largestFile[0] = directory.relativize(file).toString();
                    }

                    if (size < 1_048_576) {
                        try {
                            String content = Files.readString(file);
                            charCount[0] += content.length();
                            wordCount[0] += content.split("\\s+").length;
                            String[] lines = content.split("\n", -1);
                            for (String line : lines) {
                                totalLineLength[0] += line.length();
                                lineCount[0]++;
                            }
                        } catch (IOException ignored) {}
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            });

            double avgLineLen = lineCount[0] > 0 ? (double) totalLineLength[0] / lineCount[0] : 40;
            long chars = charCount[0] > 0 ? charCount[0] : totalBytes[0];
            long words = wordCount[0] > 0 ? wordCount[0] : chars / 5;

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
                    totalBytes[0] > 0 ? totalBytes[0] : result.totalBytes(),
                    largestFile[0],
                    largestFileSize[0],
                    result.averageFileSize(),
                    avgLineLen,
                    result.languages(),
                    byExtension
            );
        } catch (IOException e) {
            return result;
        }
    }
}
