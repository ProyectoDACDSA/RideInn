package ui;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CLITest {

    private final InputStream systemIn = System.in;
    private final PrintStream systemOut = System.out;
    private ByteArrayInputStream testIn;
    private ByteArrayOutputStream testOut;

    @BeforeEach
    public void setUpOutput() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
    }

    private void provideInput(String data) {
        testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }

    private String getOutput() {
        return testOut.toString();
    }

    @AfterEach
    public void restoreSystem() {
        System.setIn(systemIn);
        System.setOut(systemOut);
    }

    @Test
    public void testInvalidThenExitOption() {
        provideInput("0\n3\n");

        CLI.main(new String[]{});

        String output = getOutput();
        assertTrue(output.contains("Opción inválida"));
        assertTrue(output.contains("Saliendo del programa"));
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
                "3"
        ) + "\n";

        provideInput(input);

        CLI.main(new String[]{});

        String output = getOutput();
        assertTrue(output.contains("VIAJES CON MEJOR RELACIÓN CALIDAD-PRECIO"));
        assertTrue(output.contains("Saliendo del programa"));
    }
}