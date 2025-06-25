package domain.model;

import domain.model.Trip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;

class TripTest {
    private Trip trip;

    @BeforeEach
    void setUp() {
        trip = new Trip(
                "Paris",
                "Lyon",
                "15:30",
                "2025-06-01",
                19.99,
                3
        );
        trip.setId(42L);
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals("Paris", trip.getOrigin());
        assertEquals("Lyon", trip.getDestination());
        assertEquals(19.99, trip.getPrice(), 0.001);
        assertEquals(3, trip.getAvailable());
        assertEquals(LocalTime.of(15, 30), trip.getDepartureTime());
        assertEquals(LocalDate.of(2025, 6, 1), trip.getDepartureDate());
    }

    @Test
    void testSetIdAndGetId() {
        assertEquals(42L, trip.getId());
        trip.setId(100L);
        assertEquals(100L, trip.getId());
    }

    @Test
    void testDepartureDateTime() {
        LocalDateTime expected = LocalDateTime.of(2025, 6, 1, 15, 30);
        assertEquals(expected, trip.getDepartureDateTime());
    }
}

