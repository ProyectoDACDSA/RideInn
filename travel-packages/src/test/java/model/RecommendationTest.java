package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RecommendationTest {

    private Trip trip;
    private Hotel hotel;
    private Recommendation recommendation;

    @BeforeEach
    void setUp() {
        trip = new Trip("Paris", "Lyon", "10:00", "2025-07-01", 25.0, 2);
        trip.setId(1L);

        hotel = new Hotel(1L, "Hotel Lyon", "H123", "Hotel",
                "http://example.com", 4.5, 50.0,
                "Lyon", LocalDateTime.now());

        hotel.setStartDate(LocalDate.of(2025, 7, 1));
        hotel.setEndDate(LocalDate.of(2025, 7, 3));
        hotel.setNights(2);
        hotel.calculateTotalPrice();

        recommendation = new Recommendation(trip, hotel, 125.0);
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(trip, recommendation.getTrip());
        assertEquals(hotel, recommendation.getHotel());
        assertEquals(125.0, recommendation.getTotalPrice(), 0.001);
    }

    @Test
    void testSetTotalPrice() {
        recommendation.setTotalPrice();
        assertEquals(125.0, recommendation.getTotalPrice(), 0.001);
    }

    @Test
    void testSetTotalPriceWithChangedHotel() {
        hotel.setNights(3);
        hotel.calculateTotalPrice();
        recommendation.setTotalPrice();
        assertEquals(175.0, recommendation.getTotalPrice(), 0.001);
    }

    @Test
    void testToStringContainsAllFields() {
        String str = recommendation.toString();
        assertTrue(str.contains("Recomendación:"));
        assertTrue(str.contains("Trip[from=Paris"));
        assertTrue(str.contains("Hotel"));
        assertTrue(str.contains("Precio total:"));
    }
}

