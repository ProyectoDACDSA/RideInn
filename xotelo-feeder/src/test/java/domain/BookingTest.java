package domain;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

public class BookingTest {
    @Test
    public void testBookingCreation() {
        Hotel hotel = new Hotel(
                "Grand Hotel", "key123", 100, 200, 4.5,
                "Hotel", "http://example.com", "Paris"
        );
        LocalDate today = LocalDate.now();
        Booking booking = new Booking(12345L, "Xotelo", hotel, today, 3);

        assertEquals("Grand Hotel", booking.getHotelName());
        assertEquals(150, booking.getAveragePricePerNight()); // (100+200)/2
        assertEquals(today.plusDays(3), booking.getEndDate());
        assertTrue(booking.toJson().contains("\"hotelName\":\"Grand Hotel\""));
    }
}
