package domain;

import org.junit.jupiter.api.Test;
import java.time.ZonedDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class TripTest {

    @Test
    public void testTripRecord() {
        ZonedDateTime now = ZonedDateTime.now();
        Trip trip = new Trip(1L, now, now, now, true, 1000, "EUR", 1, "Paris", 2, "Lyon", now);

        assertEquals(1L, trip.id());
        assertEquals("Paris", trip.originCity());
        assertEquals("Lyon", trip.destinationCity());
    }
}