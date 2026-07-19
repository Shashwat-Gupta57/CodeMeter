package dev.codemeter.cli;

import dev.codemeter.core.model.PhysicalMetrics;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DynamicComparisonsTest {

    private PhysicalMetrics createMetrics(double lengthKm, double heightM, double weightKg) {
        return new PhysicalMetrics(
                lengthKm, lengthKm * 0.62,
                heightM, heightM * 3.28,
                lengthKm, lengthKm * 0.62,
                lengthKm * 10, lengthKm * 0.1, lengthKm * 50, lengthKm * 60, lengthKm * 20,
                heightM / 828.0, heightM / 443.0, heightM / 330.0, heightM / 8848.0,
                lengthKm / 40075.0 * 100, lengthKm / 384400.0 * 100, lengthKm / 42.195, lengthKm / 10.0,
                10, 1.0, 3.28,
                weightKg, weightKg * 2.2,
                1, 1, 100, 100, 1, 1, 1, 0.05, 10, 5.0,
                100, 1000, 2000, 500, 1000
        );
    }

    @Test
    void distanceComparisons_scaleLogically() {
        PhysicalMetrics small = createMetrics(0.5, 1, 1);
        List<String> smallComps = DynamicComparisons.getBestDistanceComparisons(small);
        assertThat(smallComps).isNotEmpty();

        PhysicalMetrics large = createMetrics(50000, 1, 1);
        List<String> largeComps = DynamicComparisons.getBestDistanceComparisons(large);
        assertThat(largeComps).anyMatch(c -> c.contains("Earth") || c.contains("Moon"));
    }
}
