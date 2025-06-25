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
    private String originalDbUrl;

    @BeforeEach
    void setUp() throws SQLException {
        // Guardar el valor original de DB_URL para restaurarlo después
        originalDbUrl = System.getenv("DB_URL");

        // Configurar base de datos en memoria para pruebas
        // Esto funciona porque DatabaseConfig usa System.getenv()
        // Necesitamos usar reflection para modificar la variable de entorno temporalmente
        try {
            // Usar reflection para modificar las variables de entorno
            java.util.Map<String, String> env = System.getenv();
            java.lang.reflect.Field field = env.getClass().getDeclaredField("m");
            field.setAccessible(true);
            ((java.util.Map<String, String>) field.get(env)).put("DB_URL", "jdbc:sqlite::memory:");
        } catch (Exception e) {
            throw new RuntimeException("Failed to set environment variable", e);
        }

        DatabaseConfig.initializeDatabase();
        hotelRepository = new HotelRepository();
    }

    @AfterEach
    void tearDown() {
        // Restaurar el valor original de DB_URL
        try {
            if (originalDbUrl != null) {
                java.util.Map<String, String> env = System.getenv();
                java.lang.reflect.Field field = env.getClass().getDeclaredField("m");
                field.setAccessible(true);
                ((java.util.Map<String, String>) field.get(env)).put("DB_URL", originalDbUrl);
            } else {
                java.util.Map<String, String> env = System.getenv();
                java.lang.reflect.Field field = env.getClass().getDeclaredField("m");
                field.setAccessible(true);
                ((java.util.Map<String, String>) field.get(env)).remove("DB_URL");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to restore environment variable", e);
        }

        DatabaseConfig.closeConnection();
    }

    @Test
    void saveAndFindHotel() throws SQLException {
        // Crear un hotel de prueba
        Hotel hotel = new Hotel(
                0L,
                "Hotel Test",
                "test-key-123",
                "Hotel",
                "http://test.com",
                4.5,
                100.0,
                "Madrid",
                LocalDateTime.now()
        );

        // Guardar el hotel
        hotelRepository.save(hotel);

        // Verificar que se asignó un ID
        assertTrue(hotel.getId() > 0, "El hotel debería tener un ID asignado después de guardar");

        // Buscar hoteles por ciudad
        List<Hotel> hotels = hotelRepository.findByCity("Madrid");

        // Verificar resultados
        assertFalse(hotels.isEmpty(), "Debería haber al menos un hotel en Madrid");
        Hotel savedHotel = hotels.get(0);

        assertEquals(hotel.getHotelName(), savedHotel.getHotelName());
        assertEquals(hotel.getKey(), savedHotel.getKey());
        assertEquals(hotel.getAccommodationType(), savedHotel.getAccommodationType());
        assertEquals(hotel.getUrl(), savedHotel.getUrl());
        assertEquals(hotel.getRating(), savedHotel.getRating());
        assertEquals(hotel.getAveragePricePerNight(), savedHotel.getAveragePricePerNight());
        assertEquals(hotel.getCity(), savedHotel.getCity());
    }

    @Test
    void saveDuplicateHotelWithSamePrice() throws SQLException {
        Hotel hotel1 = new Hotel(
                0L,
                "Duplicate Hotel",
                "dup-key-456",
                "Hostel",
                "http://duplicate.com",
                3.5,
                75.0,
                "Barcelona",
                LocalDateTime.now()
        );

        Hotel hotel2 = new Hotel(
                0L,
                "Duplicate Hotel",
                "dup-key-456",
                "Hostel",
                "http://duplicate.com",
                3.5,
                75.0,
                "Barcelona",
                LocalDateTime.now()
        );

        // Guardar el primer hotel (debería funcionar)
        hotelRepository.save(hotel1);
        assertTrue(hotel1.getId() > 0);

        // Intentar guardar el segundo hotel (debería ser ignorado silenciosamente por la restricción UNIQUE)
        hotelRepository.save(hotel2);

        // Verificar que solo hay un hotel en Barcelona
        List<Hotel> hotels = hotelRepository.findByCity("Barcelona");
        assertEquals(1, hotels.size(), "Debería haber solo un hotel con la misma clave y precio");
    }

    @Test
    void findByCityWithNoResults() throws SQLException {
        List<Hotel> hotels = hotelRepository.findByCity("CiudadInexistente");
        assertTrue(hotels.isEmpty(), "No debería haber hoteles para una ciudad inexistente");
    }

    @Test
    void saveHotelWithNullRating() throws SQLException {
        Hotel hotel = new Hotel(
                0L,
                "Null Rating Hotel",
                "null-rating-789",
                "Apartment",
                "http://nullrating.com",
                null,
                85.0,
                "Valencia",
                LocalDateTime.now()
        );

        hotelRepository.save(hotel);
        assertTrue(hotel.getId() > 0);

        List<Hotel> hotels = hotelRepository.findByCity("Valencia");
        assertEquals(1, hotels.size());
        assertNull(hotels.get(0).getRating(), "El rating debería ser null");
    }
}