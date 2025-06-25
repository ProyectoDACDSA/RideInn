package repository;

import configuration.DatabaseConfig;
import model.Trip;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TripRepositoryTest {

    private TripRepository repository;

    @BeforeAll
    void setupDatabase() throws SQLException {
        repository = new TripRepository();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS trips (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "origin TEXT NOT NULL," +
                    "destination TEXT NOT NULL," +
                    "departure_date TEXT NOT NULL," +
                    "departure_time TEXT NOT NULL," +
                    "price REAL NOT NULL," +
                    "available INTEGER NOT NULL," +
                    "UNIQUE(origin, destination, departure_date, departure_time)" +
                    ")");
        }
    }

    @BeforeEach
    void cleanTable() throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM trips");
        }
    }

    @Test
    void testSaveTrip() throws SQLException {
        Trip trip = new Trip("Paris", "Lyon", "08:00", "2025-05-20", 30.5, 10);
        repository.save(trip);

        assertTrue(trip.getId() > 0, "El id debe haberse asignado tras guardar");
    }

    @Test
    void testSaveDuplicateTripLogsInfo() throws SQLException {
        Trip trip1 = new Trip("Paris", "Lyon", "08:00", "2025-05-20", 30.5, 10);
        repository.save(trip1);

        Trip trip2 = new Trip("Paris", "Lyon", "08:00", "2025-05-20", 35.0, 5);
        assertDoesNotThrow(() -> repository.save(trip2));
    }

    @Test
    void testFindByDestinationReturnsCorrectTrips() throws SQLException {
        Trip trip1 = new Trip("Paris", "Niza", "08:00", "2025-05-20", 30.5, 10);
        Trip trip2 = new Trip("Toulouse", "Niza", "10:00", "2025-05-21", 25.0, 5);
        Trip trip3 = new Trip("Paris", "Estrasburgo", "09:00", "2025-05-22", 20.0, 7);

        repository.save(trip1);
        repository.save(trip2);
        repository.save(trip3);

        List<Trip> nizaTrips = repository.findByDestination("Niza");
        assertEquals(2, nizaTrips.size());

        for (Trip trip : nizaTrips) {
            assertEquals("Niza", trip.getDestination());
        }

        List<Trip> estrasburgoTrips = repository.findByDestination("Estrasburgo");
        assertEquals(1, estrasburgoTrips.size());
        assertEquals("Estrasburgo", estrasburgoTrips.get(0).getDestination());
    }
}

