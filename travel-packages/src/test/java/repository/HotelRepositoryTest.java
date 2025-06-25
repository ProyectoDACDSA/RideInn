package repository;

import domain.model.Hotel;
import infrastructure.configuration.DatabaseConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HotelRepositoryTest {
    private HotelRepository hotelRepository;

    @BeforeEach
    void setUp() throws SQLException {
        // Configurar SQLite en memoria
        System.setProperty("DB_URL", "jdbc:sqlite::memory:");
        DatabaseConfig.initializeDatabase();
        hotelRepository = new HotelRepository();
    }

    @AfterEach
    void tearDown() throws SQLException {
        DatabaseConfig.closeConnection();
    }

    @Test
    void testSaveHotel() throws SQLException {
        // Usar Double.valueOf en lugar de double primitivo para el rating
        Hotel hotel = new Hotel(123, "Grand Hotel", "grand-123", "Luxury",
                "http://example.com", Double.valueOf(4.5), 150.0, "Paris",
                LocalDateTime.now());

        hotelRepository.save(hotel);
        assertNotNull(hotel.getId());
    }

    @Test
    void testFindByCity() throws SQLException {
        // Datos de prueba con Double para rating
        Hotel hotel1 = new Hotel(123, "Hotel A", "hotel-a", "Standard",
                "http://hotela.com", Double.valueOf(3.5), 80.0, "Madrid",
                LocalDateTime.now());
        Hotel hotel2 = new Hotel(234, "Hotel B", "hotel-b", "Standard",
                "http://hotelb.com", Double.valueOf(4.0), 100.0, "Madrid",
                LocalDateTime.now());

        hotelRepository.save(hotel1);
        hotelRepository.save(hotel2);

        List<Hotel> hotels = hotelRepository.findByCity("Madrid");
        assertEquals(2, hotels.size());
    }

    @Test
    void testFindByCityNotFound() throws SQLException {
        List<Hotel> hotels = hotelRepository.findByCity("UnknownCity");
        assertTrue(hotels.isEmpty());
    }

    @Test
    void testSaveHotelWithNullRating() throws SQLException {
        // Hotel sin rating (null)
        Hotel hotel = new Hotel(123, "No Rating Hotel", "no-rating", "Basic",
                "http://no-rating.com", null, 50.0, "Berlin",
                LocalDateTime.now());

        hotelRepository.save(hotel);
        assertNotNull(hotel.getId());

        List<Hotel> hotels = hotelRepository.findByCity("Berlin");
        assertEquals(1, hotels.size());
        assertNull(hotels.get(0).getRating());
    }

    @Test
    void testSaveDuplicateHotelKey() {
        Hotel hotel1 = new Hotel(123, "Duplicate", "dup-key", "Standard",
                "http://duplicate.com", Double.valueOf(3.0), 75.0, "Rome",
                LocalDateTime.now());

        Hotel hotel2 = new Hotel(234, "Duplicate", "dup-key", "Standard",
                "http://duplicate.com", Double.valueOf(3.0), 75.0, "Rome",
                LocalDateTime.now());

        try {
            hotelRepository.save(hotel1);
            hotelRepository.save(hotel2); // Debería fallar por clave duplicada
            fail("Debería haber lanzado SQLException");
        } catch (SQLException e) {
            assertTrue(e.getMessage().contains("UNIQUE constraint failed"));
        }
    }
}