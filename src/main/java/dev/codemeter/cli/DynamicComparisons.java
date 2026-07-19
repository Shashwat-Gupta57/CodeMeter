package dev.codemeter.cli;

import dev.codemeter.core.metrics.PhysicalCalculator;
import dev.codemeter.core.model.PhysicalMetrics;
import java.util.ArrayList;
import java.util.List;

public class DynamicComparisons {

    public static String getBestHeightComparison(PhysicalMetrics pm) {
        double cm = pm.verticalStackMeters() * 100;
        
        if (pm.mountEverests() >= 1.0) {
            return String.format("roughly the height of %.1f Mount Everests.", pm.mountEverests());
        } else if (pm.burjKhalifas() >= 1.0) {
            return String.format("about the height of %.1f Burj Khalifas.", pm.burjKhalifas());
        } else if (pm.eiffelTowers() >= 1.0) {
            return String.format("taller than %.1f Eiffel Towers.", pm.eiffelTowers());
        } else if (cm > 300) { 
            return String.format("about %.1f stories high.", pm.verticalStackMeters() / 3.0);
        } else if (cm > 100) {
            return "about the height of a dining table.";
        } else if (cm > 30) {
            return "about the height of a desktop monitor.";
        } else if (cm > 15) {
            return "roughly the height of a paperback novel.";
        } else if (cm > 5) {
            return "roughly the height of a coffee mug.";
        } else {
            return "about the height of a matchbox.";
        }
    }

    public static String getBestWeightComparison(PhysicalMetrics pm) {
        double kg = pm.estimatedWeightKg();
        
        if (kg > 5000) {
            return String.format("weighing as much as %.1f African Elephants.", kg / 4000.0);
        } else if (kg > 1500) {
            return "about the weight of a family car.";
        } else if (kg > 300) {
            return "about the weight of a grand piano.";
        } else if (kg > 70) {
            return "about the weight of an adult human.";
        } else if (kg > 20) {
            return "about the weight of a Golden Retriever.";
        } else if (kg > 3.0) {
            return "about the weight of a gaming laptop.";
        } else if (kg > 1.0) {
            return "about the weight of a hardcover book.";
        } else if (kg > 0.2) {
            return "about the weight of a smartphone.";
        } else {
            return "weighing about as much as a few letters.";
        }
    }

    public static List<String> getBestDistanceComparisons(PhysicalMetrics pm) {
        List<String> comps = new ArrayList<>();
        
        if (pm.earthCircumferencePercent() >= 0.5) {
            comps.add(String.format("✓ %.1f%% of Earth's circumference", pm.earthCircumferencePercent()));
            comps.add(String.format("✓ %.1f Marathons", pm.marathons()));
        } else if (pm.marathons() >= 1.0) {
            comps.add(String.format("✓ %.1f Marathons", pm.marathons()));
            comps.add(String.format("✓ %.0f football fields", pm.footballFields()));
        } else if (pm.footballFields() >= 1.0) {
            comps.add(String.format("✓ %.0f football fields", pm.footballFields()));
            comps.add(String.format("✓ Around Central Park %.1f times", pm.centralParkLoops()));
        } else if (pm.olympicSwimmingPools() >= 1.0) {
            comps.add(String.format("✓ %.1f Olympic swimming pools", pm.olympicSwimmingPools()));
            comps.add(String.format("✓ %.1f Basketball courts", pm.basketballCourts()));
        } else {
            comps.add(String.format("✓ %.1f Basketball courts", pm.basketballCourts()));
            comps.add(String.format("✓ %.1f Tennis courts", pm.tennisCourts()));
        }
        
        return comps;
    }
}
