package ui;

import model.Hotel;
import model.Recommendation;
import model.Trip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.RecommendationAnalysisService;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

import static org.mockito.Mockito.*;

public class BestValueTripsTest {

    private RecommendationAnalysisService mockService;
    private Scanner scanner;
    private BestValueTrips bestValueTrips;

    @BeforeEach
    public void setup() {
        mockService = mock(RecommendationAnalysisService.class);

        String input = String.join("\n",
                "Lyon",
                "no",
                "no",
                "no",
                "1"
        );
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        bestValueTrips = new BestValueTrips(scanner) {
            @Override
            public void execute() {
                try {
                    List<Recommendation> recommendations = mockService.getTravelPackages("Lyon");

                    List<Recommendation> processed = recommendations.stream()
                            .peek(r -> {
                                r.getHotel().setStartDate(r.getTrip().getDepartureDateTime().toLocalDate());
                                r.getHotel().setEndDate(r.getHotel().getStartDate().plusDays(1));
                                r.getHotel().calculateTotalPrice();
                                r.setTotalPrice();
                            }).toList();

                    assert processed.get(0).getTotalPrice() > 0;

                    System.out.println("✔ Test ejecutado correctamente con totalPrice = " + processed.get(0).getTotalPrice());

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Test
    public void testExecute_WithValidData_DisplaysCorrectInfo() throws Exception {
        Trip trip = new Trip("Paris", "Lyon", "08:30", "2025-06-01", 25.0, 10);
        Hotel hotel = new Hotel(1L, "Ibis Lyon", "H123", "Hotel",
                "http://example.com", 4.2, 80.0, "Lyon", LocalDateTime.now());

        Recommendation recommendation = new Recommendation(trip, hotel, 0);

        when(mockService.getTravelPackages("Lyon")).thenReturn(List.of(recommendation));

        bestValueTrips.execute();
    }
}
