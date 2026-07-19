package dev.codemeter.cli;

import dev.codemeter.core.storage.StorageManager;
import picocli.CommandLine;
import picocli.CommandLine.IExecutionExceptionHandler;
import picocli.CommandLine.IParameterExceptionHandler;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.ParseResult;

import java.io.PrintWriter;

public class CodeMeterExceptionHandler implements IExecutionExceptionHandler, IParameterExceptionHandler {

    public static final int EXIT_SUCCESS = 0;
    public static final int EXIT_RUNTIME_ERROR = 1;
    public static final int EXIT_INVALID_ARGS = 2;
    public static final int EXIT_CONFIG_ERROR = 3;
    public static final int EXIT_SCANNER_FAILURE = 4;

    private void printError(String title, String message, String suggestion) {
        System.err.println();
        System.err.println("Could not " + title);
        System.err.println();
        System.err.println(message);
        System.err.println();
        System.err.println("Suggestions");
        if (suggestion != null && !suggestion.isEmpty()) {
            for (String s : suggestion.split("\n")) {
                System.err.println("• " + s);
            }
        }
        System.err.println("• Run\n\n  codemeter doctor\n\n  to verify your installation.");
        System.err.println();
    }

    @Override
    public int handleExecutionException(Exception ex, CommandLine commandLine, ParseResult parseResult) {
        if (ex instanceof IllegalArgumentException) {
            printError("execute command", ex.getMessage(), "Check the arguments provided.");
            return EXIT_INVALID_ARGS;
        } else if (ex.getClass().getName().contains("Config") || ex.getClass().getName().contains("Settings")) {
            printError("load configuration", ex.getMessage(), "Run\n\n  codemeter config\n\n  to reset or adjust settings.");
            return EXIT_CONFIG_ERROR;
        } else if (ex.getClass().getName().contains("Scan") || ex.getMessage() != null && ex.getMessage().contains("scan")) {
            printError("scan the repository", ex.getMessage(), "Ensure SCC is installed and accessible in your PATH.");
            return EXIT_SCANNER_FAILURE;
        } else {
            printError("execute command", ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred.", null);
            return EXIT_RUNTIME_ERROR;
        }
    }

    @Override
    public int handleParseException(ParameterException ex, String[] args) {
        printError("parse arguments", ex.getMessage(), "Run\n\n  codemeter --help\n\n  to see valid arguments and usage.");
        return EXIT_INVALID_ARGS;
    }
}
