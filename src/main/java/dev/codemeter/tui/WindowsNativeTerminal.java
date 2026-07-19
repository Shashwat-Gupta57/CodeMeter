package dev.codemeter.tui;

import com.googlecode.lanterna.terminal.ansi.UnixTerminal;
import com.googlecode.lanterna.terminal.ansi.UnixLikeTerminal;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;

/**
 * A fallback terminal implementation for Windows environments where JNA is missing, 
 * GraalVM Native Image System.console() is null, or Lanterna's internal WindowsTerminal is missing.
 * This class bypasses stty.exe execution which normally causes crashes on standard Windows setups.
 */
public class WindowsNativeTerminal extends UnixTerminal {

    private int originalInputMode;
    private int originalOutputMode;
    private boolean hasConsole = false;

    public WindowsNativeTerminal(InputStream in, OutputStream out, Charset charset) throws IOException {
        super(in, out, charset, UnixLikeTerminal.CtrlCBehaviour.CTRL_C_KILLS_APPLICATION);
        initWindowsConsole();
    }

    private void initWindowsConsole() {
        try {
            HANDLE hIn = Kernel32.INSTANCE.GetStdHandle(Kernel32.STD_INPUT_HANDLE);
            HANDLE hOut = Kernel32.INSTANCE.GetStdHandle(Kernel32.STD_OUTPUT_HANDLE);
            
            if (Kernel32.INSTANCE.GetConsoleMode(hIn, new IntByReference())) {
                hasConsole = true;
            }
        } catch (Throwable t) {
            // JNA might not be available or fail, ignore
            hasConsole = false;
        }
    }

    @Override
    protected void saveTerminalSettings() throws IOException {
        if (!hasConsole) return;
        try {
            HANDLE hIn = Kernel32.INSTANCE.GetStdHandle(Kernel32.STD_INPUT_HANDLE);
            HANDLE hOut = Kernel32.INSTANCE.GetStdHandle(Kernel32.STD_OUTPUT_HANDLE);
            
            IntByReference inMode = new IntByReference();
            IntByReference outMode = new IntByReference();
            
            Kernel32.INSTANCE.GetConsoleMode(hIn, inMode);
            Kernel32.INSTANCE.GetConsoleMode(hOut, outMode);
            
            originalInputMode = inMode.getValue();
            originalOutputMode = outMode.getValue();
            
            // Enable Raw Mode for Input (Disable line buffering and echo)
            int ENABLE_LINE_INPUT = 0x0002;
            int ENABLE_ECHO_INPUT = 0x0004;
            int ENABLE_PROCESSED_INPUT = 0x0001;
            int ENABLE_VIRTUAL_TERMINAL_INPUT = 0x0200;
            
            int newInMode = originalInputMode & ~ENABLE_LINE_INPUT & ~ENABLE_ECHO_INPUT & ~ENABLE_PROCESSED_INPUT;
            newInMode |= ENABLE_VIRTUAL_TERMINAL_INPUT;
            
            Kernel32.INSTANCE.SetConsoleMode(hIn, newInMode);
            
            // Enable ANSI / VT Processing for Output
            int ENABLE_VIRTUAL_TERMINAL_PROCESSING = 0x0004;
            int DISABLE_NEWLINE_AUTO_RETURN = 0x0008;
            
            int newOutMode = originalOutputMode | ENABLE_VIRTUAL_TERMINAL_PROCESSING | DISABLE_NEWLINE_AUTO_RETURN;
            Kernel32.INSTANCE.SetConsoleMode(hOut, newOutMode);
        } catch (Throwable t) {
            // Ignore if JNA fails
        }
    }

    @Override
    protected void restoreTerminalSettings() throws IOException {
        if (!hasConsole) return;
        try {
            HANDLE hIn = Kernel32.INSTANCE.GetStdHandle(Kernel32.STD_INPUT_HANDLE);
            HANDLE hOut = Kernel32.INSTANCE.GetStdHandle(Kernel32.STD_OUTPUT_HANDLE);
            
            Kernel32.INSTANCE.SetConsoleMode(hIn, originalInputMode);
            Kernel32.INSTANCE.SetConsoleMode(hOut, originalOutputMode);
        } catch (Throwable t) {
            // Ignore if JNA fails
        }
    }

    @Override
    protected String runSTTYCommand(String... command) throws IOException {
        // Bypasses execution of stty.exe
        return "";
    }
}
