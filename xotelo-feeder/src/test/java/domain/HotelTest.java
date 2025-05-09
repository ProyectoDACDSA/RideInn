package domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class HotelTest {
    @Test
    public void testHotelCreation() {
        Hotel hotel = new Hotel(
                "Grand Hotel", "key123", 100, 200, 4.5,
                "Hotel", "http://example.com", "Paris"
        );

        assertEquals("Grand Hotel", hotel.getName());
        assertEquals(100, hotel.getPriceMin());
        assertEquals(4.5, hotel.getRating());
        assertEquals("Paris", hotel.getCity());
    }
}
