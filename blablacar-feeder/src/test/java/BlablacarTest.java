import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import api.BlablacarApiClient;
import database.StopsRepository;
import org.junit.jupiter.api.*;
import scheduler.ApiScheduler;
import java.nio.file.Files;
import java.nio.file.Path;

public class BlablacarTest {

    private static final String DUMMY_API_KEY = "dummy-key";
    private static Path tempDbFile;
    private static String dbUrl;

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
    public void testApiSchedulerWithMockedClient() {
        BlablacarApiClient mockClient = new BlablacarApiClient(DUMMY_API_KEY) {
            @Override
            public String fetchData() {
                return """
                    {
                        "stops": [
                            {
                                "id": 1,
                                "_carrier_id": "abc",
                                "short_name": "MAD",
                                "long_name": "Madrid",
                                "time_zone": "Europe/Madrid",
                                "latitude": 40.4168,
                                "longitude": -3.7038,
                                "destinations_ids": [2, 3]
                            }
                        ]
                    }
                    """;
            }
        };

        StopsRepository repository = new StopsRepository(dbUrl);
        ApiScheduler scheduler = new ApiScheduler(mockClient, repository);
        scheduler.start();
        try {
            Thread.sleep(2000); // 2 segundos
        } catch (InterruptedException ignored) {}

        try (var conn = java.sql.DriverManager.getConnection(dbUrl);
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM stops")) {
            assertTrue(rs.next());
            assertEquals(1, rs.getInt("total"));
        } catch (Exception e) {
            fail("Error al verificar datos en la base de datos: " + e.getMessage());
        }
    }

    @Test
    public void testMalformedJsonHandledGracefully() {
        BlablacarApiClient mockClient = new BlablacarApiClient(DUMMY_API_KEY) {
            @Override
            public String fetchData() {
                return "INVALID_JSON";
            }
        };

        StopsRepository repository = new StopsRepository(dbUrl);
        ApiScheduler scheduler = new ApiScheduler(mockClient, repository);
        assertDoesNotThrow(scheduler::start);
    }
}
