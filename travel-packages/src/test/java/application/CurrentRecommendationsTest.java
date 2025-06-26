package application;

import domain.model.Recommendation;
import domain.model.Trip;
import domain.model.Hotel;
import domain.ports.RecommendationInputPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.*;

class CurrentRecommendationsTest {
    private ByteArrayOutputStream outputStream;
    private RecommendationInputPort testService;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        testService = new RecommendationInputPort() {
            @Override
            public List<Recommendation> getTravelPackages(String city) throws SQLException {
                if (city.equals("Paris")) {
                    Trip trip = new Trip("Lyon", "Paris", "10:00:00",
                            "2023-12-15", 120.0, true);
                    Hotel hotel = new Hotel(1L, "Hotel Paris", "paris-1", "Hotel",
                            "http://hotel.com", 4.5, 80.0, "Paris",
                            LocalDateTime.now());
                    return List.of(new Recommendation(trip, hotel, 200.0));
                }
                return List.of();
            }
        };
    }

    @Test
    void testExecuteWithFilters() throws SQLException {
        provideInput("Paris\nsi\nLyon\nsi\n15/12/2023\n2\nsi\n100\n300\n");

        CurrentRecommendations cr = new CurrentRecommendations(
                new Scanner(System.in), testService);
        cr.execute();

        String output = outputStream.toString();
        assertTrue(output.contains("Origen: LYON"));
        assertTrue(output.contains("Precio: 100.0€ - 300.0€"));
    }

    @Test
    void testExecuteWithNoResults() throws SQLException {
        provideInput("Lyon\nno\nno\nno\nno\n");

        CurrentRecommendations cr = new CurrentRecommendations(
                new Scanner(System.in), testService);
        cr.execute();

        String output = outputStream.toString();
        assertTrue(output.contains("No se encontraron recomendaciones"));
    }

    private void provideInput(String data) {
        System.setIn(new ByteArrayInputStream(data.getBytes()));
    }
}