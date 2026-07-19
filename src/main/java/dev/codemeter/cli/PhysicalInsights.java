package dev.codemeter.cli;

import dev.codemeter.core.metrics.PhysicalCalculator;
import dev.codemeter.core.model.PhysicalMetrics;

import java.util.ArrayList;
import java.util.List;

public class PhysicalInsights {

    public static List<String> generateInsights(PhysicalMetrics pm) {
        List<String> insights = new ArrayList<>();
        
        // 1. Stack Height / Pages Insight
        if (pm.verticalStackMeters() > 10.0) {
            insights.add(String.format("📚 Stacked up, your code forms a %.1f meter tower—taller than a %d-story building.", 
                    pm.verticalStackMeters(), (int) (pm.verticalStackMeters() / 3.0)));
        } else if (pm.verticalStackMeters() > 1.0) {
            insights.add(String.format("📚 Your %s pages of code would stack %.1f meters high.", 
                    PhysicalCalculator.formatNumber(pm.totalPages()), pm.verticalStackMeters()));
        } else {
            insights.add(String.format("📚 Printing this project requires %s pages, stacking %.1f cm high.", 
                    PhysicalCalculator.formatNumber(pm.totalPages()), pm.verticalStackMeters() * 100));
        }

        // 2. Length / Distance Insight
        if (pm.marathons() >= 1.0) {
            insights.add(String.format("🏃 Laid end-to-end, your characters stretch %.1f km—that's %.1f marathons!", 
                    pm.characterLengthKm(), pm.marathons()));
        } else if (pm.footballFields() > 1.0) {
            insights.add(String.format("🏃 Laid end-to-end, your characters stretch across %.1f football fields.", 
                    pm.footballFields()));
        } else {
            insights.add(String.format("📏 The characters in your code stretch %.1f meters long.", 
                    pm.characterLengthKm() * 1000));
        }

        // 3. Environmental / Weight Insight
        if (pm.treesRequired() >= 1.0) {
            insights.add(String.format("🌲 You would need to cut down %.1f trees to print this codebase.", 
                    pm.treesRequired()));
        } else if (pm.estimatedWeightKg() > 1.0) {
            insights.add(String.format("⚖️ A physical copy of your code would weigh %.1f kg.", 
                    pm.estimatedWeightKg()));
        }

        // 4. Epic Landmarks (if applicable)
        if (pm.burjKhalifas() >= 1.0) {
            insights.add(String.format("🏢 Your code stack is taller than %.1f Burj Khalifas!", pm.burjKhalifas()));
        } else if (pm.empireStateBuildings() >= 1.0) {
            insights.add(String.format("🏢 Your code stack towers over %.1f Empire State Buildings.", pm.empireStateBuildings()));
        } else if (pm.eiffelTowers() >= 1.0) {
            insights.add(String.format("🗼 Your code stack is taller than %.1f Eiffel Towers.", pm.eiffelTowers()));
        }

        return insights;
    }
}
