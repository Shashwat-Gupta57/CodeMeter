package dev.codemeter.core.scanner;

/**
 * Exception thrown when a code scan fails.
 */
public class ScanException extends Exception {

    public ScanException(String message) {
        super(message);
    }

    public ScanException(String message, Throwable cause) {
        super(message, cause);
    }
}
