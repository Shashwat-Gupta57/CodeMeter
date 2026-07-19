package dev.codemeter.core.scanner;

import java.util.Optional;

/**
 * Factory that selects the best available code scanner.
 * Prefers scc, falls back to cloc.
 */
public final class ScannerFactory {

    private ScannerFactory() {}

    /**
     * Returns the best available scanner.
     * Priority: scc > cloc.
     *
     * @return an available scanner
     * @throws ScanException if no scanner is available
     */
    public static CodeScanner create() throws ScanException {
        SccScanner scc = new SccScanner();
        if (scc.isAvailable()) {
            return scc;
        }

        ClocScanner cloc = new ClocScanner();
        if (cloc.isAvailable()) {
            return cloc;
        }

        throw new ScanException(
                "No code scanner found. Please install scc or cloc.\n" +
                "  scc:  https://github.com/boyter/scc\n" +
                "  cloc: https://github.com/AlDanial/cloc"
        );
    }

    /**
     * Returns the preferred scanner if available.
     */
    public static Optional<CodeScanner> tryCreate() {
        try {
            return Optional.of(create());
        } catch (ScanException e) {
            return Optional.empty();
        }
    }

    /**
     * Returns the name of the available scanner, or "none".
     */
    public static String availableScannerName() {
        SccScanner scc = new SccScanner();
        if (scc.isAvailable()) return "scc";

        ClocScanner cloc = new ClocScanner();
        if (cloc.isAvailable()) return "cloc";

        return "none";
    }
}
