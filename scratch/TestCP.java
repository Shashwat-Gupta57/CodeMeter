import com.sun.jna.platform.win32.Kernel32;

public class TestCP {
    public static void main(String[] args) {
        try {
            Kernel32.INSTANCE.SetConsoleOutputCP(65001);
            Kernel32.INSTANCE.SetConsoleCP(65001);
            System.out.println("CP set to 65001");
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
