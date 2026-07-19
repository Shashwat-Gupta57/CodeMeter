package dev.codemeter.cli;

public class TerminalCapabilities {
    
    /**
     * Determines if the current terminal environment likely supports rich Unicode characters and Emojis.
     */
    public static boolean supportsUnicode() {
        String os = System.getProperty("os.name").toLowerCase();
        
        // macOS and Linux natively support Unicode in modern terminals.
        if (!os.contains("win")) {
            String lang = System.getenv("LANG");
            // If LANG isn't set, modern Unix systems still default to UTF-8
            return lang == null || lang.toUpperCase().contains("UTF-8");
        }
        
        // Windows specific checks
        
        // 1. Windows Terminal explicitly supports rich Unicode and Emojis
        if (System.getenv("WT_SESSION") != null) {
            return true;
        }
        
        // 2. Mintty / Git Bash / Cygwin
        String term = System.getenv("TERM");
        if (term != null && (term.contains("xterm") || term.contains("cygwin") || term.contains("rxvt"))) {
            return true;
        }
        
        // 3. IntelliJ IDEA / JetBrains consoles
        if (System.getenv("IDEA_INITIAL_DIRECTORY") != null) {
            return true;
        }
        
        // 4. ConEmu / Cmder
        if (System.getenv("ConEmuPID") != null) {
            return true;
        }
        
        // 5. Windows codepage 65001 (UTF-8)
        // If file.encoding was overridden via JVM args (e.g. -Dfile.encoding=UTF-8)
        // Note: Java 18+ defaults to UTF-8 everywhere, but the Windows Console itself might still be CP437.
        // Without JNA to call GetConsoleOutputCP(), we use environment hints.
        
        // For legacy cmd.exe or standard PowerShell without WT_SESSION, 
        // they notoriously fail at rendering Emojis and Box Drawing characters natively.
        return false;
    }
}
