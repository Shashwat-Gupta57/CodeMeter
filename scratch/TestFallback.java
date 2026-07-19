package dev.codemeter;
import com.googlecode.lanterna.terminal.ansi.UnixTerminal;
import com.googlecode.lanterna.terminal.ansi.UnixLikeTerminal;
import com.googlecode.lanterna.terminal.Terminal;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.io.IOException;

public class TestFallback {
    public static void main(String[] args) throws Exception {
        Terminal t = new UnixTerminal(System.in, System.out, Charset.defaultCharset(), UnixLikeTerminal.CtrlCBehaviour.CTRL_C_KILLS_APPLICATION) {
            @Override protected void saveTerminalSettings() {}
            @Override protected void restoreTerminalSettings() {}
            @Override protected String runSTTYCommand(String... cmd) { return ""; }
        };
        System.out.println("Success! Terminal: " + t.getClass().getName());
    }
}
