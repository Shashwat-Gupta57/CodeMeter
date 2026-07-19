package dev.codemeter.cli;

import dev.codemeter.CodeMeter;
import dev.codemeter.shell.ShellApp;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * Main CLI command for CodeMeter.
 * When invoked without subcommands, launches the interactive Shell.
 */
@Command(
        name = "codemeter",
        description = "Measure your code. Physically.",
        version = CodeMeter.VERSION,
        mixinStandardHelpOptions = true,
        subcommands = {
                ScanCommand.class,
                ExportCommand.class,
                WrappedCommand.class
        }
)
public class CodeMeterCommand implements Runnable {

    @Override
    public void run() {
        // Default action: launch the Shell
        ShellApp app = new ShellApp();
        app.run();
    }
}
