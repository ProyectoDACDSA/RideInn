package xotelo_feeder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class xoteloAPI {
    private static final String DB_URL = "jdbc:sqlite:rideinn.db";
    private static final Map<String, String> cityUrls = Map.of(
            "Madrid", "https://data.xotelo.com/api/list?location_key=g187514&offset=0&limit=30&sort=best_value",
            "Barcelona", "https://data.xotelo.com/api/list?location_key=g187497&offset=0&limit=30&sort=best_value",
            "Valencia", "https://data.xotelo.com/api/list?location_key=g187529&offset=0&limit=30&sort=best_value",
            "Granada", "https://data.xotelo.com/api/list?location_key=g187441&offset=0&limit=30&sort=best_value",
            "Seville", "https://data.xotelo.com/api/list?location_key=g187443&offset=0&limit=30&sort=best_value"
    );

    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        Runnable task = () -> {
            resetDatabase();
            for (Map.Entry<String, String> entry : cityUrls.entrySet()) {
                String jsonResponse = fetchData(entry.getKey(), entry.getValue());
                if (jsonResponse != null) {
                    saveDataToDatabase(jsonResponse, entry.getKey());
                }
            }
        };
        scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.DAYS);
    }

    private static String fetchData(String city, String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                System.out.println("API connected successfully for " + city);
                return response.toString();
            } else {
                System.out.println("Connection error for " + city + ": " + responseCode);
            }
            connection.disconnect();
        } catch (Exception e) {
            System.out.println("Connection error for " + city);
            e.printStackTrace();
        }
        return null;
    }

    private static void resetDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String dropTableSQL = "DROP TABLE IF EXISTS hotels;";
            conn.createStatement().execute(dropTableSQL);

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
            System.out.println("Database reset error:");
            e.printStackTrace();
        }
    }

    private static void saveDataToDatabase(String jsonData, String city) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(jsonData, JsonObject.class);
            JsonArray hotels = jsonObject.getAsJsonObject("result").getAsJsonArray("list");

            if (hotels == null) {
                System.out.println("No hotels found in API response.");
                return;
            }

            String insertSQL = """
                INSERT OR REPLACE INTO hotels 
                (name, key, type, url, rating, review_count, min_price, max_price, latitude, longitude, amenities, city) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
            """;
            PreparedStatement pstmt = conn.prepareStatement(insertSQL);

            for (int i = 0; i < hotels.size(); i++) {
                JsonObject hotel = hotels.get(i).getAsJsonObject();

                String name = hotel.get("name").getAsString();
                String key = hotel.get("key").getAsString();
                String type = hotel.get("accommodation_type").getAsString();
                String url = hotel.get("url").getAsString();
                double rating = hotel.get("review_summary").getAsJsonObject().get("rating").getAsDouble();
                int reviewCount = hotel.get("review_summary").getAsJsonObject().get("count").getAsInt();
                double minPrice = hotel.get("price_ranges").getAsJsonObject().get("minimum").getAsDouble();
                double maxPrice = hotel.get("price_ranges").getAsJsonObject().get("maximum").getAsDouble();
                double latitude = hotel.get("geo").getAsJsonObject().get("latitude").getAsDouble();
                double longitude = hotel.get("geo").getAsJsonObject().get("longitude").getAsDouble();

                JsonArray amenitiesArray = hotel.getAsJsonArray("highlighted_amenities");
                StringBuilder amenities = new StringBuilder();
                for (int j = 0; j < amenitiesArray.size(); j++) {
                    amenities.append(amenitiesArray.get(j).getAsJsonObject().get("name").getAsString()).append(", ");
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
            System.out.println("Database error:");
            e.printStackTrace();
        }
    }
}
