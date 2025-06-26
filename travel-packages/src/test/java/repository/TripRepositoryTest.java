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
                "Madrid",
                "Barcelona",
                "10:00",
                "2025-07-15",
                45.5,
                3
        );

        assertNotNull(trip);
        assertEquals("Madrid", trip.getOrigin());
        assertEquals("Barcelona", trip.getDestination());
    }
}
