package infrastructure;

import domain.model.Hotel;
import domain.model.Trip;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class ActiveMqConsumerTest {

    @Test
    public void testParseTrip() throws Exception {
        String json = """
            {
                "origin": "Paris",
                "destination": "Niza",
                "departureTime": "2025-06-25T15:30:00",
                "price": 45.50,
                "available": 3
            }
            """;

        ActiveMqConsumer consumer = new ActiveMqConsumer();

        Method parseTrip = ActiveMqConsumer.class.getDeclaredMethod("parseTrip", String.class);
        parseTrip.setAccessible(true);

        Trip trip = (Trip) parseTrip.invoke(consumer, json);

        assertNotNull(trip);
        assertEquals("Paris", trip.getOrigin());
        assertEquals("Niza", trip.getDestination());
        assertEquals(45.50, trip.getPrice());
        assertEquals(3, trip.getAvailable());
        assertEquals("15:30", trip.getDepartureTime().toString());
        assertEquals("2025-06-25", trip.getDepartureDate().toString());
    }

    @Test
    public void testParseHotel() throws Exception {
        String json = """
            {
                "id": 123,
                "hotelName": "Hotel du Centre",
                "key": "HC123",
                "accommodationType": "Hotel",
                "url": "http://hotelducentre.example.com",
                "rating": 4.3,
                "averagePricePerNight": 120.0,
                "city": "Niza",
                "timestamp": "2025-06-24T10:15:30"
            }
            """;

        ActiveMqConsumer consumer = new ActiveMqConsumer();

        Method parseHotel = ActiveMqConsumer.class.getDeclaredMethod("parseHotel", String.class);
        parseHotel.setAccessible(true);

        Hotel hotel = (Hotel) parseHotel.invoke(consumer, json);

        assertNotNull(hotel);
        assertEquals(123L, hotel.getId());
        assertEquals("Hotel du Centre", hotel.getHotelName());
        assertEquals("Niza", hotel.getCity());
        assertEquals(4.3, hotel.getRating());
        assertEquals(120.0, hotel.getAveragePricePerNight());
    }
}