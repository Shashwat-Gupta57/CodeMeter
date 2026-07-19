package dev.codemeter.tui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import dev.codemeter.CodeMeter;
import dev.codemeter.core.metrics.PhysicalCalculator;
import dev.codemeter.core.metrics.PrintCalculator;
import dev.codemeter.core.metrics.ComparisonResult;
import dev.codemeter.core.model.*;
import dev.codemeter.core.scanner.CodeScanner;
import dev.codemeter.core.scanner.ScanException;
import dev.codemeter.core.scanner.ScannerFactory;
import dev.codemeter.core.storage.StorageManager;
import dev.codemeter.tui.screens.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Main TUI application controller.
 * Manages the fullscreen terminal UI with Lanterna.
 */
public class TuiApp {

    public enum AppScreen {
        HOME, SCANNING, DASHBOARD, SETTINGS, ABOUT, WRAPPED, SEARCH
    }

    public enum DashboardTab {
        OVERVIEW, PHYSICAL, PRINTED, GROWTH, HISTORY, ACHIEVEMENTS, COMPARISONS, SETTINGS_TAB, ABOUT_TAB
    }

    private Screen screen;
    private Terminal terminal;
    private StorageManager storage;
    private AppScreen currentScreen = AppScreen.HOME;
    private DashboardTab currentTab = DashboardTab.OVERVIEW;
    private ScanResult currentScanResult;
    private PhysicalMetrics currentPhysical;
    private PrintedMetrics currentPrinted;
    private boolean running = true;

    // Home screen state
    private int homeSelectedIndex = 0;
    private static final String[] HOME_ITEMS = {
            "Scan Current Directory",
            "Scan Another Folder",
            "Resume Last Scan",
            "Global Statistics",
            "History",
            "Wrapped",
            "Achievements",
            "Settings",
            "About",
            "Exit"
    };

    // Dashboard sidebar state
    private int sidebarIndex = 0;
    private static final String[] SIDEBAR_ITEMS = {
            "Overview", "Physical", "Printed", "Growth",
            "History", "Achievements", "Comparisons",
            "Settings", "About"
    };
    private static final String[] SIDEBAR_ICONS = {
            "◈", "📐", "🖨", "📈", "📅", "🏆", "⚖", "⚙", "ℹ"
    };

    // Scanning state
    private AtomicInteger scanProgress = new AtomicInteger(0);
    private AtomicBoolean scanComplete = new AtomicBoolean(false);
    private String scanError = null;
    private String scanningPath = "";

    // Scroll state
    private int scrollOffset = 0;

    // Search
    private String searchQuery = "";
    private boolean searchActive = false;

    // Wrapped page
    private int wrappedPage = 0;

