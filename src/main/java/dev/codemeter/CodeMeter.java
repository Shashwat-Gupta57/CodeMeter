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
        int exitCode = new CommandLine(new CodeMeterCommand())
                .setCaseInsensitiveEnumValuesAllowed(true)
                .execute(args);
        System.exit(exitCode);
    }
}
