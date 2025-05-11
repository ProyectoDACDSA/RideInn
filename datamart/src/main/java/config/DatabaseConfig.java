package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final String DB_URL = "jdbc:sqlite:datamart.db";
    private static Connection connection;

    public static synchronized Connection getConnection() throws SQLException {
        System.out.println("Obteniendo conexión a: " + DB_URL);
        if (connection == null || connection.isClosed()) {
            System.out.println("Creando nueva conexión...");
            connection = DriverManager.getConnection(DB_URL);
            connection.setAutoCommit(true);
            System.out.println("Conexión establecida");
        }
        return connection;
    }

    public static void commit() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.commit();
        }
    }

    public static void rollback() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.rollback();
            }
        } catch (SQLException e) {
            System.err.println("Error during rollback: " + e.getMessage());
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }


    public static void initializeDatabase() {
        try (Connection conn = getConnection()) {
            boolean isNewDB = !checkIfTablesExist(conn);
            conn.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS trips (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "timestamp BIGINT NOT NULL," +
                            "origin TEXT NOT NULL," +
                            "destination TEXT NOT NULL," +
                            "departure_time TEXT NOT NULL," +
                            "price REAL NOT NULL," +
                            "available INTEGER NOT NULL," +
                            "processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            conn.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS hotels (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "timestamp BIGINT NOT NULL," +
                            "hotel_name TEXT NOT NULL," +
                            "hotel_key TEXT NOT NULL," +
                            "accommodation_type TEXT NOT NULL," +
                            "url TEXT," +
                            "rating REAL," +
                            "avg_price_per_night REAL NOT NULL," +
                            "start_date TEXT NOT NULL," +
                            "end_date TEXT NOT NULL," +
                            "total_price REAL NOT NULL," +
                            "city TEXT NOT NULL," +
                            "processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            conn.createStatement().execute(
                    "CREATE INDEX IF NOT EXISTS idx_trips_destination ON trips(destination)");
            conn.createStatement().execute(
                    "CREATE INDEX IF NOT EXISTS idx_hotels_city ON hotels(city)");
            if (isNewDB) {
                System.out.println("Nueva base de datos creada exitosamente");
            } else {
                System.out.println("Base de datos existente inicializada");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al inicializar la base de datos", e);
        }
    }

    private static boolean checkIfTablesExist(Connection conn) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getTables(null, null, null, new String[]{"TABLE"})) {
            boolean hasTrips = false;
            boolean hasHotels = false;
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME").toLowerCase();
                if ("trips".equals(tableName)) hasTrips = true;
                if ("hotels".equals(tableName)) hasHotels = true;
            }
            return hasTrips && hasHotels;
        }
    }
}
