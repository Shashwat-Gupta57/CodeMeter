package dev.codemeter.core.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class SettingsTest {

    @Test
    void defaultSettings_areReasonable() {
        Settings s = new Settings();
        assertThat(s.getPaperSize()).isEqualTo(Settings.PaperSize.A4);
        assertThat(s.getFontSizePt()).isEqualTo(11);
        assertThat(s.getPaperWeightGsm()).isEqualTo(80.0);
        assertThat(s.getWorkingHoursPerDay()).isEqualTo(8.0);
    }

    @Test
    void settersAndGetters_workProperly() {
        Settings s = new Settings();
        s.setPaperSize(Settings.PaperSize.LETTER);
        s.setFontSizePt(12);
        s.setCharacterWidthMm(1.6);
        s.setTreePagesPerTree(9000);
        
        assertThat(s.getPaperSize()).isEqualTo(Settings.PaperSize.LETTER);
        assertThat(s.getFontSizePt()).isEqualTo(12);
        assertThat(s.getCharacterWidthMm()).isEqualTo(1.6);
        assertThat(s.getTreePagesPerTree()).isEqualTo(9000.0);
    }
}
