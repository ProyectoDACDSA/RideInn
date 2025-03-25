package blablacar_feeder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class blablacarAPI {

    private static final String API_URL = "https://bus-api.blablacar.com/v3/stops";
    private static final String API_KEY = "RPQn87Lb1z2OPQTwsOQsAQ";
    private static final String DB_URL = "jdbc:sqlite:rideinn.db";

    public static void main(String[] args) {
        String jsonResponse = fetchDataFromAPI();
        if (jsonResponse != null) {
            saveDataToDatabase(jsonResponse);
        }
    }

    private static String fetchDataFromAPI() {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Token " + API_KEY);
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                System.out.println("API connected successfully");
                return response.toString();
            } else {
                System.out.println("Connection error: " + responseCode);
            }
            connection.disconnect();
        } catch (Exception e) {
            System.out.println("Connection error: ");
            e.printStackTrace();
        }
        return null;
    }

    private static void saveDataToDatabase(String jsonData) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {

            String dropTableSQL = "DROP TABLE IF EXISTS stops;";
            conn.createStatement().execute(dropTableSQL);

            String createTableSQL = """
            CREATE TABLE IF NOT EXISTS stops (
                id INTEGER PRIMARY KEY,
                carrier_id TEXT,
                short_name TEXT,
                long_name TEXT,
                time_zone TEXT,
                latitude REAL,
                longitude REAL,
                destinations_ids TEXT
            );
        """;
            conn.createStatement().execute(createTableSQL);

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(jsonData, JsonObject.class);
            JsonArray stops = jsonObject.getAsJsonArray("stops");

            if (stops == null) {
                System.out.println("No stops found in API response.");
                return;
            }

            String insertSQL = """
            INSERT OR REPLACE INTO stops 
            (id, carrier_id, short_name, long_name, time_zone, latitude, longitude, destinations_ids) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?);
        """;
            PreparedStatement pstmt = conn.prepareStatement(insertSQL);

            for (int i = 0; i < stops.size(); i++) {
                JsonObject stop = stops.get(i).getAsJsonObject();

                int id = stop.has("id") ? stop.get("id").getAsInt() : -1;
                String carrierId = stop.has("_carrier_id") ? stop.get("_carrier_id").getAsString() : null;
                String shortName = stop.has("short_name") ? stop.get("short_name").getAsString() : null;
                String longName = stop.has("long_name") ? stop.get("long_name").getAsString() : null;
                String timeZone = stop.has("time_zone") ? stop.get("time_zone").getAsString() : null;
                double latitude = stop.has("latitude") ? stop.get("latitude").getAsDouble() : 0.0;
                double longitude = stop.has("longitude") ? stop.get("longitude").getAsDouble() : 0.0;

                JsonArray destinationsArray = stop.has("destinations_ids") ? stop.getAsJsonArray("destinations_ids") : null;
                String destinationsIds = (destinationsArray != null) ? gson.toJson(destinationsArray) : "[]";

                pstmt.setInt(1, id);
                pstmt.setString(2, carrierId);
                pstmt.setString(3, shortName);
                pstmt.setString(4, longName);
                pstmt.setString(5, timeZone);
                pstmt.setDouble(6, latitude);
                pstmt.setDouble(7, longitude);
                pstmt.setString(8, destinationsIds);

                pstmt.addBatch();
            }
            pstmt.executeBatch();
            System.out.println("Data inserted successfully");
        } catch (SQLException e) {
            System.out.println("Database error:");
            e.printStackTrace();
        }
    }
}

