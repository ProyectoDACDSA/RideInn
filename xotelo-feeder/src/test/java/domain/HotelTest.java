package domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HotelTest {

    @Test
    void constructor_ShouldSetAllFields() {
        Hotel hotel = new Hotel("Test Hotel", "test123", 100, 200, 4.5, "Hotel", "http://test.com", "Paris");

        assertEquals("Test Hotel", hotel.name());
        assertEquals("test123", hotel.key());
        assertEquals(100, hotel.priceMin());
        assertEquals(200, hotel.priceMax());
        assertEquals(4.5, hotel.rating());
        assertEquals("Hotel", hotel.accommodationType());
        assertEquals("http://test.com", hotel.url());
        assertEquals("Paris", hotel.city());
    }
}