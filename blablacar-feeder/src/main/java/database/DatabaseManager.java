package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = System.getenv("DB_URL");

    public DatabaseManager() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String checkTableExistenceQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name='fares'";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(checkTableExistenceQuery)) {
                if (!rs.next()) {
                    String createTableSQL = "CREATE TABLE IF NOT EXISTS fares (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "origin TEXT NOT NULL," +
                            "destination TEXT NOT NULL," +
                            "origin_id INTEGER NOT NULL," +
                            "destination_id INTEGER NOT NULL," +
                            "departure TEXT," +
                            "arrival TEXT," +
                            "available BOOLEAN," +
                            "price_cents INTEGER," +
                            "price_currency TEXT," +
                            "updated_at TEXT," +
                            "response TEXT," +
                            "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP" +
                            ")";
                    try (Statement createStmt = conn.createStatement()) {
                        createStmt.execute(createTableSQL);
                        System.out.println("Database initialized successfully.");
                    }
                }
            }

            String checkColumnExistenceQuery = "PRAGMA table_info(fares)";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(checkColumnExistenceQuery)) {
                boolean responseExists = false;

                while (rs.next()) {
                    String columnName = rs.getString("name");
                    if ("response".equals(columnName)) {
                        responseExists = true;
                    }
                }

                if (!responseExists) {
                    String addResponseColumnSQL = "ALTER TABLE fares ADD COLUMN response TEXT";
                    try (Statement alterStmt = conn.createStatement()) {
                        alterStmt.execute(addResponseColumnSQL);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveFare(String origin, String destination, int originId, int destinationId,
                         String departure, String arrival, boolean available,
                         int priceCents, String priceCurrency, String updatedAt, String response) {
        String sql = "INSERT INTO fares (origin, destination, origin_id, destination_id, departure, arrival, available, price_cents, price_currency, updated_at, response) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

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
            pstmt.setString(11, response);

            pstmt.executeUpdate();

            System.out.println("Saved in the database: " + origin + " -> " + destination + "\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
