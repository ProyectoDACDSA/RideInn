package configuration;

import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConfigTest {

    @BeforeEach
    void setUp() {
        DatabaseConfig.initializeDatabase();
    }

    @AfterEach
    void tearDown() {
        DatabaseConfig.closeConnection();
    }

    @Test
    void testConnectionIsNotNullAndOpen() throws SQLException {
        Connection conn = DatabaseConfig.getConnection();
        assertNotNull(conn);
        assertFalse(conn.isClosed());
    }

    @Test
    void testCloseConnection() throws SQLException {
        Connection conn = DatabaseConfig.getConnection();
        DatabaseConfig.closeConnection();
        assertTrue(conn.isClosed());
    }

    @Test
    void testInitializeDatabaseCreatesTables() throws SQLException {
        Connection conn = DatabaseConfig.getConnection();
        DatabaseMetaData meta = conn.getMetaData();

        ResultSet tripsTable = meta.getTables(null, null, "trips", null);
        ResultSet hotelsTable = meta.getTables(null, null, "hotels", null);

        assertTrue(tripsTable.next(), "Tabla 'trips' no fue creada");
        assertTrue(hotelsTable.next(), "Tabla 'hotels' no fue creada");

        tripsTable.close();
        hotelsTable.close();
    }
}

