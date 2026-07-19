package dev.codemeter;

import dev.codemeter.cli.CodeMeterCommand;
import picocli.CommandLine;

/**
 * CodeMeter — Measure your code. Physically.
 * 
 * Main entry point for the application.
 * Launches the interactive TUI by default, or processes CLI subcommands.
 */
public final class CodeMeter {

    public static final String NAME = "CodeMeter";
    public static final String VERSION = "1.0.0";
    public static final String TAGLINE = "Measure your code. Physically.";

    public static void main(String[] args) {
        // Force UTF-8 output to prevent Windows console encoding issues (e.g. replacing '━' with '?')
        try {
            System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
            System.setErr(new java.io.PrintStream(System.err, true, "UTF-8"));
        } catch (java.io.UnsupportedEncodingException e) {
            // Should never happen, UTF-8 is guaranteed by JVM
        }
        // Force headless mode to prevent AWT from trying to load native UI libraries
        // This is crucial for GraalVM Native Image on Windows to avoid UnsatisfiedLinkError
        System.setProperty("java.awt.headless", "true");

        int exitCode = new CommandLine(new CodeMeterCommand())
                .setCaseInsensitiveEnumValuesAllowed(true)
                .execute(args);
        System.exit(exitCode);
    }
}
