package dev.codemeter.cli;

import dev.codemeter.core.model.ScanResult;
import dev.codemeter.core.model.Settings;

public class HumanEffortCalculator {

    private static final int TYPING_WPM = 60;
    private static final int READING_WPM = 200;
    private static final int CHARS_PER_WORD = 5;

    public static String calculateTypingTime(ScanResult result, Settings settings) {
        double charsPerWord = settings != null ? settings.getAverageWordLength() : 5.0;
        double typingWpm = settings != null ? settings.getTypingSpeedWpm() : 60.0;
        long totalWords = (long) (result.totalCharacters() / charsPerWord);
        long totalMinutes = (long) (totalWords / typingWpm);
        return formatTime(totalMinutes, "typing", settings);
    }

    public static String calculateReadingTime(ScanResult result, Settings settings) {
        double charsPerWord = settings != null ? settings.getAverageWordLength() : 5.0;
        double readingWpm = settings != null ? settings.getReadingSpeedWpm() : 250.0;
        long totalWords = (long) (result.totalCharacters() / charsPerWord);
        long totalMinutes = (long) (totalWords / readingWpm);
        return formatTime(totalMinutes, "reading", settings);
    }
    
    public static String formatTimeExact(ScanResult result, boolean typing, Settings settings) {
        double charsPerWord = settings != null ? settings.getAverageWordLength() : 5.0;
        double speed = settings != null ? (typing ? settings.getTypingSpeedWpm() : settings.getReadingSpeedWpm()) : (typing ? 60.0 : 250.0);
        long totalWords = (long) (result.totalCharacters() / charsPerWord);
        long totalMinutes = (long) (totalWords / speed);
        
        if (totalMinutes < 60) return "≈ " + totalMinutes + " minutes";
        return "≈ " + (totalMinutes / 60) + " hours";
    }

    private static String formatTime(long totalMinutes, String activity, Settings settings) {
        long hours = totalMinutes / 60;
        double workingHours = settings != null ? settings.getWorkingHoursPerDay() : 8.0;
        
        if (hours < 1) {
            return "≈ " + totalMinutes + " minutes of continuous " + activity;
        } else if (hours < workingHours) {
            return "≈ " + hours + " hours of continuous " + activity;
        } else if (hours < workingHours * 5) {
            long days = (long) (hours / workingHours);
            return "≈ " + days + " full work days of " + activity;
        } else if (hours < workingHours * 20) {
            long weeks = (long) (hours / (workingHours * 5));
            return "≈ " + weeks + " weeks of full-time " + activity;
        } else if (hours < workingHours * 240) {
            long months = (long) (hours / (workingHours * 20));
            return "≈ " + months + " months of full-time " + activity;
        } else {
            long years = (long) (hours / (workingHours * 240));
            return "≈ " + years + " years of full-time " + activity;
        }
    }
}
