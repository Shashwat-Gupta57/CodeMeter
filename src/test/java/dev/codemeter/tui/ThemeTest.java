package dev.codemeter.tui;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ThemeTest {

    @Test
    void langColor_cyclesThroughPalette() {
        var color0 = Theme.langColor(0);
        var color1 = Theme.langColor(1);
        assertThat(color0).isNotEqualTo(color1);

        // Should cycle
        var colorWrapped = Theme.langColor(Theme.LANG_COLORS.length);
        assertThat(colorWrapped).isEqualTo(color0);
    }

    @Test
    void sectionTitle_containsTitle() {
        String result = Theme.sectionTitle("TEST");
        assertThat(result).contains("TEST");
        assertThat(result).contains(Theme.BOX_H);
    }

    @Test
    void progressBar_emptyAtZero() {
        String bar = Theme.progressBar(0, 10);
        assertThat(bar).doesNotContain(Theme.PROGRESS_FULL);
    }

    @Test
    void progressBar_fullAt100() {
        String bar = Theme.progressBar(100, 10);
        assertThat(bar).contains(Theme.PROGRESS_FULL);
    }

    @Test
    void keyHint_formatsCorrectly() {
        String result = Theme.keyHint("Enter", "Select");
        assertThat(result).isEqualTo("[Enter] Select");
    }
}
