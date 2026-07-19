package dev.codemeter.export;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.codemeter.core.metrics.PhysicalCalculator;
import dev.codemeter.core.metrics.PrintCalculator;
import dev.codemeter.core.model.*;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Service for exporting scan results to various formats.
 */
public class ExportService {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Export scan results to the specified format.
     */
    public void export(ScanResult result, String format, Path outputPath) throws IOException {
        switch (format.toLowerCase()) {
            case "json" -> Files.writeString(outputPath, exportJson(result));
            case "csv" -> Files.writeString(outputPath, exportCsv(result));
            case "markdown", "md" -> Files.writeString(outputPath, exportMarkdown(result));
            case "pdf" -> exportPdf(result, outputPath);
            case "svg" -> exportSvg(result, outputPath);
            case "png" -> exportPng(result, outputPath);
            default -> throw new IOException("Unsupported format: " + format);
        }
    }

    private String exportJson(ScanResult result) {
        Settings settings = new Settings();
        PhysicalMetrics physical = PhysicalCalculator.calculate(result, settings);
        PrintedMetrics printed = PrintCalculator.calculate(result, settings);

        record ExportData(ScanResult scan, PhysicalMetrics physical, PrintedMetrics printed) {}
        return GSON.toJson(new ExportData(result, physical, printed));
    }

    private String exportCsv(ScanResult result) {
        StringBuilder csv = new StringBuilder();
        csv.append("Language,Files,Code,Comments,Blanks,Total Lines,Bytes\n");
        for (LanguageStats lang : result.languages()) {
            csv.append(String.format("%s,%d,%d,%d,%d,%d,%d\n",
                    escapeCsv(lang.language()),
                    lang.files(),
                    lang.codeLines(),
                    lang.commentLines(),
                    lang.blankLines(),
                    lang.totalLines(),
                    lang.bytes()));
        }
        csv.append(String.format("\nTotal,%d,%d,%d,%d,%d,%d\n",
                result.totalFiles(),
                result.totalCodeLines(),
                result.totalCommentLines(),
                result.totalBlankLines(),
                result.totalLines(),
                result.totalBytes()));
        return csv.toString();
    }

    private String exportMarkdown(ScanResult result) {
        Settings settings = new Settings();
        PhysicalMetrics pm = PhysicalCalculator.calculate(result, settings);

        StringBuilder md = new StringBuilder();
        md.append("# CodeMeter Report\n\n");
        md.append("## ").append(result.projectName()).append("\n\n");

        md.append("### Overview\n\n");
        md.append("| Metric | Value |\n");
        md.append("|--------|-------|\n");
        md.append(String.format("| Files | %s |\n", PhysicalCalculator.formatNumber(result.totalFiles())));
        md.append(String.format("| Languages | %d |\n", result.languageCount()));
        md.append(String.format("| Code Lines | %s |\n", PhysicalCalculator.formatNumber(result.totalCodeLines())));
        md.append(String.format("| Comment Lines | %s |\n", PhysicalCalculator.formatNumber(result.totalCommentLines())));
        md.append(String.format("| Blank Lines | %s |\n", PhysicalCalculator.formatNumber(result.totalBlankLines())));
        md.append(String.format("| Total Lines | %s |\n", PhysicalCalculator.formatNumber(result.totalLines())));
        md.append(String.format("| Characters | %s |\n", PhysicalCalculator.formatNumber(result.totalCharacters())));
        md.append(String.format("| Bytes | %s |\n", PhysicalCalculator.formatNumber(result.totalBytes())));
        md.append("\n");

        md.append("### Physical Metrics\n\n");
        md.append("| Metric | Value |\n");
        md.append("|--------|-------|\n");
        md.append(String.format("| Character Length | %s |\n", PhysicalCalculator.formatMetric(pm.characterLengthKm(), "km")));
        md.append(String.format("| Stack Height | %s |\n", PhysicalCalculator.formatMetric(pm.verticalStackMeters(), "m")));
        md.append(String.format("| Football Fields | %.1f |\n", pm.footballFields()));
        md.append(String.format("| Burj Khalifas | %.2f |\n", pm.burjKhalifas()));
        md.append(String.format("| Eiffel Towers | %.2f |\n", pm.eiffelTowers()));
        md.append(String.format("| Pages | %s |\n", PhysicalCalculator.formatNumber(pm.totalPages())));
        md.append(String.format("| Trees Required | %.1f |\n", pm.treesRequired()));
        md.append(String.format("| Weight | %s |\n", PhysicalCalculator.formatMetric(pm.estimatedWeightKg(), "kg")));
        md.append("\n");

        md.append("### Language Breakdown\n\n");
        md.append("| Language | Files | Code | Comments | Blanks | % |\n");
        md.append("|----------|-------|------|----------|--------|----|n");
        for (LanguageStats lang : result.languages().stream()
                .sorted((a, b) -> Long.compare(b.codeLines(), a.codeLines()))
                .toList()) {
            md.append(String.format("| %s | %d | %s | %s | %s | %.1f%% |\n",
                    lang.language(),
                    lang.files(),
                    PhysicalCalculator.formatNumber(lang.codeLines()),
                    PhysicalCalculator.formatNumber(lang.commentLines()),
                    PhysicalCalculator.formatNumber(lang.blankLines()),
                    lang.percentageOf(result.totalCodeLines())));
        }
        md.append("\n");

        md.append("---\n");
        md.append("*Generated by [CodeMeter](https://github.com/codemeter/codemeter) — Measure your code. Physically.*\n");

        return md.toString();
    }

