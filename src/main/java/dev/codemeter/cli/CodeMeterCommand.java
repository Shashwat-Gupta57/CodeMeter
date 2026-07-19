package dev.codemeter.cli;

import dev.codemeter.CodeMeter;

import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * Main CLI command for CodeMeter.
 * When invoked without subcommands, it prints the help menu.
 */
@Command(
        name = "codemeter",
        description = "Measure your code. Physically.",
        version = CodeMeter.VERSION,
        mixinStandardHelpOptions = true,
        subcommands = {
                ScanCommand.class,
                ConfigCommand.class,
                ExportCommand.class,
                WrappedCommand.class,
                CompareCommand.class,
                HistoryCommand.class,
                DoctorCommand.class,
                MilestonesCommand.class,
                StatsCommand.class,
                BenchmarkCommand.class
        }
)
public class CodeMeterCommand implements Runnable {
    @picocli.CommandLine.Spec
    picocli.CommandLine.Model.CommandSpec spec;

    @Override
    public void run() {
        dev.codemeter.CodeMeter.printCustomHelp();
    }
}
