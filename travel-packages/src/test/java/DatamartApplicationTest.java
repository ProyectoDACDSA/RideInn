import configuration.DatabaseConfig;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import service.EventStoreReader;
import static org.mockito.Mockito.*;

public class DatamartApplicationTest {

    private MockedStatic<DatabaseConfig> databaseConfigMock;
    private MockedStatic<EventStoreReader> eventStoreReaderStaticMock;

    @BeforeEach
    public void setUp() {
        databaseConfigMock = Mockito.mockStatic(DatabaseConfig.class);
        databaseConfigMock.when(DatabaseConfig::initializeDatabase).then(invocation -> null);
        databaseConfigMock.when(DatabaseConfig::closeConnection).then(invocation -> null);
    }

    @AfterEach
    public void tearDown() {
        databaseConfigMock.close();
        if (eventStoreReaderStaticMock != null) {
            eventStoreReaderStaticMock.close();
        }
    }

    @Test
    public void testMainExceptionHandling() throws Exception {
        databaseConfigMock.when(DatabaseConfig::initializeDatabase).thenThrow(new RuntimeException("Error DB"));

        DatamartApplication.main(new String[]{});

        databaseConfigMock.verify(DatabaseConfig::initializeDatabase, times(1));
        databaseConfigMock.verify(DatabaseConfig::closeConnection, times(1));
    }
}

