package dev.codemeter.cli;

import com.sun.jna.Library;
import com.sun.jna.Native;

public class WindowsConsoleSetup {
    public interface Kernel32 extends Library {
        Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class);
        boolean SetConsoleOutputCP(int wCodePageID);
    }

    public static void enableUTF8() {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            try {
                Kernel32.INSTANCE.SetConsoleOutputCP(65001); // UTF-8 Codepage
            } catch (Throwable t) {
                // Silently ignore if JNA fails or kernel32 isn't available (e.g., in some IDEs or non-Windows shells)
            }
        }
    }
}
