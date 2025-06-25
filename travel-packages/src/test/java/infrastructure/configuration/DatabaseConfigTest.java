package infrastructure.configuration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

class DatabaseConfigTest {

    @BeforeEach
    @AfterEach
    void cleanup() throws SQLException {
        DatabaseConfig.closeConnection();
    }

    @Test
    void shouldThrowExceptionWhenUrlNotSet() {
        System.clearProperty("DB_URL");

        assertThrows(IllegalStateException.class, DatabaseConfig::getConnection);
    }

    @Test
    void shouldHandleCloseWhenNoConnectionExists() {
        assertDoesNotThrow(DatabaseConfig::closeConnection);
    }
}