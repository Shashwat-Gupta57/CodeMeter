package com.googlecode.lanterna.terminal.ansi;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.io.IOException;

public class WindowsAnsiTerminal extends UnixLikeTTYTerminal {
    public WindowsAnsiTerminal(InputStream in, OutputStream out, Charset charset) throws IOException {
        super(in, out, charset, UnixLikeTerminal.CtrlCBehaviour.CTRL_C_KILLS_APPLICATION);
    }
    @Override
    protected void saveTerminalSettings() throws IOException {}
    @Override
    protected void restoreTerminalSettings() throws IOException {}
    @Override
    protected void registerTerminal() throws IOException {}
}
