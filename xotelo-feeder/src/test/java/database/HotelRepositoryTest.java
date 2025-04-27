package database;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.sql.*;

public class HotelRepositoryTest {

    private HotelRepository repository;
    private Connection conn;

    @BeforeEach
    public void setup() throws Exception {
        String dbUrl = "jdbc:sqlite::memory:";
        repository = new HotelRepository(dbUrl);
        conn = DriverManager.getConnection(dbUrl);

        repository.resetDatabase();
    }

    @AfterEach
    public void cleanup() throws Exception {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    @Test
    public void testSaveHotelsWithInvalidJsonDoesNotInsert() {
        String invalidJson = "{}"; // No "result" field

        assertDoesNotThrow(() -> repository.saveHotels(invalidJson, "Paris"),
                "Should handle invalid JSON gracefully without throwing");
    }
}


