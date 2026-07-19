package dev.codemeter.core.model;

public class Settings {

    public enum ThemeMode { DARK, LIGHT, SYSTEM }
    public enum MeasurementSystem { METRIC, IMPERIAL }
    public enum PaperSize { A4, LETTER, LEGAL, A3 }
    public enum ScannerBackend { SCC, CLOC, NATIVE }
    public enum HeadlineStrategy { PHYSICAL, SCALE, DEFAULT }

    // Backend
    private ScannerBackend scannerBackend = ScannerBackend.SCC;
    private boolean gitIntegration = true;
    private boolean parallelism = true;
    private boolean cacheEnabled = true;

    // Typography
    private String fontName = "JetBrains Mono";
    private int fontSizePt = 11;
    private double characterWidthMm = 1.50; // Replaced 2.5mm
    private double characterHeightMm = 4.23;
    private double lineSpacing = 1.15;
    private int tabWidthSpaces = 4;
    private int charactersPerTab = 4;

    // Paper
    private PaperSize paperSize = PaperSize.A4;
    private double pageWidthMm = 210.0;
    private double pageHeightMm = 297.0;
    private double marginTopMm = 25.4;
    private double marginBottomMm = 25.4;
    private double marginLeftMm = 25.4;
    private double marginRightMm = 25.4;
    private double paperWeightGsm = 80.0;
    private double paperThicknessMm = 0.10;
    private boolean doubleSidedPrinting = true;
    private double bindingMarginMm = 10.0;

    // Printing
    private int printerDpi = 600;
    private double inkCoveragePercent = 5.0;
    private double printingCostPerPage = 0.05;
    private double bindingCostPerBook = 5.00;
    private int pagesPerBook = 300;
    private int pagesPerPrinterTray = 500;
    private int pagesPerBox = 2500;
    private double shelfWidthPerBookMm = 30.0;
    private double averagePrintSpeedPpm = 30.0;

    // Environment
    private double treePagesPerTree = 8333.0;
    private double co2PerSheetGrams = 4.5;
    private double paperRecyclingFactor = 0.6;

    // Reading & Writing
    private double readingSpeedWpm = 250.0;
    private double typingSpeedWpm = 60.0;
    private double workingHoursPerDay = 8.0;
    private double averageWordLength = 5.0;
    private double averageSentenceLength = 15.0;

    // Distance Calculations
    private double characterSpacingMm = 0.1;
    private double spaceCharacterWidthMm = 1.50;
    private double tabRenderWidthMm = 6.0;

    // Comparison Preferences
    private String comparisonStyle = "EDITORIAL";
    private String comparisonUnits = "EVERYDAY";
    private MeasurementSystem measurementSystem = MeasurementSystem.METRIC;
    private boolean showEstimates = true;
    private boolean showConfidenceLevels = true;
    private boolean showFunFacts = true;
    private boolean showAchievements = true;
    private boolean showRepositoryComparisons = true;
    private boolean showGitStatistics = true;

    // Story
    private ThemeMode theme = ThemeMode.DARK;
    private String storyDensity = "SPACIOUS";
    private String verbosity = "HIGH";
    private String whitespaceLevel = "HIGH";
    private int maximumComparisonsPerSection = 3;
    private HeadlineStrategy headlineStrategy = HeadlineStrategy.PHYSICAL;

    // Getters and Setters

    // Backend
    public ScannerBackend getScannerBackend() { return scannerBackend; }
    public void setScannerBackend(ScannerBackend backend) { this.scannerBackend = backend; }
    public boolean isGitIntegration() { return gitIntegration; }
    public void setGitIntegration(boolean git) { this.gitIntegration = git; }
    public boolean isParallelism() { return parallelism; }
    public void setParallelism(boolean par) { this.parallelism = par; }
    public boolean isCacheEnabled() { return cacheEnabled; }
    public void setCacheEnabled(boolean cache) { this.cacheEnabled = cache; }

