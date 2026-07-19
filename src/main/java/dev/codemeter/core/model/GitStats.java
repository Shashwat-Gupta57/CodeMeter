package dev.codemeter.core.model;

/**
 * Metadata derived from the Git repository (if available).
 */
public record GitStats(
        String creationDate,
        String lastCommitDate,
        long ageInDays,
        long totalCommits
) {}
