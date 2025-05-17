import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class CLITest {

    @Test
    public void testMenuOptionExit() {
        String input = "0\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        CLI.main(new String[]{});

        String output = out.toString();
        assert output.contains("Saliendo del programa");
    }

    @Test
    public void testMenuOptionBestValueTripsThenExit() {
        String input = String.join("\n",
                "2",
                "Paris",
                "no",
                "no",
                "no",
                "5",
                "0"
        ) + "\n";

        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        CLI.main(new String[]{});

        String output = out.toString();
        assert output.contains("VIAJES CON MEJOR RELACIÓN CALIDAD-PRECIO");
        assert output.contains("Saliendo del programa");
    }
}