    public void run() {
        try {
            initTerminal();
            storage = new StorageManager();
            storage.load();
            mainLoop();
        } catch (IOException e) {
            System.err.println("Failed to initialize terminal: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    private void initTerminal() throws IOException {
        DefaultTerminalFactory factory = new DefaultTerminalFactory();
        factory.setTerminalEmulatorTitle("CodeMeter — Measure your code. Physically.");
        factory.setPreferTerminalEmulator(false);
        factory.setForceTextTerminal(true);
        
        try {
            terminal = factory.createTerminal();
        } catch (IOException e) {
            if (System.getProperty("os.name", "").toLowerCase().startsWith("windows")) {
                // Fallback for GraalVM Native Image on Windows / Missing Lanterna WindowsTerminal
                terminal = new WindowsNativeTerminal(System.in, System.out, java.nio.charset.Charset.defaultCharset());
            } else {
                throw e;
            }
        }
        
        screen = new TerminalScreen(terminal);
        screen.startScreen();
        screen.setCursorPosition(null); // Hide cursor
    }

    private void mainLoop() throws IOException {
        while (running) {
            render();
            screen.refresh();

            KeyStroke key = screen.pollInput();
            if (key != null) {
                handleInput(key);
            } else {
                // Small sleep to prevent busy-waiting
                try { Thread.sleep(16); } catch (InterruptedException ignored) {}
            }

            // Check terminal resize
            TerminalSize newSize = screen.doResizeIfNecessary();
            if (newSize != null) {
                // Terminal was resized, will re-render on next loop
            }
        }
    }

    private void render() throws IOException {
        TextGraphics g = screen.newTextGraphics();
        TerminalSize size = screen.getTerminalSize();

        // Clear screen
        Renderer.clearArea(g, 0, 0, size.getColumns(), size.getRows(), Theme.BG_PRIMARY);

        switch (currentScreen) {
            case HOME -> renderHome(g, size);
            case SCANNING -> renderScanning(g, size);
            case DASHBOARD -> renderDashboard(g, size);
            case SETTINGS -> renderSettings(g, size);
            case ABOUT -> renderAbout(g, size);
            case WRAPPED -> renderWrapped(g, size);
            case SEARCH -> renderSearch(g, size);
        }
    }

    // ── HOME SCREEN ──────────────────────────────────────────

    private void renderHome(TextGraphics g, TerminalSize size) {
        int w = size.getColumns();
        int h = size.getRows();

        // Header
        renderHomeHeader(g, w);

        // Menu items
        int menuStartY = 10;
        int menuWidth = Math.min(50, w - 10);
        int menuX = (w - menuWidth) / 2;

        for (int i = 0; i < HOME_ITEMS.length; i++) {
            int y = menuStartY + i * 2;
            if (y >= h - 4) break;

            boolean selected = (i == homeSelectedIndex);
            String prefix = selected ? Theme.TRIANGLE_RIGHT + " " : "  ";

            if (selected) {
                g.setBackgroundColor(Theme.BG_SELECTED);
                g.setForegroundColor(Theme.ACCENT_PRIMARY);
                g.putString(menuX, y, " ".repeat(menuWidth));
                g.putString(menuX + 2, y, prefix + HOME_ITEMS[i]);
                g.setBackgroundColor(Theme.BG_PRIMARY);
            } else {
                g.setForegroundColor(Theme.TEXT_SECONDARY);
                g.putString(menuX + 2, y, prefix + HOME_ITEMS[i]);
            }
        }

        // Recent projects
        int recentY = menuStartY + HOME_ITEMS.length * 2 + 1;
        if (recentY < h - 4 && !storage.getRecentPaths().isEmpty()) {
            g.setForegroundColor(Theme.TEXT_MUTED);
            g.putString(menuX, recentY, Theme.sectionTitle("Recent Projects"));
            recentY++;

            List<String> recent = storage.getRecentPaths();
            for (int i = 0; i < Math.min(5, recent.size()) && recentY + i < h - 3; i++) {
                g.setForegroundColor(Theme.TEXT_SECONDARY);
                g.putString(menuX + 2, recentY + i, Theme.BULLET + " " +
                        Renderer.truncate(recent.get(i), menuWidth - 6));
            }
        }

        // Footer
        renderFooter(g, w, h);
    }

    private void renderHomeHeader(TextGraphics g, int width) {
        String[] logo = {
                "  ██████╗ ██████╗ ██████╗ ███████╗███╗   ███╗███████╗████████╗███████╗██████╗ ",
                " ██╔════╝██╔═══██╗██╔══██╗██╔════╝████╗ ████║██╔════╝╚══██╔══╝██╔════╝██╔══██╗",
                " ██║     ██║   ██║██║  ██║█████╗  ██╔████╔██║█████╗     ██║   █████╗  ██████╔╝",
                " ██║     ██║   ██║██║  ██║██╔══╝  ██║╚██╔╝██║██╔══╝     ██║   ██╔══╝  ██╔══██╗",
                " ╚██████╗╚██████╔╝██████╔╝███████╗██║ ╚═╝ ██║███████╗   ██║   ███████╗██║  ██║",
                "  ╚═════╝ ╚═════╝ ╚═════╝ ╚══════╝╚═╝     ╚═╝╚══════╝   ╚═╝   ╚══════╝╚═╝  ╚═╝"
        };

        int logoWidth = logo[0].length();
        if (width < logoWidth + 4) {
            // Small terminal fallback
            Renderer.drawCentered(g, 1, width, "CodeMeter", Theme.ACCENT_PRIMARY);
            Renderer.drawCentered(g, 2, width, CodeMeter.TAGLINE, Theme.TEXT_SECONDARY);
            return;
        }

        g.setForegroundColor(Theme.ACCENT_PRIMARY);
        for (int i = 0; i < logo.length; i++) {
            int x = (width - logoWidth) / 2;
            g.putString(Math.max(0, x), i + 1, logo[i]);
        }

        Renderer.drawCentered(g, logo.length + 2, width, CodeMeter.TAGLINE, Theme.TEXT_SECONDARY);

        // Version and scanner info
        String version = "v" + CodeMeter.VERSION;
        String scanner = "Scanner: " + ScannerFactory.availableScannerName();
        Renderer.drawCentered(g, logo.length + 3, width,
                version + "  " + Theme.SEPARATOR + "  " + scanner, Theme.TEXT_MUTED);
    }

    // ── SCANNING SCREEN ──────────────────────────────────────

    private void renderScanning(TextGraphics g, TerminalSize size) {
        int w = size.getColumns();
        int h = size.getRows();

        Renderer.drawCentered(g, h / 2 - 4, w, "Scanning...", Theme.ACCENT_PRIMARY);
        Renderer.drawCentered(g, h / 2 - 2, w,
                Renderer.truncate(scanningPath, w - 10), Theme.TEXT_SECONDARY);

        // Progress bar
        int barWidth = Math.min(50, w - 20);
        int barX = (w - barWidth) / 2;
        int progress = scanProgress.get();
        Renderer.drawProgressBar(g, barX, h / 2, barWidth, progress, Theme.ACCENT_PRIMARY);
        Renderer.drawCentered(g, h / 2 + 1, w, progress + "%", Theme.TEXT_PRIMARY);

        // Animated spinner
        String[] spinner = {"⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"};
        int spinIdx = (int) ((System.currentTimeMillis() / 100) % spinner.length);
        Renderer.drawCentered(g, h / 2 + 3, w,
                spinner[spinIdx] + " Analyzing codebase...", Theme.ACCENT_CYAN);

        if (scanError != null) {
            Renderer.drawCentered(g, h / 2 + 5, w, "Error: " + scanError, Theme.ACCENT_DANGER);
            Renderer.drawCentered(g, h / 2 + 7, w, "Press any key to go back", Theme.TEXT_MUTED);
        }

        if (scanComplete.get()) {
            currentScreen = AppScreen.DASHBOARD;
            currentTab = DashboardTab.OVERVIEW;
            sidebarIndex = 0;
        }
    }

    // ── DASHBOARD ────────────────────────────────────────────

    private void renderDashboard(TextGraphics g, TerminalSize size) {
        int w = size.getColumns();
        int h = size.getRows();

        // Sidebar
        int sidebarWidth = Math.min(24, w / 4);
        renderSidebar(g, sidebarWidth, h);

        // Content area
        int contentX = sidebarWidth + 1;
        int contentWidth = w - sidebarWidth - 2;
        int contentHeight = h - 3; // Leave room for status bar

        // Separator
        g.setForegroundColor(Theme.BORDER);
        for (int y = 0; y < h - 2; y++) {
            g.putString(sidebarWidth, y, Theme.BOX_V);
        }

        // Render current tab content
        switch (currentTab) {
            case OVERVIEW -> OverviewPanel.render(g, contentX, 0, contentWidth, contentHeight, currentScanResult);
            case PHYSICAL -> PhysicalPanel.render(g, contentX, 0, contentWidth, contentHeight, currentPhysical, storage.getSettings(), scrollOffset);
            case PRINTED -> PrintedPanel.render(g, contentX, 0, contentWidth, contentHeight, currentPrinted, storage.getSettings(), scrollOffset);
            case GROWTH -> GrowthPanel.render(g, contentX, 0, contentWidth, contentHeight, storage.getProjectHistory(currentScanResult.projectPath()), scrollOffset);
            case HISTORY -> HistoryPanel.render(g, contentX, 0, contentWidth, contentHeight, storage.getGlobalHistory(), scrollOffset);
            case ACHIEVEMENTS -> AchievementsPanel.render(g, contentX, 0, contentWidth, contentHeight, storage.getAchievements(), scrollOffset);
            case COMPARISONS -> ComparisonsPanel.render(g, contentX, 0, contentWidth, contentHeight, currentScanResult, storage.getProjectHistory(currentScanResult.projectPath()), scrollOffset);
            case SETTINGS_TAB -> SettingsPanel.render(g, contentX, 0, contentWidth, contentHeight, storage.getSettings(), scrollOffset);
            case ABOUT_TAB -> AboutPanel.render(g, contentX, 0, contentWidth, contentHeight);
        }

        // Status bar
        renderStatusBar(g, w, h);
    }

    private void renderSidebar(TextGraphics g, int width, int height) {
        // Sidebar background
        Renderer.clearArea(g, 0, 0, width, height, Theme.SIDEBAR_BG);

        // Title
        g.setForegroundColor(Theme.ACCENT_PRIMARY);
        g.setBackgroundColor(Theme.SIDEBAR_BG);
        g.putString(2, 1, Theme.DIAMOND + " CodeMeter");

        // Project name
        if (currentScanResult != null) {
            g.setForegroundColor(Theme.TEXT_MUTED);
            g.putString(2, 2, Renderer.truncate(currentScanResult.projectName(), width - 4));
        }

        // Separator
        g.setForegroundColor(Theme.BORDER);
        g.putString(1, 3, Theme.BOX_H.repeat(width - 2));

        // Menu items
        for (int i = 0; i < SIDEBAR_ITEMS.length; i++) {
            int y = 5 + i * 2;
            if (y >= height - 2) break;

            boolean active = (i == sidebarIndex);

            if (active) {
                g.setBackgroundColor(Theme.BG_SELECTED);
                g.setForegroundColor(Theme.ACCENT_PRIMARY);
                g.putString(0, y, " ".repeat(width));
                g.putString(1, y, Theme.BLOCK_FULL);
                g.putString(3, y, SIDEBAR_ICONS[i] + " " + SIDEBAR_ITEMS[i]);
            } else {
                g.setBackgroundColor(Theme.SIDEBAR_BG);
                g.setForegroundColor(Theme.TEXT_SECONDARY);
                g.putString(3, y, SIDEBAR_ICONS[i] + " " + SIDEBAR_ITEMS[i]);
            }
        }

        g.setBackgroundColor(Theme.BG_PRIMARY);
    }

    private void renderStatusBar(TextGraphics g, int width, int height) {
        int y = height - 1;
        g.setBackgroundColor(Theme.BG_SECONDARY);
        g.putString(0, y, " ".repeat(width));

        g.setForegroundColor(Theme.TEXT_MUTED);
        g.setBackgroundColor(Theme.BG_SECONDARY);

        String statusLeft = " " + Theme.keyHint("↑↓", "Navigate") + "  " +
                Theme.keyHint("Enter", "Select") + "  " +
                Theme.keyHint("/", "Search") + "  " +
                Theme.keyHint("q", "Back/Quit");

        String statusRight = "CodeMeter v" + CodeMeter.VERSION + " ";

        g.putString(0, y, Renderer.truncate(statusLeft, width - statusRight.length() - 1));
        g.setForegroundColor(Theme.ACCENT_PRIMARY);
        g.putString(width - statusRight.length(), y, statusRight);

        g.setBackgroundColor(Theme.BG_PRIMARY);
    }

    private void renderFooter(TextGraphics g, int width, int height) {
        int y = height - 1;
        g.setBackgroundColor(Theme.BG_SECONDARY);
        g.putString(0, y, " ".repeat(width));
        g.setForegroundColor(Theme.TEXT_MUTED);
        g.putString(1, y, Theme.keyHint("↑↓", "Navigate") + "  " +
                Theme.keyHint("Enter", "Select") + "  " +
                Theme.keyHint("q", "Quit"));

        String info = storage.getProjectCount() + " projects " + Theme.SEPARATOR + " " +
                storage.getTotalScanCount() + " scans ";
        g.setForegroundColor(Theme.ACCENT_PRIMARY);
        g.putString(width - info.length() - 1, y, info);
        g.setBackgroundColor(Theme.BG_PRIMARY);
    }

    // ── SETTINGS SCREEN ──────────────────────────────────────

    private void renderSettings(TextGraphics g, TerminalSize size) {
        int w = size.getColumns();
        int h = size.getRows();
        Renderer.drawCentered(g, 1, w, "Settings", Theme.ACCENT_PRIMARY);
        SettingsPanel.render(g, 2, 3, w - 4, h - 5, storage.getSettings(), scrollOffset);
        renderFooter(g, w, h);
    }

    // ── ABOUT SCREEN ─────────────────────────────────────────

    private void renderAbout(TextGraphics g, TerminalSize size) {
        int w = size.getColumns();
        int h = size.getRows();
        AboutPanel.render(g, 2, 1, w - 4, h - 3);
        renderFooter(g, w, h);
    }

    // ── WRAPPED SCREEN ───────────────────────────────────────

    private void renderWrapped(TextGraphics g, TerminalSize size) {
        WrappedScreen.render(g, size, storage, wrappedPage);
    }

    // ── SEARCH ───────────────────────────────────────────────

    private void renderSearch(TextGraphics g, TerminalSize size) {
        int w = size.getColumns();
        int h = size.getRows();

        Renderer.clearArea(g, 0, 0, w, h, Theme.BG_PRIMARY);

        // Search box
        int boxWidth = Math.min(60, w - 10);
        int boxX = (w - boxWidth) / 2;

        Renderer.drawCard(g, boxX, 2, boxWidth, 3, "Search");
        g.setForegroundColor(Theme.TEXT_PRIMARY);
        g.setBackgroundColor(Theme.BG_CARD);
        g.putString(boxX + 2, 3, "/ " + searchQuery + "▌");

        // Search results would go here
        g.setForegroundColor(Theme.TEXT_MUTED);
        g.setBackgroundColor(Theme.BG_PRIMARY);
        Renderer.drawCentered(g, 7, w, "Type to search projects, settings, metrics...", Theme.TEXT_MUTED);
        Renderer.drawCentered(g, h - 2, w, "Press Esc to close", Theme.TEXT_MUTED);
    }

    // ── INPUT HANDLING ───────────────────────────────────────

    private void handleInput(KeyStroke key) {
        if (searchActive && currentScreen == AppScreen.SEARCH) {
            handleSearchInput(key);
            return;
        }

        switch (currentScreen) {
            case HOME -> handleHomeInput(key);
            case SCANNING -> handleScanningInput(key);
            case DASHBOARD -> handleDashboardInput(key);
            case SETTINGS -> handleSettingsInput(key);
            case WRAPPED -> handleWrappedInput(key);
            default -> handleGenericInput(key);
        }
    }

    private void handleHomeInput(KeyStroke key) {
        switch (key.getKeyType()) {
            case ArrowUp -> {
                homeSelectedIndex = Math.max(0, homeSelectedIndex - 1);
            }
            case ArrowDown -> {
                homeSelectedIndex = Math.min(HOME_ITEMS.length - 1, homeSelectedIndex + 1);
            }
            case Enter -> {
                activateHomeItem(homeSelectedIndex);
            }
            case Character -> {
                char c = key.getCharacter();
                if (c == 'q' || c == 'Q') {
                    running = false;
                } else if (c == '/') {
                    currentScreen = AppScreen.SEARCH;
                    searchActive = true;
                    searchQuery = "";
                } else if (c >= '1' && c <= '9') {
                    int idx = c - '1';
                    if (idx < HOME_ITEMS.length) {
                        homeSelectedIndex = idx;
                        activateHomeItem(idx);
                    }
                }
            }
            case Escape -> running = false;
            default -> {}
        }
    }

    private void activateHomeItem(int index) {
        switch (index) {
            case 0 -> startScan(Path.of(".").toAbsolutePath().normalize().toString());
            case 1 -> {
                // Scan another folder - for TUI, use current dir for now
                // In a full implementation, a file picker dialog would appear
                startScan(Path.of(".").toAbsolutePath().normalize().toString());
            }
            case 2 -> {
                // Resume last scan
                List<String> recent = storage.getRecentPaths();
                if (!recent.isEmpty()) {
                    startScan(recent.get(0));
                }
            }
            case 3 -> {
                // Global statistics - show dashboard with last result or empty
                if (!storage.getGlobalHistory().isEmpty()) {
                    currentScreen = AppScreen.DASHBOARD;
                    currentTab = DashboardTab.HISTORY;
                    sidebarIndex = 4;
                }
            }
            case 4 -> {
                // History
                currentScreen = AppScreen.DASHBOARD;
                currentTab = DashboardTab.HISTORY;
                sidebarIndex = 4;
            }
            case 5 -> {
                // Wrapped
                currentScreen = AppScreen.WRAPPED;
                wrappedPage = 0;
            }
            case 6 -> {
                // Achievements
                currentScreen = AppScreen.DASHBOARD;
                currentTab = DashboardTab.ACHIEVEMENTS;
                sidebarIndex = 5;
            }
            case 7 -> {
                // Settings
                currentScreen = AppScreen.SETTINGS;
            }
            case 8 -> {
                // About
                currentScreen = AppScreen.ABOUT;
            }
            case 9 -> running = false;
        }
    }

    private void handleDashboardInput(KeyStroke key) {
        switch (key.getKeyType()) {
            case ArrowUp -> {
                if (key.isAltDown()) {
                    // Alt+Up: scroll content
                    scrollOffset = Math.max(0, scrollOffset - 1);
                } else {
                    sidebarIndex = Math.max(0, sidebarIndex - 1);
                    updateTab();
                    scrollOffset = 0;
                }
            }
            case ArrowDown -> {
                if (key.isAltDown()) {
                    scrollOffset++;
                } else {
                    sidebarIndex = Math.min(SIDEBAR_ITEMS.length - 1, sidebarIndex + 1);
                    updateTab();
                    scrollOffset = 0;
                }
            }
            case PageUp -> scrollOffset = Math.max(0, scrollOffset - 10);
            case PageDown -> scrollOffset += 10;
            case Enter -> updateTab();
            case Character -> {
                char c = key.getCharacter();
                if (c == 'q' || c == 'Q') {
                    currentScreen = AppScreen.HOME;
                    scrollOffset = 0;
                } else if (c == '/') {
                    currentScreen = AppScreen.SEARCH;
                    searchActive = true;
                    searchQuery = "";
                } else if (c == 'j') {
                    scrollOffset++;
                } else if (c == 'k') {
                    scrollOffset = Math.max(0, scrollOffset - 1);
                } else if (c == 'J') {
                    sidebarIndex = Math.min(SIDEBAR_ITEMS.length - 1, sidebarIndex + 1);
                    updateTab();
                    scrollOffset = 0;
                } else if (c == 'K') {
                    sidebarIndex = Math.max(0, sidebarIndex - 1);
                    updateTab();
                    scrollOffset = 0;
                } else if (c >= '1' && c <= '9') {
                    int idx = c - '1';
                    if (idx < SIDEBAR_ITEMS.length) {
                        sidebarIndex = idx;
                        updateTab();
                        scrollOffset = 0;
                    }
                }
            }
            case Escape -> {
                currentScreen = AppScreen.HOME;
                scrollOffset = 0;
            }
            default -> {}
        }
    }

    private void handleScanningInput(KeyStroke key) {
        if (scanError != null || scanComplete.get()) {
            if (scanError != null) {
                currentScreen = AppScreen.HOME;
                scanError = null;
            }
        }
        if (key.getKeyType() == KeyType.Escape) {
            currentScreen = AppScreen.HOME;
        }
    }

    private void handleSettingsInput(KeyStroke key) {
        if (key.getKeyType() == KeyType.Escape || key.getKeyType() == KeyType.Character &&
                (key.getCharacter() == 'q' || key.getCharacter() == 'Q')) {
            currentScreen = AppScreen.HOME;
        }
    }

    private void handleWrappedInput(KeyStroke key) {
        switch (key.getKeyType()) {
            case ArrowRight, ArrowDown, Enter -> wrappedPage++;
            case ArrowLeft, ArrowUp -> wrappedPage = Math.max(0, wrappedPage - 1);
            case Escape -> currentScreen = AppScreen.HOME;
            case Character -> {
                char c = key.getCharacter();
                if (c == 'q' || c == 'Q') currentScreen = AppScreen.HOME;
                else if (c == ' ') wrappedPage++;
            }
            default -> {}
        }
    }

    private void handleSearchInput(KeyStroke key) {
        switch (key.getKeyType()) {
            case Escape -> {
                searchActive = false;
                currentScreen = currentScanResult != null ? AppScreen.DASHBOARD : AppScreen.HOME;
            }
            case Backspace -> {
                if (!searchQuery.isEmpty()) {
                    searchQuery = searchQuery.substring(0, searchQuery.length() - 1);
                }
            }
            case Character -> {
                searchQuery += key.getCharacter();
            }
            case Enter -> {
                searchActive = false;
                currentScreen = currentScanResult != null ? AppScreen.DASHBOARD : AppScreen.HOME;
            }
            default -> {}
        }
    }

    private void handleGenericInput(KeyStroke key) {
        if (key.getKeyType() == KeyType.Escape ||
                (key.getKeyType() == KeyType.Character &&
                        (key.getCharacter() == 'q' || key.getCharacter() == 'Q'))) {
            currentScreen = AppScreen.HOME;
        }
    }

    private void updateTab() {
        currentTab = DashboardTab.values()[sidebarIndex];
        // Recalculate metrics if switching to a tab that needs them
        if (currentScanResult != null) {
            if (currentTab == DashboardTab.PHYSICAL) {
                currentPhysical = PhysicalCalculator.calculate(currentScanResult, storage.getSettings());
            } else if (currentTab == DashboardTab.PRINTED) {
                currentPrinted = PrintCalculator.calculate(currentScanResult, storage.getSettings());
            }
        }
    }

    // ── SCANNING ─────────────────────────────────────────────

    private void startScan(String path) {
        scanningPath = path;
        scanProgress.set(0);
        scanComplete.set(false);
        scanError = null;
        currentScreen = AppScreen.SCANNING;

        CompletableFuture.runAsync(() -> {
            try {
                CodeScanner scanner = ScannerFactory.create();
                ScanResult result = scanner.scan(Path.of(path), progress -> {
                    scanProgress.set(progress);
                });

                currentScanResult = result;
                currentPhysical = PhysicalCalculator.calculate(result, storage.getSettings());
                currentPrinted = PrintCalculator.calculate(result, storage.getSettings());

                // Save to storage
                storage.addOrUpdateProject(Project.from(result));
                storage.addHistoryEntry(HistoryEntry.from(result));
                storage.addRecentPath(result.projectPath());
                List<Achievement> newAchievements = storage.checkAndUpdateAchievements(result);
                storage.save();

                scanComplete.set(true);
            } catch (ScanException e) {
                scanError = e.getMessage();
            }
        });
    }

    private void cleanup() {
        try {
            if (screen != null) {
                screen.stopScreen();
            }
            if (terminal != null) {
                terminal.close();
            }
        } catch (IOException e) {
            // Best effort cleanup
        }
    }
}
