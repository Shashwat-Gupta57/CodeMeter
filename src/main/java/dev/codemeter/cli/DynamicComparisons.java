package dev.codemeter.cli;

import dev.codemeter.core.metrics.PhysicalCalculator;
import dev.codemeter.core.model.PhysicalMetrics;
import java.util.ArrayList;
import java.util.List;

public class DynamicComparisons {

    public static String getBestHeightComparison(PhysicalMetrics pm) {
        double cm = pm.verticalStackMeters() * 100;
        
        if (pm.mountEverests() >= 1.0) {
            return "≈ " + String.format("%.1f", pm.mountEverests()) + " Mount Everests";
        } else if (pm.burjKhalifas() >= 1.0) {
            return "≈ " + String.format("%.1f", pm.burjKhalifas()) + " Burj Khalifas";
        } else if (pm.eiffelTowers() >= 1.0) {
            return "≈ " + String.format("%.1f", pm.eiffelTowers()) + " Eiffel Towers";
        } else if (cm > 200) { // 2 meters
            return "≈ " + String.format("%.1f", pm.verticalStackMeters() / 3.0) + " stories high";
        } else if (cm > 100) {
            return "≈ Dining table";
        } else if (cm > 30) {
            return "≈ Desktop monitor";
        } else if (cm > 15) {
            return "≈ Paperback novel";
        } else if (cm > 5) {
            return "≈ Coffee mug";
        } else {
            return "≈ Matchbox";
        }
    }

    public static String getBestWeightComparison(PhysicalMetrics pm) {
        double kg = pm.estimatedWeightKg();
        
        if (kg > 5000) {
            return "≈ " + String.format("%.1f", kg / 4000.0) + " African Elephants";
        } else if (kg > 1500) {
            return "≈ Family car";
        } else if (kg > 300) {
            return "≈ Grand piano";
        } else if (kg > 70) {
            return "≈ Adult human";
        } else if (kg > 20) {
            return "≈ Golden Retriever";
        } else if (kg > 3.0) {
            return "≈ Gaming laptop";
        } else if (kg > 1.0) {
            return "≈ Hardcover book";
        } else if (kg > 0.2) {
            return "≈ Smartphone";
        } else {
            return "≈ A few letters";
        }
    }

    public static String getBestShelfComparison(PhysicalMetrics pm) {
        double cm = pm.shelfWidthMeters() * 100;
        
        if (pm.bookshelves() >= 1.0) {
            return String.format("Fills %.1f full bookshelves", pm.bookshelves());
        } else if (cm > 30) {
            return "Fits comfortably on one bookshelf";
        } else if (cm > 5) {
            return "Takes up a corner of a desk";
        } else {
            return "Barely noticeable on a shelf";
        }
    }

    public static List<String> getBestDistanceComparisons(PhysicalMetrics pm) {
        List<String> comps = new ArrayList<>();
        
        if (pm.earthCircumferencePercent() >= 0.5) {
            comps.add(String.format("✓ %.1f%% of Earth's circumference", pm.earthCircumferencePercent()));
            comps.add(String.format("✓ %.1f Marathons", pm.marathons()));
        } else if (pm.marathons() >= 1.0) {
            comps.add(String.format("✓ %.1f Marathons", pm.marathons()));
            comps.add(String.format("✓ %.1f Eiffel Towers", pm.eiffelTowers()));
            comps.add(String.format("✓ %.0f football fields", pm.footballFields()));
        } else if (pm.footballFields() >= 1.0) {
            comps.add(String.format("✓ %.0f football fields", pm.footballFields()));
            comps.add(String.format("✓ Around Central Park %.1f times", pm.centralParkLoops()));
            comps.add(String.format("✓ %.1f Marathons", pm.marathons()));
            comps.add(String.format("✓ %.1f Eiffel Towers", pm.eiffelTowers()));
        } else {
            comps.add(String.format("✓ %.1f Olympic swimming pools", pm.olympicSwimmingPools()));
            comps.add(String.format("✓ %.1f Basketball courts", pm.basketballCourts()));
            comps.add(String.format("✓ %.1f Tennis courts", pm.tennisCourts()));
        }
        
        return comps;
    }
}
