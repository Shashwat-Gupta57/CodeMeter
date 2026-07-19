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
        
        // Force Windows Console to actually decode the UTF-8 bytes we send it
        dev.codemeter.cli.WindowsConsoleSetup.enableUTF8();
        // Force headless mode to prevent AWT from trying to load native UI libraries
        // This is crucial for GraalVM Native Image on Windows to avoid UnsatisfiedLinkError
        System.setProperty("java.awt.headless", "true");

        CommandLine cmd = new CommandLine(new CodeMeterCommand());
        cmd.setCaseInsensitiveEnumValuesAllowed(true);
        
        dev.codemeter.cli.CodeMeterExceptionHandler handler = new dev.codemeter.cli.CodeMeterExceptionHandler();
        cmd.setExecutionExceptionHandler(handler);
        cmd.setParameterExceptionHandler(handler);
        
        // Manual Help Override
        if (args.length == 1 && (args[0].equals("-h") || args[0].equals("--help"))) {
            printCustomHelp();
            System.exit(0);
        }
        
        int exitCode = cmd.execute(args);
        System.exit(exitCode);
    }
    
    public static void printCustomHelp() {
        String div = "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n";
        System.out.print(div);
        System.out.print("CodeMeter\n\nMeasure your code. Physically.\n\n");
        System.out.print(div);
        System.out.print("USAGE\n\ncodemeter <command>\n\n");
        System.out.print(div);
        System.out.print("STORY\n\nscan\n\nwrapped\n\ncompare\n\nhistory\n\n");
        System.out.print(div);
        System.out.print("TOOLS\n\nconfig\n\ndoctor\n\nexport\n\nmilestones\n\nstats\n\nbenchmark\n\n");
        System.out.print(div);
        System.out.print("OPTIONS\n\n-h\n\n--help\n\n-V\n\n--version\n\n");
        System.out.print(div);
        System.out.print("Run\n\ncodemeter scan .\n\nto generate your first Story.\n");
    }
}
