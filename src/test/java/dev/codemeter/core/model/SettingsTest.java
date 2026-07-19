package dev.codemeter.core.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class SettingsTest {

    @Test
    void defaultSettings_areReasonable() {
        Settings s = new Settings();
        assertThat(s.getTheme()).isEqualTo(Settings.ThemeMode.DARK);
        assertThat(s.getPaperSize()).isEqualTo(Settings.PaperSize.A4);
        assertThat(s.getFontSize()).isEqualTo(10);
        assertThat(s.isAnimationsEnabled()).isTrue();
        assertThat(s.isHistoryEnabled()).isTrue();
    }

    @Test
    void paperDimensions_A4() {
        Settings s = new Settings();
        s.setPaperSize(Settings.PaperSize.A4);
        assertThat(s.getPaperWidthMm()).isEqualTo(210.0);
        assertThat(s.getPaperHeightMm()).isEqualTo(297.0);
    }

    @Test
    void paperDimensions_Letter() {
        Settings s = new Settings();
        s.setPaperSize(Settings.PaperSize.LETTER);
        assertThat(s.getPaperWidthMm()).isEqualTo(215.9);
        assertThat(s.getPaperHeightMm()).isEqualTo(279.4);
    }

    @Test
    void printableWidth_substractsMargins() {
        Settings s = new Settings();
        s.setPaperSize(Settings.PaperSize.A4);
        double printableWidth = s.getPrintableWidthMm();
        assertThat(printableWidth).isLessThan(210.0);
        assertThat(printableWidth).isGreaterThan(100.0);
    }

    @Test
    void fontSize_clampedToValidRange() {
        Settings s = new Settings();
        s.setFontSize(3);
        assertThat(s.getFontSize()).isEqualTo(6);

        s.setFontSize(50);
        assertThat(s.getFontSize()).isEqualTo(30);

        s.setFontSize(12);
        assertThat(s.getFontSize()).isEqualTo(12);
    }

    @Test
    void charWidth_increasesWithFontSize() {
        Settings s = new Settings();
        s.setFontSize(8);
        double small = s.getCharWidthMm();

        s.setFontSize(14);
        double large = s.getCharWidthMm();

        assertThat(large).isGreaterThan(small);
    }
}
