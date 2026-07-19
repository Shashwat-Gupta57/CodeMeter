package dev.codemeter.cli;

import dev.codemeter.core.model.Settings;
import dev.codemeter.core.storage.StorageManager;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Ansi;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Function;

@Command(
        name = "config",
        description = "Interactively configure physical assumptions and story settings",
        mixinStandardHelpOptions = true
)
public class ConfigCommand implements Runnable {

    record ConfigItem(int id, String key, String name, String category, Function<Settings, String> getter, java.util.function.BiConsumer<Settings, String> setter) {}

    private final StorageManager storageManager = new StorageManager();

    @Override
    public void run() {
        storageManager.load();
        Settings settings = storageManager.getSettings();
        List<ConfigItem> items = buildItems();

        Scanner scanner = new Scanner(System.in);
        System.out.println(Ansi.AUTO.string("@|bold,cyan CodeMeter Configuration|@"));
        System.out.println("Type a setting name or number to edit, or 'exit' to quit.");
        System.out.println();

        String currentCategory = "";
        for (ConfigItem item : items) {
            if (!item.category().equals(currentCategory)) {
                System.out.println(Ansi.AUTO.string("\n@|bold " + item.category() + "|@"));
                currentCategory = item.category();
            }
            System.out.println(Ansi.AUTO.string(String.format("%d. %s @|faint [%s]|@", item.id(), item.name(), item.getter().apply(settings))));
        }

        while (true) {
            System.out.println();
            System.out.print(Ansi.AUTO.string("@|bold,yellow > |@"));
            if (!scanner.hasNextLine()) break;
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
                break;
            }
            if (input.isEmpty()) continue;

            ConfigItem selected = null;
            try {
                int id = Integer.parseInt(input);
                selected = items.stream().filter(i -> i.id() == id).findFirst().orElse(null);
            } catch (NumberFormatException e) {
                // Fuzzy match
                selected = items.stream()
                        .filter(i -> i.name().toLowerCase().contains(input.toLowerCase()) || i.key().toLowerCase().contains(input.toLowerCase()))
                        .findFirst().orElse(null);
            }

            if (selected == null) {
                System.out.println(Ansi.AUTO.string("@|red Invalid selection.|@"));
                continue;
            }

            System.out.println(Ansi.AUTO.string("\n@|bold " + selected.name() + "|@"));
            System.out.println("Current value: " + selected.getter().apply(settings));
            System.out.print("Enter new value: ");
            String newValue = scanner.nextLine().trim();

            if (newValue.isEmpty()) {
                System.out.println(Ansi.AUTO.string("@|faint Cancelled.|@"));
                continue;
            }

            System.out.print("Confirm? (Y/N)\n> ");
            String confirm = scanner.nextLine().trim();
            if (confirm.equalsIgnoreCase("y")) {
                try {
                    selected.setter().accept(settings, newValue);
                    storageManager.saveSettings();
                    System.out.println(Ansi.AUTO.string("@|green Configuration updated.|@"));
                    System.out.println(selected.name() + ": " + selected.getter().apply(settings));
                } catch (Exception ex) {
                    System.out.println(Ansi.AUTO.string("@|red Invalid value format.|@"));
                }
            } else {
                System.out.println(Ansi.AUTO.string("@|faint Cancelled.|@"));
            }
        }
    }

    private List<ConfigItem> buildItems() {
        List<ConfigItem> items = new ArrayList<>();
        items.add(new ConfigItem(1, "font_name", "Font Name", "Typography", 
            s -> String.valueOf(s.getFontName()), 
            (s, v) -> s.setFontName(v)));
        items.add(new ConfigItem(2, "font_size_pt", "Font Size (pt)", "Typography", 
            s -> String.valueOf(s.getFontSizePt()), 
            (s, v) -> s.setFontSizePt(Integer.parseInt(v))));
        items.add(new ConfigItem(3, "character_width_mm", "Character Width (mm)", "Typography", 
            s -> String.valueOf(s.getCharacterWidthMm()), 
            (s, v) -> s.setCharacterWidthMm(Double.parseDouble(v))));
        items.add(new ConfigItem(4, "character_height_mm", "Character Height (mm)", "Typography", 
            s -> String.valueOf(s.getCharacterHeightMm()), 
            (s, v) -> s.setCharacterHeightMm(Double.parseDouble(v))));
        items.add(new ConfigItem(5, "line_spacing", "Line Spacing", "Typography", 
            s -> String.valueOf(s.getLineSpacing()), 
            (s, v) -> s.setLineSpacing(Double.parseDouble(v))));
        items.add(new ConfigItem(6, "tab_width_spaces", "Tab Width Spaces", "Typography", 
            s -> String.valueOf(s.getTabWidthSpaces()), 
            (s, v) -> s.setTabWidthSpaces(Integer.parseInt(v))));
        items.add(new ConfigItem(7, "characters_per_tab", "Characters Per Tab", "Typography", 
            s -> String.valueOf(s.getCharactersPerTab()), 
            (s, v) -> s.setCharactersPerTab(Integer.parseInt(v))));
        items.add(new ConfigItem(8, "paper_size", "Paper Size", "Paper", 
            s -> String.valueOf(s.getPaperSize()), 
            (s, v) -> s.setPaperSize(Settings.PaperSize.valueOf(v.toUpperCase()))));
        items.add(new ConfigItem(9, "page_width_mm", "Page Width (mm)", "Paper", 
            s -> String.valueOf(s.getPageWidthMm()), 
            (s, v) -> s.setPageWidthMm(Double.parseDouble(v))));
        items.add(new ConfigItem(10, "page_height_mm", "Page Height (mm)", "Paper", 
            s -> String.valueOf(s.getPageHeightMm()), 
            (s, v) -> s.setPageHeightMm(Double.parseDouble(v))));
        items.add(new ConfigItem(11, "margin_top_mm", "Margin Top (mm)", "Paper", 
            s -> String.valueOf(s.getMarginTopMm()), 
            (s, v) -> s.setMarginTopMm(Double.parseDouble(v))));
        items.add(new ConfigItem(12, "margin_bottom_mm", "Margin Bottom (mm)", "Paper", 
            s -> String.valueOf(s.getMarginBottomMm()), 
            (s, v) -> s.setMarginBottomMm(Double.parseDouble(v))));
        items.add(new ConfigItem(13, "margin_left_mm", "Margin Left (mm)", "Paper", 
            s -> String.valueOf(s.getMarginLeftMm()), 
            (s, v) -> s.setMarginLeftMm(Double.parseDouble(v))));
        items.add(new ConfigItem(14, "margin_right_mm", "Margin Right (mm)", "Paper", 
            s -> String.valueOf(s.getMarginRightMm()), 
            (s, v) -> s.setMarginRightMm(Double.parseDouble(v))));
        items.add(new ConfigItem(15, "paper_weight_gsm", "Paper Weight (gsm)", "Paper", 
            s -> String.valueOf(s.getPaperWeightGsm()), 
            (s, v) -> s.setPaperWeightGsm(Double.parseDouble(v))));
        items.add(new ConfigItem(16, "paper_thickness_mm", "Paper Thickness (mm)", "Paper", 
            s -> String.valueOf(s.getPaperThicknessMm()), 
            (s, v) -> s.setPaperThicknessMm(Double.parseDouble(v))));
        items.add(new ConfigItem(17, "double_sided_printing", "Double Sided Printing", "Paper", 
            s -> String.valueOf(s.isDoubleSidedPrinting()), 
            (s, v) -> s.setDoubleSidedPrinting(Boolean.parseBoolean(v))));
        items.add(new ConfigItem(18, "binding_margin_mm", "Binding Margin (mm)", "Paper", 
            s -> String.valueOf(s.getBindingMarginMm()), 
            (s, v) -> s.setBindingMarginMm(Double.parseDouble(v))));
        items.add(new ConfigItem(19, "printer_dpi", "Printer Dpi", "Printing", 
            s -> String.valueOf(s.getPrinterDpi()), 
            (s, v) -> s.setPrinterDpi(Integer.parseInt(v))));
        items.add(new ConfigItem(20, "ink_coverage_percent", "Ink Coverage Percent", "Printing", 
            s -> String.valueOf(s.getInkCoveragePercent()), 
            (s, v) -> s.setInkCoveragePercent(Double.parseDouble(v))));
        items.add(new ConfigItem(21, "printing_cost_per_page", "Printing Cost Per Page", "Printing", 
            s -> String.valueOf(s.getPrintingCostPerPage()), 
            (s, v) -> s.setPrintingCostPerPage(Double.parseDouble(v))));
        items.add(new ConfigItem(22, "binding_cost_per_book", "Binding Cost Per Book", "Printing", 
            s -> String.valueOf(s.getBindingCostPerBook()), 
            (s, v) -> s.setBindingCostPerBook(Double.parseDouble(v))));
        items.add(new ConfigItem(23, "pages_per_book", "Pages Per Book", "Printing", 
            s -> String.valueOf(s.getPagesPerBook()), 
            (s, v) -> s.setPagesPerBook(Integer.parseInt(v))));
        items.add(new ConfigItem(24, "pages_per_printer_tray", "Pages Per Printer Tray", "Printing", 
            s -> String.valueOf(s.getPagesPerPrinterTray()), 
            (s, v) -> s.setPagesPerPrinterTray(Integer.parseInt(v))));
        items.add(new ConfigItem(25, "pages_per_box", "Pages Per Box", "Printing", 
            s -> String.valueOf(s.getPagesPerBox()), 
            (s, v) -> s.setPagesPerBox(Integer.parseInt(v))));
        items.add(new ConfigItem(26, "shelf_width_per_book_mm", "Shelf Width Per Book (mm)", "Printing", 
            s -> String.valueOf(s.getShelfWidthPerBookMm()), 
            (s, v) -> s.setShelfWidthPerBookMm(Double.parseDouble(v))));
        items.add(new ConfigItem(27, "average_print_speed_ppm", "Average Print Speed (ppm)", "Printing", 
            s -> String.valueOf(s.getAveragePrintSpeedPpm()), 
            (s, v) -> s.setAveragePrintSpeedPpm(Double.parseDouble(v))));
        items.add(new ConfigItem(28, "tree_pages_per_tree", "Tree Pages Per Tree", "Environment", 
            s -> String.valueOf(s.getTreePagesPerTree()), 
            (s, v) -> s.setTreePagesPerTree(Double.parseDouble(v))));
        items.add(new ConfigItem(29, "co2_per_sheet_grams", "Co2 Per Sheet Grams", "Environment", 
            s -> String.valueOf(s.getCo2PerSheetGrams()), 
            (s, v) -> s.setCo2PerSheetGrams(Double.parseDouble(v))));
        items.add(new ConfigItem(30, "paper_recycling_factor", "Paper Recycling Factor", "Environment", 
            s -> String.valueOf(s.getPaperRecyclingFactor()), 
            (s, v) -> s.setPaperRecyclingFactor(Double.parseDouble(v))));
        items.add(new ConfigItem(31, "reading_speed_wpm", "Reading Speed (wpm)", "Reading & Writing", 
            s -> String.valueOf(s.getReadingSpeedWpm()), 
            (s, v) -> s.setReadingSpeedWpm(Double.parseDouble(v))));
        items.add(new ConfigItem(32, "typing_speed_wpm", "Typing Speed (wpm)", "Reading & Writing", 
            s -> String.valueOf(s.getTypingSpeedWpm()), 
            (s, v) -> s.setTypingSpeedWpm(Double.parseDouble(v))));
        items.add(new ConfigItem(33, "working_hours_per_day", "Working Hours Per Day", "Reading & Writing", 
            s -> String.valueOf(s.getWorkingHoursPerDay()), 
            (s, v) -> s.setWorkingHoursPerDay(Double.parseDouble(v))));
        items.add(new ConfigItem(34, "average_word_length", "Average Word Length", "Reading & Writing", 
            s -> String.valueOf(s.getAverageWordLength()), 
            (s, v) -> s.setAverageWordLength(Double.parseDouble(v))));
        items.add(new ConfigItem(35, "average_sentence_length", "Average Sentence Length", "Reading & Writing", 
            s -> String.valueOf(s.getAverageSentenceLength()), 
            (s, v) -> s.setAverageSentenceLength(Double.parseDouble(v))));
        items.add(new ConfigItem(36, "character_spacing_mm", "Character Spacing (mm)", "Distance Calculations", 
            s -> String.valueOf(s.getCharacterSpacingMm()), 
            (s, v) -> s.setCharacterSpacingMm(Double.parseDouble(v))));
        items.add(new ConfigItem(37, "space_character_width_mm", "Space Character Width (mm)", "Distance Calculations", 
            s -> String.valueOf(s.getSpaceCharacterWidthMm()), 
            (s, v) -> s.setSpaceCharacterWidthMm(Double.parseDouble(v))));
        items.add(new ConfigItem(38, "tab_render_width_mm", "Tab Render Width (mm)", "Distance Calculations", 
            s -> String.valueOf(s.getTabRenderWidthMm()), 
            (s, v) -> s.setTabRenderWidthMm(Double.parseDouble(v))));
        return items;
    }
}
