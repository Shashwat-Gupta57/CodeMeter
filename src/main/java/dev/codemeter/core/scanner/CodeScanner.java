package dev.codemeter.core.scanner;

import dev.codemeter.core.model.ScanResult;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * Interface for code scanning backends (scc, cloc).
 */
public interface CodeScanner {

    /**
     * Returns the name of this scanner (e.g., "scc", "cloc").
     */
    String name();

    /**
     * Checks if this scanner is available on the system.
     */
    boolean isAvailable();

    /**
     * Scans the given directory and returns results.
     *
     * @param directory the directory to scan
     * @param progressCallback optional callback for progress updates (percentage 0-100)
     * @return the scan result
     * @throws ScanException if the scan fails
     */
    ScanResult scan(Path directory, Consumer<Integer> progressCallback) throws ScanException;
}
