import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class CharsetTest {
    public static void main(String[] args) throws Exception {
        System.out.println("Default Out: ━━━━━━━━━━━━━━━━━━━━━━");
        
        PrintStream utf8Out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        System.setOut(utf8Out);
        
        System.out.println("UTF-8 Out: ━━━━━━━━━━━━━━━━━━━━━━");
    }
}
