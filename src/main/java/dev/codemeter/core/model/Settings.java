package dev.codemeter.core.model;

/**
 * Application settings, persisted to config.toml.
 */
public class Settings {

    public enum ThemeMode { DARK, LIGHT, SYSTEM }
    public enum MeasurementSystem { METRIC, IMPERIAL }
    public enum PaperSize { A4, LETTER, LEGAL, A3 }
    public enum MarginType { NORMAL, NARROW, CUSTOM }
    public enum InkType { LASER, INKJET, DRAFT, CUSTOM }

    // Theme
    private ThemeMode theme = ThemeMode.DARK;
    private boolean animationsEnabled = true;

    // Measurement
    private MeasurementSystem measurement = MeasurementSystem.METRIC;

    // Print settings
    private PaperSize paperSize = PaperSize.A4;
    private MarginType marginType = MarginType.NORMAL;
    private String fontName = "JetBrains Mono";
    private int fontSize = 10;
    private double lineSpacing = 1.15;
    private InkType inkType = InkType.LASER;
    private double paperThicknessMm = 0.1;

    // Margins in mm
    private double marginTop = 25.4;
    private double marginBottom = 25.4;
    private double marginLeft = 25.4;
    private double marginRight = 25.4;

    // Feature toggles
    private boolean comparisonObjectsEnabled = true;
    private boolean historyEnabled = true;

    // Getters and setters
    public ThemeMode getTheme() { return theme; }
    public void setTheme(ThemeMode theme) { this.theme = theme; }

    public boolean isAnimationsEnabled() { return animationsEnabled; }
    public void setAnimationsEnabled(boolean enabled) { this.animationsEnabled = enabled; }

    public MeasurementSystem getMeasurement() { return measurement; }
    public void setMeasurement(MeasurementSystem measurement) { this.measurement = measurement; }

    public PaperSize getPaperSize() { return paperSize; }
    public void setPaperSize(PaperSize paperSize) { this.paperSize = paperSize; }

    public MarginType getMarginType() { return marginType; }
    public void setMarginType(MarginType marginType) { this.marginType = marginType; }

    public String getFontName() { return fontName; }
    public void setFontName(String fontName) { this.fontName = fontName; }

    public int getFontSize() { return fontSize; }
    public void setFontSize(int fontSize) { this.fontSize = Math.max(6, Math.min(30, fontSize)); }

    public double getLineSpacing() { return lineSpacing; }
    public void setLineSpacing(double lineSpacing) { this.lineSpacing = lineSpacing; }

    public InkType getInkType() { return inkType; }
    public void setInkType(InkType inkType) { this.inkType = inkType; }

    public double getPaperThicknessMm() { return paperThicknessMm; }
    public void setPaperThicknessMm(double thickness) { this.paperThicknessMm = thickness; }

    public double getMarginTop() { return marginTop; }
    public void setMarginTop(double mm) { this.marginTop = mm; }

    public double getMarginBottom() { return marginBottom; }
    public void setMarginBottom(double mm) { this.marginBottom = mm; }

    public double getMarginLeft() { return marginLeft; }
    public void setMarginLeft(double mm) { this.marginLeft = mm; }

    public double getMarginRight() { return marginRight; }
    public void setMarginRight(double mm) { this.marginRight = mm; }

    public boolean isComparisonObjectsEnabled() { return comparisonObjectsEnabled; }
    public void setComparisonObjectsEnabled(boolean enabled) { this.comparisonObjectsEnabled = enabled; }

    public boolean isHistoryEnabled() { return historyEnabled; }
    public void setHistoryEnabled(boolean enabled) { this.historyEnabled = enabled; }

    // Paper dimensions in mm
    public double getPaperWidthMm() {
        return switch (paperSize) {
            case A4 -> 210;
            case LETTER -> 215.9;
            case LEGAL -> 215.9;
            case A3 -> 297;
        };
    }

    public double getPaperHeightMm() {
        return switch (paperSize) {
            case A4 -> 297;
            case LETTER -> 279.4;
            case LEGAL -> 355.6;
            case A3 -> 420;
        };
    }

    /**
     * Returns printable width in mm (total width minus left and right margins).
     */
    public double getPrintableWidthMm() {
        if (marginType == MarginType.NARROW) {
            return getPaperWidthMm() - 25.4; // ~0.5 inch each side
        }
        return getPaperWidthMm() - marginLeft - marginRight;
    }

    /**
     * Returns printable height in mm.
     */
    public double getPrintableHeightMm() {
        if (marginType == MarginType.NARROW) {
            return getPaperHeightMm() - 25.4;
        }
        return getPaperHeightMm() - marginTop - marginBottom;
    }

    /**
     * Returns approximate character width in mm for the configured font and size.
     * Monospace font: character width ≈ fontSize * 0.6 (in points → mm conversion).
     */
    public double getCharWidthMm() {
        // 1 pt = 0.3528 mm, monospace ratio ≈ 0.6
        return fontSize * 0.3528 * 0.6;
    }

    /**
     * Returns line height in mm for the configured font size and spacing.
     */
    public double getLineHeightMm() {
        return fontSize * 0.3528 * lineSpacing;
    }

    /**
     * Returns paper weight per sheet in grams (standard 80 gsm paper).
     */
    public double getSheetWeightGrams() {
        double areaSqM = (getPaperWidthMm() / 1000.0) * (getPaperHeightMm() / 1000.0);
        return areaSqM * 80; // 80 gsm standard
    }
}
