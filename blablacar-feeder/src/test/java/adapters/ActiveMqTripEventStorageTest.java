package adapters;

import domain.Trip;
import domain.TripEvent;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ActiveMqTripEventStorageTest {

    @Test
    public void testStoreDoesNotThrow() {
        ActiveMqTripEventStorage storage = new ActiveMqTripEventStorage();
        Trip trip = new Trip(1L, null, null, null, true, 1000, "EUR", 1, "Paris", 2, "Lyon", null);
        TripEvent event = new TripEvent(System.currentTimeMillis(), "test", trip);

        assertDoesNotThrow(() -> storage.store(event));
    }
}
