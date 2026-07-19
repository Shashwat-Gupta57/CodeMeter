package dev.codemeter.cli;

import dev.codemeter.core.model.PhysicalMetrics;
import dev.codemeter.core.model.ScanResult;
import dev.codemeter.core.metrics.PhysicalCalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DynamicComparisons {

    private static String getCheckmark() {
        return "✓";
    }

    public static String getBestHeightComparison(PhysicalMetrics pm) {
        double cm = pm.verticalStackMeters() * 100;
        
        if (pm.mountEverests() >= 1.0) {
            return String.format("roughly the height of %.1f Mount Everests.", pm.mountEverests());
        } else if (pm.burjKhalifas() >= 1.0) {
            return String.format("about the height of %.1f Burj Khalifas.", pm.burjKhalifas());
        } else if (pm.eiffelTowers() >= 1.0) {
            return String.format("taller than %.1f Eiffel Towers.", pm.eiffelTowers());
        } else if (pm.verticalStackMeters() > 10.0) {
            return "about the height of a telephone pole.";
        } else if (pm.verticalStackMeters() > 5.0) {
            return "about the height of an adult giraffe.";
        } else if (cm > 300) { 
            return String.format("about %.1f stories high.", pm.verticalStackMeters() / 3.0);
        } else if (cm > 170) {
            return "about the height of a human.";
        } else if (cm > 100) {
            return "about the height of a dining table.";
        } else if (cm > 45) {
            return "about the height of a standard chair.";
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
        
        if (pm.moonDistancePercent() >= 0.1) {
            comps.add(String.format("%s %.1f%% of the distance to the Moon", getCheckmark(), pm.moonDistancePercent()));
            comps.add(String.format("%s %.1f%% of Earth's circumference", getCheckmark(), pm.earthCircumferencePercent()));
        } else if (pm.earthCircumferencePercent() >= 0.5) {
            comps.add(String.format("%s %.1f%% of Earth's circumference", getCheckmark(), pm.earthCircumferencePercent()));
            comps.add(String.format("%s %.1f Marathons", getCheckmark(), pm.marathons()));
        } else if (pm.marathons() >= 1.0) {
            comps.add(String.format("%s %.1f Marathons", getCheckmark(), pm.marathons()));
            comps.add(String.format("%s %.0f football fields", getCheckmark(), pm.footballFields()));
        } else if (pm.footballFields() >= 5.0) {
            comps.add(String.format("%s %.0f football fields", getCheckmark(), pm.footballFields()));
            comps.add(String.format("%s %.1f airport runways", getCheckmark(), pm.characterLengthKm() / 3.0)); // Assume 3km runway
        } else if (pm.footballFields() >= 1.0) {
            comps.add(String.format("%s %.0f football fields", getCheckmark(), pm.footballFields()));
            comps.add(String.format("%s Around Central Park %.1f times", getCheckmark(), pm.centralParkLoops()));
        } else if (pm.olympicSwimmingPools() >= 1.0) {
            comps.add(String.format("%s %.1f Olympic swimming pools", getCheckmark(), pm.olympicSwimmingPools()));
            comps.add(String.format("%s %.1f city blocks", getCheckmark(), pm.characterLengthKm() * 1000 / 80.0)); // ~80m city block
        } else {
            comps.add(String.format("%s %.1f city blocks", getCheckmark(), pm.characterLengthKm() * 1000 / 80.0));
            comps.add(String.format("%s %.1f Basketball courts", getCheckmark(), pm.basketballCourts()));
        }
        
        return comps;
    }

    public static List<String> getBookComparisons(PhysicalMetrics pm) {
        List<String> comps = new ArrayList<>();
        long pages = pm.totalPages();

        if (pages > 4000) {
            comps.add(String.format("%s %.1f Encyclopedia Brittanica sets", getCheckmark(), pages / 32000.0));
            comps.add(String.format("%s %.1f complete Harry Potter series", getCheckmark(), pages / 4200.0));
        } else if (pages > 1000) {
            comps.add(String.format("%s %.1f complete Harry Potter series", getCheckmark(), pages / 4200.0));
            comps.add(String.format("%s %.1f Lord of the Rings trilogies", getCheckmark(), pages / 1178.0));
        } else if (pages > 300) {
            comps.add(String.format("%s %.1f College textbooks", getCheckmark(), pages / 800.0));
            comps.add(String.format("%s %.1f standard novels", getCheckmark(), pages / 300.0));
        } else if (pages > 50) {
            comps.add(String.format("%s %.1f standard novels", getCheckmark(), pages / 300.0));
            comps.add(String.format("%s %.1f magazines", getCheckmark(), pages / 80.0));
        } else {
            comps.add(String.format("%s %.1f magazines", getCheckmark(), pages / 80.0));
            comps.add(String.format("%s %.1f pamphlets", getCheckmark(), pages / 10.0));
        }
        return comps;
    }

    public static String getRepositoryScaleBenchmark(ScanResult result) {
        long lines = result.totalCodeLines();
        if (lines > 30_000_000) return "Windows 10 Kernel";
        if (lines > 27_000_000) return "Linux Kernel";
        if (lines > 15_000_000) return "Chromium";
        if (lines > 6_000_000) return "Minecraft";
        if (lines > 4_000_000) return "VS Code";
        if (lines > 2_000_000) return "Node.js";
        if (lines > 1_500_000) return "Spring Boot";
        if (lines > 300_000) return "Git";
        if (lines > 150_000) return "SQLite";
        if (lines > 130_000) return "Redis";
        if (lines > 50_000) return "BusyBox";
        if (lines > 10_000) return "the original UNIX v1 kernel";
        return null; // Too small for these big benchmarks
    }

    public static List<String> getFunFacts(ScanResult result, PhysicalMetrics pm) {
        List<String> facts = new ArrayList<>();
        
        // Size fact
        String scale = getRepositoryScaleBenchmark(result);
        if (scale != null) {
            facts.add("Your project is now larger than " + scale + ".");
        }
        
        // Time fact
        double printTime = pm.timeToPrintMinutes();
        if (printTime > 60 * 24 * 365) {
            facts.add(String.format("Printing this code would take %.1f years running 24/7.", printTime / (60 * 24 * 365.0)));
        } else if (printTime > 60 * 24) {
            facts.add(String.format("Printing this code would take %.1f straight days.", printTime / (60 * 24.0)));
        } else if (printTime > 60) {
            facts.add(String.format("It would take %.1f hours for a standard printer to print your code.", printTime / 60.0));
        }

        // Weight fact
        if (pm.estimatedWeightKg() > 1000) {
            facts.add("The printed code is heavy enough to crush a small car.");
        } else if (pm.estimatedWeightKg() > 100) {
            facts.add("You could not lift the printed version of your codebase by yourself.");
        }

        // Distance fact
        if (pm.characterLengthKm() > 100) {
            facts.add("You could drive at highway speeds for an hour alongside your code.");
        }

        // Token fact
        if (pm.estimatedTokenCount() > 1_000_000) {
            facts.add(String.format("Feeding this codebase to an LLM would consume roughly %s tokens.", PhysicalCalculator.formatNumber(pm.estimatedTokenCount())));
        }

        // Fill up to 3 facts using generic fallbacks if needed
        if (facts.size() < 3) {
            facts.add(String.format("Your project consumes %.2f liters of printer ink if printed.", pm.inkEstimationLiters()));
        }
        if (facts.size() < 3) {
            facts.add(String.format("You've written enough characters to fill %d floppy disks.", (long)(result.totalCharacters() / 1.44e6)));
        }
        if (facts.size() < 3) {
            facts.add(String.format("You have created roughly %s AST nodes.", PhysicalCalculator.formatNumber(pm.estimatedAstNodes())));
        }
        if (facts.size() < 3) {
            facts.add(String.format("Your codebase requires %.1f trees to produce the paper to print it.", pm.treesRequired()));
        }

        Collections.shuffle(facts);
        return facts.subList(0, Math.min(facts.size(), 5));
    }
}
