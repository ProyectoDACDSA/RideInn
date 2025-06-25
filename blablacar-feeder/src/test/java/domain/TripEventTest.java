package domain;

import org.junit.jupiter.api.Test;
import java.time.ZonedDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class TripEventTest {

    @Test
    public void testTripEventCreation() {
        ZonedDateTime now = ZonedDateTime.now();
        Trip trip = new Trip(1L, now, now, now, true, 1000, "EUR", 1, "Paris", 2, "Lyon", now);
        TripEvent event = new TripEvent(12345L, "test", trip);

        assertEquals("Paris", event.getOriginCity());
        assertEquals("Lyon", event.getDestinationCity());
        assertTrue(event.isAvailable());
        assertEquals(10.0f, event.getPrice());
    }
}
