package dev.codemeter.tui;

import com.googlecode.lanterna.terminal.ansi.UnixTerminal;
import com.googlecode.lanterna.terminal.ansi.UnixLikeTerminal;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * A fallback terminal implementation for Windows environments where JNA is missing, 
 * GraalVM Native Image System.console() is null, or Lanterna's internal WindowsTerminal is missing.
 * This class bypasses stty.exe execution which normally causes crashes on standard Windows setups.
 */
public class WindowsNativeTerminal extends UnixTerminal {

    public WindowsNativeTerminal(InputStream in, OutputStream out, Charset charset) throws IOException {
        super(in, out, charset, UnixLikeTerminal.CtrlCBehaviour.CTRL_C_KILLS_APPLICATION);
    }

    @Override
    protected void saveTerminalSettings() throws IOException {
        // No-op for Windows
    }

    @Override
    protected void restoreTerminalSettings() throws IOException {
        // No-op for Windows
    }

    @Override
    protected String runSTTYCommand(String... command) throws IOException {
        // Bypasses execution of stty.exe
        return "";
    }
}
