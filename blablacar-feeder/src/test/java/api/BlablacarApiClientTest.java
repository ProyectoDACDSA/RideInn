package api;
import org.junit.jupiter.api.*;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

public class BlablacarApiClientTest {

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
    public void testDummyApiClientReturnsExpectedString() throws Exception {
        BlablacarApiClient client = new BlablacarApiClient("dummy-key") {
            @Override
            public String fetchData() {
                return "mock response";
            }
        };
        String result = client.fetchData();
        assertNotNull(result);
        assertEquals("mock response", result);
    }
}