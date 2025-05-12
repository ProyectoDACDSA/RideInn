package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final String DB_URL = "jdbc:sqlite:datamart.db";
    private static Connection connection;

    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
            connection.setAutoCommit(true);
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
            conn.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS trips (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "origin TEXT NOT NULL," +
                            "destination TEXT NOT NULL," +
                            "departure_date TEXT NOT NULL," +
                            "departure_time TEXT NOT NULL," +
                            "price REAL NOT NULL," +
                            "available INTEGER NOT NULL," +
                            "processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                            "CONSTRAINT unique_trip UNIQUE (origin, destination, departure_date, departure_time))");

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
                            "processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"+
                            "CONSTRAINT unique_book UNIQUE (hotel_key, start_date))");

            conn.createStatement().execute(
                    "CREATE INDEX IF NOT EXISTS idx_trips_destination ON trips(destination)");
            conn.createStatement().execute(
                    "CREATE INDEX IF NOT EXISTS idx_hotels_city ON hotels(city)");
        } catch (SQLException e) {
            throw new RuntimeException("Error al inicializar la base de datos", e);
        }
    }
}
