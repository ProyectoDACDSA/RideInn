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

    private HotelRepository repository;

    @BeforeAll
    void setupDatabase() throws SQLException {
        repository = new HotelRepository();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS hotels (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "hotel_name TEXT NOT NULL," +
                    "hotel_key TEXT NOT NULL," +
                    "accommodation_type TEXT NOT NULL," +
                    "url TEXT NOT NULL," +
                    "rating DOUBLE," +
                    "avg_price_per_night DOUBLE NOT NULL," +
                    "city TEXT NOT NULL," +
                    "processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "UNIQUE(hotel_key, avg_price_per_night)" +
                    ")");
        }
    }

    @BeforeEach
    void cleanTable() throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM hotels");
        }
    }

    @Test
    void testSaveHotel() throws SQLException {
        Hotel hotel = new Hotel(
                0, "Hotel Test", "HT123", "Hostel", "http://test.com",
                4.5, 50.0, "Paris",
                LocalDateTime.now());
        repository.save(hotel);

        assertTrue(hotel.getId() > 0, "El id debe asignarse al guardar");
    }

    @Test
    void testSaveDuplicateHotelLogsInfo() throws SQLException {
        Hotel hotel1 = new Hotel(
                0, "Hotel Test", "HT123", "Hostel", "http://test.com",
                4.5, 50.0, "Paris",
                LocalDateTime.now());
        repository.save(hotel1);

        Hotel hotel2 = new Hotel(
                0, "Hotel Test", "HT123", "Hostel", "http://test.com",
                4.5, 50.0, "Paris",
                LocalDateTime.now());
        assertDoesNotThrow(() -> repository.save(hotel2));
    }

    @Test
    void testFindByCityReturnsCorrectHotels() throws SQLException {
        Hotel hotel1 = new Hotel(
                0, "Hotel A", "HA123", "Hotel", "http://a.com", 3.0,
                70.0, "Niza",
                LocalDateTime.now());
        Hotel hotel2 = new Hotel(
                0, "Hotel B", "HB123", "Hostel", "http://b.com",
                4.0, 40.0, "Niza",
                LocalDateTime.now());
        Hotel hotel3 = new Hotel(
                0, "Hotel C", "HC123", "Hotel", "http://c.com",
                5.0, 100.0, "Lyon",
                LocalDateTime.now());

        repository.save(hotel1);
        repository.save(hotel2);
        repository.save(hotel3);

        List<Hotel> nizaHotels = repository.findByCity("Niza");
        assertEquals(2, nizaHotels.size());

        assertTrue(nizaHotels.get(0).getAveragePricePerNight() <= nizaHotels.get(1).getAveragePricePerNight());

        for (Hotel h : nizaHotels) {
            assertEquals("Niza", h.getCity());
        }

        List<Hotel> lyonHotels = repository.findByCity("Lyon");
        assertEquals(1, lyonHotels.size());
        assertEquals("Lyon", lyonHotels.get(0).getCity());
    }
}