    private String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private void exportPdf(ScanResult result, Path outputPath) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(FontName.HELVETICA_BOLD), 18);
                contentStream.newLineAtOffset(50, 700);
                contentStream.showText("CodeMeter Report: " + result.projectName());
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(new PDType1Font(FontName.HELVETICA), 12);
                contentStream.newLineAtOffset(50, 660);
                contentStream.showText("Total Files: " + PhysicalCalculator.formatNumber(result.totalFiles()));
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Total Lines: " + PhysicalCalculator.formatNumber(result.totalLines()));
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Code Lines: " + PhysicalCalculator.formatNumber(result.totalCodeLines()));
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Characters: " + PhysicalCalculator.formatNumber(result.totalCharacters()));
                contentStream.endText();
            }

            document.save(outputPath.toFile());
        }
    }

    private void drawCard(Graphics2D g, ScanResult result) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(25, 25, 25)); // Dark background
        g.fillRect(0, 0, 800, 400);

        g.setColor(new Color(139, 92, 246)); // Purple accent
        g.setFont(new Font("SansSerif", Font.BOLD, 36));
        g.drawString("CodeMeter", 50, 80);

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.PLAIN, 24));
        g.drawString("Project: " + result.projectName(), 50, 140);
        
        g.setFont(new Font("SansSerif", Font.PLAIN, 20));
        g.drawString("Lines of Code: " + PhysicalCalculator.formatNumber(result.totalCodeLines()), 50, 200);
        g.drawString("Total Files: " + PhysicalCalculator.formatNumber(result.totalFiles()), 50, 240);
        g.drawString("Characters: " + PhysicalCalculator.formatNumber(result.totalCharacters()), 50, 280);
        
        g.setColor(new Color(150, 150, 150));
        g.setFont(new Font("SansSerif", Font.ITALIC, 16));
        g.drawString("Generated by codemeter.dev", 50, 350);
    }

    private void exportSvg(ScanResult result, Path outputPath) throws IOException {
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);
        
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
        svgGenerator.setSVGCanvasSize(new java.awt.Dimension(800, 400));
        
        drawCard(svgGenerator, result);
        
        try (Writer out = new OutputStreamWriter(new FileOutputStream(outputPath.toFile()), "UTF-8")) {
            svgGenerator.stream(out, true);
        }
    }

    private void exportPng(ScanResult result, Path outputPath) throws IOException {
        BufferedImage image = new BufferedImage(800, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        
        drawCard(g, result);
        g.dispose();
        
        ImageIO.write(image, "png", outputPath.toFile());
    }
}
