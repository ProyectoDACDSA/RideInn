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

    private void resetDatabase() {
        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            conn.createStatement().execute("DROP TABLE IF EXISTS hotels;");
            String createTableSQL = """
                CREATE TABLE hotels (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT,
                    key TEXT UNIQUE,
                    type TEXT,
                    url TEXT,
                    rating REAL,
                    review_count INTEGER,
                    min_price REAL,
                    max_price REAL,
                    latitude REAL,
                    longitude REAL,
                    amenities TEXT,
                    city TEXT
                );
            """;
            conn.createStatement().execute(createTableSQL);
        } catch (SQLException e) {
            LOGGER.warning("Database reset error:");
            e.printStackTrace();
        }
    }

    public void saveHotels(String jsonData, String city) {
        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(jsonData, JsonObject.class);
            JsonArray hotels = jsonObject.getAsJsonObject("result").getAsJsonArray("list");

            if (hotels == null) {
                LOGGER.warning("No hotels found in API response.");
                return;
            }

            String insertSQL = """
                INSERT OR REPLACE INTO hotels 
                (name, key, type, url, rating, review_count, min_price, max_price, latitude, longitude, amenities, city) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
            """;
            PreparedStatement pstmt = conn.prepareStatement(insertSQL);

            for (JsonElement el : hotels) {
                JsonObject hotel = el.getAsJsonObject();

                String name = hotel.get("name").getAsString();
                String key = hotel.get("key").getAsString();
                String type = hotel.get("accommodation_type").getAsString();
                String url = hotel.get("url").getAsString();
                double rating = hotel.getAsJsonObject("review_summary").get("rating").getAsDouble();
                int reviewCount = hotel.getAsJsonObject("review_summary").get("count").getAsInt();
                double minPrice = hotel.getAsJsonObject("price_ranges").get("minimum").getAsDouble();
                double maxPrice = hotel.getAsJsonObject("price_ranges").get("maximum").getAsDouble();
                double latitude = hotel.getAsJsonObject("geo").get("latitude").getAsDouble();
                double longitude = hotel.getAsJsonObject("geo").get("longitude").getAsDouble();

                JsonArray amenitiesArray = hotel.getAsJsonArray("highlighted_amenities");
                StringBuilder amenities = new StringBuilder();
                for (JsonElement amenityEl : amenitiesArray) {
                    amenities.append(amenityEl.getAsJsonObject().get("name").getAsString()).append(", ");
                }
                String amenitiesString = amenities.length() > 0 ? amenities.substring(0, amenities.length() - 2) : "";

                pstmt.setString(1, name);
                pstmt.setString(2, key);
                pstmt.setString(3, type);
                pstmt.setString(4, url);
                pstmt.setDouble(5, rating);
                pstmt.setInt(6, reviewCount);
                pstmt.setDouble(7, minPrice);
                pstmt.setDouble(8, maxPrice);
                pstmt.setDouble(9, latitude);
                pstmt.setDouble(10, longitude);
                pstmt.setString(11, amenitiesString);
                pstmt.setString(12, city);

                pstmt.addBatch();
            }
            pstmt.executeBatch();
            System.out.println("Data inserted successfully for " + city);
        } catch (SQLException e) {
            LOGGER.warning("Database error:");
            e.printStackTrace();
        }
    }
}


