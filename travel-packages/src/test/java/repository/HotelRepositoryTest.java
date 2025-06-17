package repository;

import config.DatabaseConfig;
import model.Hotel;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HotelRepositoryTest {
    private HotelRepository repo;

    @BeforeAll
    void setupDB() throws SQLException {
        repo = new HotelRepository();
        try (Connection c = DatabaseConfig.getConnection();
             Statement s = c.createStatement()) {
            s.execute("""
                CREATE TABLE IF NOT EXISTS hotels (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    hotel_name TEXT NOT NULL,
                    hotel_key TEXT NOT NULL,
                    accommodation_type TEXT NOT NULL,
                    url TEXT NOT NULL,
                    rating DOUBLE,
                    avg_price_per_night DOUBLE NOT NULL,
                    city TEXT NOT NULL,
                    processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    UNIQUE(hotel_key, avg_price_per_night)
                )
                """);
        }
    }

    @BeforeEach
    void clean() throws SQLException {
        try (var c = DatabaseConfig.getConnection();
             var s = c.createStatement()) {
            s.execute("DELETE FROM hotels");
        }
    }

    private Hotel createHotel(String name, String key, String type, String url, double rating,
                              double price, String city) {
        return new Hotel(0, name, key, type, url, rating, price, city, LocalDateTime.now());
    }

    @Test
    void testSaveHotel() throws SQLException {
        var h = createHotel("Hotel Test", "HT123", "Hostel", "http://test.com", 4.5, 50, "Paris");
        repo.save(h);
        assertTrue(h.getId() > 0, "Id must be assigned on save");
    }

    @Test
    void testSaveDuplicateHotelNoThrow() throws SQLException {
        var h1 = createHotel("Hotel Test", "HT123", "Hostel", "http://test.com", 4.5, 50, "Paris");
        var h2 = createHotel("Hotel Test", "HT123", "Hostel", "http://test.com", 4.5, 50, "Paris");
        repo.save(h1);
        assertDoesNotThrow(() -> repo.save(h2));
    }

    @Test
    void testFindByCity() throws SQLException {
        var h1 = createHotel("Hotel A", "HA123", "Hotel", "http://a.com", 3, 70, "Niza");
        var h2 = createHotel("Hotel B", "HB123", "Hostel", "http://b.com", 4, 40, "Niza");
        var h3 = createHotel("Hotel C", "HC123", "Hotel", "http://c.com", 5, 100, "Lyon");
        repo.save(h1);
        repo.save(h2);
        repo.save(h3);

        List<Hotel> nizaHotels = repo.findByCity("Niza");
        assertEquals(2, nizaHotels.size());
        assertTrue(nizaHotels.get(0).getAveragePricePerNight() <= nizaHotels.get(1).getAveragePricePerNight());
        nizaHotels.forEach(h -> assertEquals("Niza", h.getCity()));

        List<Hotel> lyonHotels = repo.findByCity("Lyon");
        assertEquals(1, lyonHotels.size());
        assertEquals("Lyon", lyonHotels.get(0).getCity());
    }
}
