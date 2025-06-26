package repository;

import domain.model.Trip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TripRepositoryTest {

    @BeforeEach
    public void setUp() {
        TripRepository tripRepository = new TripRepository();
    }

    @Test
    public void testTripCreationAndRepositoryInstantiation() {
        Trip trip = new Trip(
                "Paris",
                "Niza",
                "10:00",
                "2025-07-15",
                45.5,
                true
        );

        assertNotNull(trip);
        assertEquals("Paris", trip.getOrigin());
        assertEquals("Niza", trip.getDestination());
    }
}
