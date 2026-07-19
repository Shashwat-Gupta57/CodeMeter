package dev.codemeter.shell;

import dev.codemeter.CodeMeter;
import dev.codemeter.cli.CodeMeterCommand;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import picocli.CommandLine;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ShellApp {

    public void run() {
        try (Terminal terminal = TerminalBuilder.builder()
                .system(true)
                .build()) {

            LineReader reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .appName("CodeMeter")
                    .build();

            terminal.writer().println();
            terminal.writer().println(CommandLine.Help.Ansi.AUTO.string("@|bold,cyan CodeMeter Shell v" + CodeMeter.VERSION + "|@"));
            terminal.writer().println(CommandLine.Help.Ansi.AUTO.string("@|faint Type 'scan .' to measure your code, or 'help' for commands.|@"));
            terminal.writer().println();

            while (true) {
                String line;
                try {
                    line = reader.readLine(CommandLine.Help.Ansi.AUTO.string("@|bold,green CodeMeter > |@"));
                } catch (UserInterruptException | EndOfFileException e) {
                    break;
                }

                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("quit")) {
                    break;
                }

                if (line.equalsIgnoreCase("clear")) {
                    terminal.puts(org.jline.utils.InfoCmp.Capability.clear_screen);
                    terminal.flush();
                    continue;
                }

                // Execute using PicoCLI
                String[] args = parseArgs(line);
                
                // We use CodeMeterCommand without the root 'codemeter' name so 'scan' routes properly
                new CommandLine(new CodeMeterCommand())
                        .setCaseInsensitiveEnumValuesAllowed(true)
                        .execute(args);
                
                terminal.writer().println();
            }
        } catch (IOException e) {
            System.err.println("Failed to initialize CodeMeter Shell: " + e.getMessage());
        }
    }

    private String[] parseArgs(String line) {
        // Basic space splitting. A production shell might use JLine's org.jline.reader.Parser
        // to properly handle quotes, but for our simple commands this is sufficient.
        return line.split("\\s+");
    }
}