    // Typography
    public String getFontName() { return fontName; }
    public void setFontName(String f) { this.fontName = f; }
    public int getFontSizePt() { return fontSizePt; }
    public void setFontSizePt(int s) { this.fontSizePt = s; }
    public double getCharacterWidthMm() { return characterWidthMm; }
    public void setCharacterWidthMm(double v) { this.characterWidthMm = v; }
    public double getCharacterHeightMm() { return characterHeightMm; }
    public void setCharacterHeightMm(double v) { this.characterHeightMm = v; }
    public double getLineSpacing() { return lineSpacing; }
    public void setLineSpacing(double v) { this.lineSpacing = v; }
    public int getTabWidthSpaces() { return tabWidthSpaces; }
    public void setTabWidthSpaces(int v) { this.tabWidthSpaces = v; }
    public int getCharactersPerTab() { return charactersPerTab; }
    public void setCharactersPerTab(int v) { this.charactersPerTab = v; }

    // Paper
    public PaperSize getPaperSize() { return paperSize; }
    public void setPaperSize(PaperSize p) { this.paperSize = p; }
    public double getPageWidthMm() { return pageWidthMm; }
    public void setPageWidthMm(double v) { this.pageWidthMm = v; }
    public double getPageHeightMm() { return pageHeightMm; }
    public void setPageHeightMm(double v) { this.pageHeightMm = v; }
    public double getMarginTopMm() { return marginTopMm; }
    public void setMarginTopMm(double v) { this.marginTopMm = v; }
    public double getMarginBottomMm() { return marginBottomMm; }
    public void setMarginBottomMm(double v) { this.marginBottomMm = v; }
    public double getMarginLeftMm() { return marginLeftMm; }
    public void setMarginLeftMm(double v) { this.marginLeftMm = v; }
    public double getMarginRightMm() { return marginRightMm; }
    public void setMarginRightMm(double v) { this.marginRightMm = v; }
    public double getPaperWeightGsm() { return paperWeightGsm; }
    public void setPaperWeightGsm(double v) { this.paperWeightGsm = v; }
    public double getPaperThicknessMm() { return paperThicknessMm; }
    public void setPaperThicknessMm(double v) { this.paperThicknessMm = v; }
    public boolean isDoubleSidedPrinting() { return doubleSidedPrinting; }
    public void setDoubleSidedPrinting(boolean d) { this.doubleSidedPrinting = d; }
    public double getBindingMarginMm() { return bindingMarginMm; }
    public void setBindingMarginMm(double v) { this.bindingMarginMm = v; }

    // Printing
    public int getPrinterDpi() { return printerDpi; }
    public void setPrinterDpi(int v) { this.printerDpi = v; }
    public double getInkCoveragePercent() { return inkCoveragePercent; }
    public void setInkCoveragePercent(double v) { this.inkCoveragePercent = v; }
    public double getPrintingCostPerPage() { return printingCostPerPage; }
    public void setPrintingCostPerPage(double v) { this.printingCostPerPage = v; }
    public double getBindingCostPerBook() { return bindingCostPerBook; }
    public void setBindingCostPerBook(double v) { this.bindingCostPerBook = v; }
    public int getPagesPerBook() { return pagesPerBook; }
    public void setPagesPerBook(int v) { this.pagesPerBook = v; }
    public int getPagesPerPrinterTray() { return pagesPerPrinterTray; }
    public void setPagesPerPrinterTray(int v) { this.pagesPerPrinterTray = v; }
    public int getPagesPerBox() { return pagesPerBox; }
    public void setPagesPerBox(int v) { this.pagesPerBox = v; }
    public double getShelfWidthPerBookMm() { return shelfWidthPerBookMm; }
    public void setShelfWidthPerBookMm(double v) { this.shelfWidthPerBookMm = v; }
    public double getAveragePrintSpeedPpm() { return averagePrintSpeedPpm; }
    public void setAveragePrintSpeedPpm(double v) { this.averagePrintSpeedPpm = v; }

    // Environment
    public double getTreePagesPerTree() { return treePagesPerTree; }
    public void setTreePagesPerTree(double v) { this.treePagesPerTree = v; }
    public double getCo2PerSheetGrams() { return co2PerSheetGrams; }
    public void setCo2PerSheetGrams(double v) { this.co2PerSheetGrams = v; }
    public double getPaperRecyclingFactor() { return paperRecyclingFactor; }
    public void setPaperRecyclingFactor(double v) { this.paperRecyclingFactor = v; }

