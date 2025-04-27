package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = System.getenv("DB_URL");

    public DatabaseManager() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = createConnection();
             Statement stmt = conn.createStatement()) {
            resetDatabase(stmt);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void resetDatabase(Statement stmt) throws SQLException {
        stmt.execute("DROP TABLE IF EXISTS fares");
        stmt.execute(buildCreateTableSQL());
        System.out.println("Database reset and initialized successfully.");
    }

    private String buildCreateTableSQL() {
        return """
            CREATE TABLE fares (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                origin TEXT NOT NULL,
                destination TEXT NOT NULL,
                origin_id INTEGER NOT NULL,
                destination_id INTEGER NOT NULL,
                departure TEXT,
                arrival TEXT,
                available BOOLEAN,
                price_cents INTEGER,
                price_currency TEXT,
                updated_at TEXT
            );
        """;
    }

    public void saveFare(String origin, String destination, int originId, int destinationId,
                         String departure, String arrival, boolean available,
                         int priceCents, String priceCurrency, String updatedAt) {
        try (Connection conn = createConnection();
             PreparedStatement pstmt = conn.prepareStatement(buildInsertSQL())) {
            setFareParameters(pstmt, origin, destination, originId, destinationId,
                    departure, arrival, available, priceCents, priceCurrency, updatedAt);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setFareParameters(PreparedStatement pstmt, String origin, String destination, int originId, int destinationId,
                                   String departure, String arrival, boolean available,
                                   int priceCents, String priceCurrency, String updatedAt) throws SQLException {
        pstmt.setString(1, origin);
        pstmt.setString(2, destination);
        pstmt.setInt(3, originId);
        pstmt.setInt(4, destinationId);
        pstmt.setString(5, departure);
        pstmt.setString(6, arrival);
        pstmt.setBoolean(7, available);
        pstmt.setInt(8, priceCents);
        pstmt.setString(9, priceCurrency);
        pstmt.setString(10, updatedAt);
    }

    private String buildInsertSQL() {
        return """
            INSERT INTO fares (origin, destination, origin_id, destination_id, departure, arrival, available, price_cents, price_currency, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
    }

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}

