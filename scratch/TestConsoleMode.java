import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;

public class TestConsoleMode {
    public static void main(String[] args) throws Exception {
        HANDLE hIn = Kernel32.INSTANCE.GetStdHandle(-10); // STD_INPUT_HANDLE
        IntByReference mode = new IntByReference();
        if (Kernel32.INSTANCE.GetConsoleMode(hIn, mode)) {
            System.out.println("Mode: " + mode.getValue());
            int ENABLE_LINE_INPUT = 0x0002;
            int ENABLE_ECHO_INPUT = 0x0004;
            int ENABLE_PROCESSED_INPUT = 0x0001;
            int newMode = mode.getValue() & ~ENABLE_LINE_INPUT & ~ENABLE_ECHO_INPUT & ~ENABLE_PROCESSED_INPUT;
            Kernel32.INSTANCE.SetConsoleMode(hIn, newMode);
            System.out.println("Disabled line input. Please type something:");
            int read = System.in.read();
            System.out.println("Read: " + read);
        } else {
            System.out.println("No console");
        }
    }
}
