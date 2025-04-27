package database;

import com.google.gson.*;
import java.sql.*;
import java.util.logging.Logger;

public class HotelRepository {
    private final String dbUrl;
    private static final Logger LOGGER = Logger.getLogger(HotelRepository.class.getName());

    public HotelRepository(String dbUrl) {
        this.dbUrl = dbUrl;
        resetDatabase();
    }

    void resetDatabase() {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS hotels;");
            stmt.execute(getCreateTableSQL());
        } catch (SQLException e) {
            logError("Database reset error:", e);
        }
    }

    public void saveHotels(String jsonData, String city) {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(getInsertSQL())) {
            JsonArray hotels = parseHotels(jsonData);
            if (hotels == null) return;
            saveHotelsBatch(hotels, pstmt, city);
            System.out.println("Data inserted successfully for " + city);
        } catch (Exception e) {
            logError("Database error:", e);
        }
    }

    private JsonArray parseHotels(String jsonData) {
        JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();
        return jsonObject.getAsJsonObject("result").getAsJsonArray("list");
    }

    private void saveHotelsBatch(JsonArray hotels, PreparedStatement pstmt, String city) throws SQLException {
        for (JsonElement el : hotels) {
            bindHotelData(pstmt, el.getAsJsonObject(), city);
            pstmt.addBatch();
        }
        pstmt.executeBatch();
    }

    private void bindHotelData(PreparedStatement pstmt, JsonObject hotel, String city) throws SQLException {
        pstmt.setString(1, hotel.get("name").getAsString());
        pstmt.setString(2, hotel.get("key").getAsString());
        pstmt.setString(3, hotel.get("accommodation_type").getAsString());
        pstmt.setString(4, hotel.get("url").getAsString());
        pstmt.setDouble(5, hotel.getAsJsonObject("review_summary").get("rating").getAsDouble());
        pstmt.setInt(6, hotel.getAsJsonObject("review_summary").get("count").getAsInt());
        pstmt.setDouble(7, hotel.getAsJsonObject("price_ranges").get("minimum").getAsDouble());
        pstmt.setDouble(8, hotel.getAsJsonObject("price_ranges").get("maximum").getAsDouble());
        pstmt.setDouble(9, hotel.getAsJsonObject("geo").get("latitude").getAsDouble());
        pstmt.setDouble(10, hotel.getAsJsonObject("geo").get("longitude").getAsDouble());
        pstmt.setString(11, extractAmenities(hotel));
        pstmt.setString(12, city);
    }

    private String extractAmenities(JsonObject hotel) {
        JsonArray amenitiesArray = hotel.getAsJsonArray("highlighted_amenities");
        if (amenitiesArray == null) return "";
        StringBuilder amenities = new StringBuilder();
        for (JsonElement amenity : amenitiesArray) {
            amenities.append(amenity.getAsJsonObject().get("name").getAsString()).append(", ");
        }
        return !amenities.isEmpty() ? amenities.substring(0, amenities.length() - 2) : "";
    }

    private void logError(String message, Exception e) {
        LOGGER.warning(message);
        e.printStackTrace();
    }

    private String getCreateTableSQL() {
        return """
            CREATE TABLE hotels (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT, key TEXT UNIQUE, type TEXT, url TEXT,
                rating REAL, review_count INTEGER,
                min_price REAL, max_price REAL,
                latitude REAL, longitude REAL,
                amenities TEXT, city TEXT
            );
            """;
    }

    private String getInsertSQL() {
        return """
            INSERT OR REPLACE INTO hotels
            (name, key, type, url, rating, review_count, min_price, max_price, latitude, longitude, amenities, city)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
            """;
    }
}



