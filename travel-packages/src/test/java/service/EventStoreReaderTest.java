package service;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;


import static org.junit.jupiter.api.Assertions.*;

public class EventStoreReaderTest {

    @Test
    public void testConstructorCreatesGson() {
        EventStoreReader reader = new EventStoreReader("path1", "path2");
        assertNotNull(reader);
    }

    @Test
    public void testProcessTripLine_validJson_noException() throws Exception {
        String jsonTrip = "{\"origin\":\"Paris\",\"destination\":\"Lyon\",\"departureTime\":\"2025-05-17T15:30:00\",\"price\":50.0,\"available\":3}";

        EventStoreReader reader = new EventStoreReader("dummyPath", "dummyPath");

        Method method = EventStoreReader.class.getDeclaredMethod("processTripLine", String.class);
        method.setAccessible(true);

        assertDoesNotThrow(() -> method.invoke(reader, jsonTrip));
    }

    @Test
    public void testProcessTripLine_invalidJson_logsError() throws Exception {
        String invalidJson = "{bad json}";

        EventStoreReader reader = new EventStoreReader("dummyPath", "dummyPath");

        Method method = EventStoreReader.class.getDeclaredMethod("processTripLine", String.class);
        method.setAccessible(true);

        assertDoesNotThrow(() -> method.invoke(reader, invalidJson));
    }

    @Test
    public void testProcessHotelLine_validJson_noException() throws Exception {
        String jsonHotel = "{\"name\":\"Hotel Paris\",\"city\":\"Paris\",\"availableRooms\":5,\"pricePerNight\":120.0}";

        EventStoreReader reader = new EventStoreReader("dummyPath", "dummyPath");

        Method method = EventStoreReader.class.getDeclaredMethod("processHotelLine", String.class);
        method.setAccessible(true);

        assertDoesNotThrow(() -> method.invoke(reader, jsonHotel));
    }

    @Test
    public void testProcessHotelLine_invalidJson_logsError() throws Exception {
        String invalidJson = "{not a json}";

        EventStoreReader reader = new EventStoreReader("dummyPath", "dummyPath");

        Method method = EventStoreReader.class.getDeclaredMethod("processHotelLine", String.class);
        method.setAccessible(true);

        assertDoesNotThrow(() -> method.invoke(reader, invalidJson));
    }

    @Test
    public void testProcessAllHistoricalEvents_noDirs_logsWarnings() {
        EventStoreReader reader = new EventStoreReader("nonexistent/path1", "nonexistent/path2");

        assertDoesNotThrow(reader::processAllHistoricalEvents);
    }
}

