package domain;

import static org.junit.jupiter.api.Assertions.*;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;

public class TripTest {
    @Test
    public void testTripCreation() {
        ZonedDateTime now = ZonedDateTime.now();
        Trip trip = new Trip(12345L,
                now,
                now.plusHours(4),
                now,
                true,
                2550,
                "EUR",
                90,
                "Paris",
                137,
                "Lyon",
                now);
        TripEvent tripEvent = new TripEvent(
                12345L,
                "blablacar",
                trip
        );

        assertEquals("Paris", tripEvent.getOriginCity());
        assertEquals("Lyon", tripEvent.getDestinationCity());
        assertEquals(25.50, tripEvent.getPrice() / 100.0);
        assertTrue(tripEvent.isAvailable());
    }
}
