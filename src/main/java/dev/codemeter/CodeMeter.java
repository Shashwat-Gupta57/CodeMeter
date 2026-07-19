package dev.codemeter;

import dev.codemeter.cli.CodeMeterCommand;
import picocli.CommandLine;

/**
 * CodeMeter — Measure your code. Physically.
 * 
 * Main entry point for the application.
 * Parses CLI subcommands via Picocli and delegates to the appropriate handler.
 */
public final class CodeMeter {

    public static final String NAME = "CodeMeter";
    public static final String VERSION = "2.5.2";
    public static final String TAGLINE = "Measure your code. Physically.";

    public static void main(String[] args) {
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
