package database;
import org.junit.jupiter.api.*;
import java.nio.file.Files;
import java.nio.file.Path;
import database.StopsRepository;
import static org.junit.jupiter.api.Assertions.*;

public class StopsRepositoryTest {

    protected static Path tempDbFile;
    protected static String dbUrl;

    @BeforeAll
    public static void setupDatabase() throws Exception {
        tempDbFile = Files.createTempFile("test-stops", ".db");
        dbUrl = "jdbc:sqlite:" + tempDbFile.toAbsolutePath();
    }

    @AfterAll
    public static void cleanup() throws Exception {
        Files.deleteIfExists(tempDbFile);
    }

    @Test
    public void testStopWithMissingFields() {
        String json = """
            {
                \"stops\": [
                    {
                        \"id\": 2,
                        \"short_name\": \"BCN\"
                    }
                ]
            }
        """;
        StopsRepository repository = new StopsRepository(dbUrl);
        repository.save(json);

        try (var conn = java.sql.DriverManager.getConnection(dbUrl);
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery("SELECT * FROM stops WHERE id = 2")) {
            assertTrue(rs.next());
            assertEquals("BCN", rs.getString("short_name"));
            assertNull(rs.getString("carrier_id"));
        } catch (Exception e) {
            fail("Error al verificar base de datos: " + e.getMessage());
        }
    }
}