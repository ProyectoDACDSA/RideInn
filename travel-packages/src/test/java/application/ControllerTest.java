package application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void testMainMenuDisplay() {
        provideInput("3\n");
        new Controller().start();

        String output = outputStream.toString();
        assertTrue(output.contains("=== MENÚ DE ANÁLISIS DE VIAJES ==="));
        assertTrue(output.contains("1. Recomendaciones Actuales"));
        assertTrue(output.contains("2. Viajes Mejor Valorados"));
        assertTrue(output.contains("3. Salir"));
    }

    @Test
    void testExitOption() {
        provideInput("3\n");
        new Controller().start();

        assertTrue(outputStream.toString().contains("Saliendo del programa..."));
    }

    @Test
    void testInvalidOption() {
        provideInput("4\n3\n");
        new Controller().start();

        assertTrue(outputStream.toString().contains("Opción inválida. Intente de nuevo."));
    }

    private void provideInput(String data) {
        System.setIn(new ByteArrayInputStream(data.getBytes()));
    }
}