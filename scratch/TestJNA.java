import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;

public class TestJNA {
    public static void main(String[] args) {
        HANDLE hConsole = Kernel32.INSTANCE.GetStdHandle(Kernel32.STD_INPUT_HANDLE);
        IntByReference mode = new IntByReference();
        Kernel32.INSTANCE.GetConsoleMode(hConsole, mode);
        System.out.println("Input Mode: " + mode.getValue());
    }
}
