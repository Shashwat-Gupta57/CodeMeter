import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

public class TestLanterna {
    public static void main(String[] args) {
        try {
            DefaultTerminalFactory factory = new DefaultTerminalFactory();
            Terminal t = factory.createTerminal();
            System.out.println("Success! Class: " + t.getClass().getName());
        } catch (Exception e) {
            System.out.println("Failed: " + e.getMessage());
        }
    }
}