    // Reading & Writing
    public double getReadingSpeedWpm() { return readingSpeedWpm; }
    public void setReadingSpeedWpm(double v) { this.readingSpeedWpm = v; }
    public double getTypingSpeedWpm() { return typingSpeedWpm; }
    public void setTypingSpeedWpm(double v) { this.typingSpeedWpm = v; }
    public double getWorkingHoursPerDay() { return workingHoursPerDay; }
    public void setWorkingHoursPerDay(double v) { this.workingHoursPerDay = v; }
    public double getAverageWordLength() { return averageWordLength; }
    public void setAverageWordLength(double v) { this.averageWordLength = v; }
    public double getAverageSentenceLength() { return averageSentenceLength; }
    public void setAverageSentenceLength(double v) { this.averageSentenceLength = v; }

    // Distance Calculations
    public double getCharacterSpacingMm() { return characterSpacingMm; }
    public void setCharacterSpacingMm(double v) { this.characterSpacingMm = v; }
    public double getSpaceCharacterWidthMm() { return spaceCharacterWidthMm; }
    public void setSpaceCharacterWidthMm(double v) { this.spaceCharacterWidthMm = v; }
    public double getTabRenderWidthMm() { return tabRenderWidthMm; }
    public void setTabRenderWidthMm(double v) { this.tabRenderWidthMm = v; }

    // Comparison Preferences
    public String getComparisonStyle() { return comparisonStyle; }
    public void setComparisonStyle(String v) { this.comparisonStyle = v; }
    public String getComparisonUnits() { return comparisonUnits; }
    public void setComparisonUnits(String v) { this.comparisonUnits = v; }
    public MeasurementSystem getMeasurementSystem() { return measurementSystem; }
    public void setMeasurementSystem(MeasurementSystem v) { this.measurementSystem = v; }
    public boolean isShowEstimates() { return showEstimates; }
    public void setShowEstimates(boolean v) { this.showEstimates = v; }
    public boolean isShowConfidenceLevels() { return showConfidenceLevels; }
    public void setShowConfidenceLevels(boolean v) { this.showConfidenceLevels = v; }
    public boolean isShowFunFacts() { return showFunFacts; }
    public void setShowFunFacts(boolean v) { this.showFunFacts = v; }
    public boolean isShowAchievements() { return showAchievements; }
    public void setShowAchievements(boolean v) { this.showAchievements = v; }
    public boolean isShowRepositoryComparisons() { return showRepositoryComparisons; }
    public void setShowRepositoryComparisons(boolean v) { this.showRepositoryComparisons = v; }
    public boolean isShowGitStatistics() { return showGitStatistics; }
    public void setShowGitStatistics(boolean v) { this.showGitStatistics = v; }

    // Story
    public ThemeMode getTheme() { return theme; }
    public void setTheme(ThemeMode v) { this.theme = v; }
    public String getStoryDensity() { return storyDensity; }
    public void setStoryDensity(String v) { this.storyDensity = v; }
    public String getVerbosity() { return verbosity; }
    public void setVerbosity(String v) { this.verbosity = v; }
    public String getWhitespaceLevel() { return whitespaceLevel; }
    public void setWhitespaceLevel(String v) { this.whitespaceLevel = v; }
    public int getMaximumComparisonsPerSection() { return maximumComparisonsPerSection; }
    public void setMaximumComparisonsPerSection(int v) { this.maximumComparisonsPerSection = v; }
    public HeadlineStrategy getHeadlineStrategy() { return headlineStrategy; }
    public void setHeadlineStrategy(HeadlineStrategy v) { this.headlineStrategy = v; }

    // Utility 
    public double getPrintableWidthMm() {
        return pageWidthMm - marginLeftMm - marginRightMm;
    }
    public double getPrintableHeightMm() {
        return pageHeightMm - marginTopMm - marginBottomMm;
    }
    public double getSheetWeightGrams() {
        return (pageWidthMm / 1000.0) * (pageHeightMm / 1000.0) * paperWeightGsm;
    }
}
