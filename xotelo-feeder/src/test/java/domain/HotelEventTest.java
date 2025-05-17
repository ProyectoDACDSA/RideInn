package domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HotelEventTest {

    @Test
    public void testBookingCreation() {
        Hotel hotel = new Hotel(
                "Grand Hotel", "key123", 100, 200, 4.5,
                "Hotel", "http://example.com", "Paris"
        );

        HotelEvent event = new HotelEvent(12345L, "Xotelo", hotel);

        assertEquals(12345L, event.getTs());
        assertEquals("Xotelo", event.getSs());
        assertEquals("Grand Hotel", event.getHotelName());
        assertEquals("key123", event.getKey());
        assertEquals("Hotel", event.getAccommodationType());
        assertEquals("http://example.com", event.getUrl());
        assertEquals(4.5, event.getRating(), 0.01);
        assertEquals(150, event.getAveragePricePerNight()); // (100 + 200) / 2
        assertEquals("Paris", event.getCity());

        String json = event.toJson();
        assertTrue(json.contains("\"hotelName\":\"Grand Hotel\""));
        assertTrue(json.contains("\"key\":\"key123\""));
        assertTrue(json.contains("\"rating\":4.5"));
    }
}
