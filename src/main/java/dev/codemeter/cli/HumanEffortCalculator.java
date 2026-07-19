package dev.codemeter.cli;

import dev.codemeter.core.model.ScanResult;

public class HumanEffortCalculator {

    private static final int TYPING_WPM = 60;
    private static final int READING_WPM = 200;
    private static final int CHARS_PER_WORD = 5;

    public static String calculateTypingTime(ScanResult result) {
        long totalWords = result.totalCharacters() / CHARS_PER_WORD;
        long totalMinutes = totalWords / TYPING_WPM;
        return formatTime(totalMinutes, "typing");
    }

    public static String calculateReadingTime(ScanResult result) {
        long totalWords = result.totalCharacters() / CHARS_PER_WORD;
        long totalMinutes = totalWords / READING_WPM;
        return formatTime(totalMinutes, "reading");
    }
    
    public static String formatTimeExact(ScanResult result, boolean typing) {
        long totalWords = result.totalCharacters() / CHARS_PER_WORD;
        long totalMinutes = totalWords / (typing ? TYPING_WPM : READING_WPM);
        
        if (totalMinutes < 60) return "≈ " + totalMinutes + " minutes";
        return "≈ " + (totalMinutes / 60) + " hours";
    }

    private static String formatTime(long totalMinutes, String activity) {
        long hours = totalMinutes / 60;
        
        if (hours < 1) {
            return "≈ " + totalMinutes + " minutes of continuous " + activity;
        } else if (hours < 8) {
            return "≈ " + hours + " hours of continuous " + activity;
        } else if (hours < 40) {
            long days = hours / 8;
            return "≈ " + days + " full work days of " + activity;
        } else if (hours < 160) {
            long weeks = hours / 40;
            return "≈ " + weeks + " weeks of full-time " + activity;
        } else if (hours < 1920) {
            long months = hours / 160;
            return "≈ " + months + " months of full-time " + activity;
        } else {
            long years = hours / 1920;
            return "≈ " + years + " years of full-time " + activity;
        }
    }
}
