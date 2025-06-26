package application;

import domain.model.Recommendation;
import domain.model.Trip;
import domain.model.Hotel;
import domain.ports.RecommendationInputPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class BestValueTripsTest {
    private RecommendationInputPort recommendationService;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        recommendationService = city -> city.equalsIgnoreCase("Paris") ? mockRecommendations() : new ArrayList<>();
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void testValueRatingInOutput() {
        runWithInput("Paris\nno\nno\nno\n3\n");
        String output = outputStream.toString();
        assertAll(
                () -> assertTrue(output.contains("★ Excelente")),
                () -> assertTrue(output.contains("▲ Bueno")),
                () -> assertTrue(output.contains("▼ Regular")),
                () -> {
                    int excelente = output.indexOf("★ Excelente");
                    int bueno = output.indexOf("▲ Bueno");
                    int regular = output.indexOf("▼ Regular");
                    assertTrue(excelente < bueno && bueno < regular,
                            "Resultados deben estar ordenados por mejor ratio primero");
                }
        );
    }

    @Test
    void testTruncateFunctionalityInOutput() {
        runWithInput("Paris\nno\nno\nno\n1\n");
        assertTrue(outputStream.toString().contains("Excelente Hotel"));
    }

    private void runWithInput(String input) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        try {
            new BestValueTrips(scanner, recommendationService).execute();
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    private List<Recommendation> mockRecommendations() {
        return Arrays.asList(
                new Recommendation(
                        new Trip("Niza", "Paris", "10:00:00", "2023-12-15", 100.0, true),
                        new Hotel(1L, "Excelente Hotel", "paris-hotel-1", "Hotel",
                                "http://example.com", 4.5, 80.0, "Paris", LocalDateTime.now()),
                        180.0
                ),
                new Recommendation(
                        new Trip("Toulouse", "Paris", "12:00:00", "2023-12-15", 120.0, true),
                        new Hotel(2L, "Buen Hotel", "paris-hotel-2", "Hotel",
                                "http://example.com", 4.0, 100.0, "Paris", LocalDateTime.now()),
                        220.0
                )
        );
    }
}