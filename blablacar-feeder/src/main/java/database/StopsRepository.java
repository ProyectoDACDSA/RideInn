package database;

import com.google.gson.*;
import java.sql.*;

public class StopsRepository {
    private final String dbUrl;

    public StopsRepository(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public void save(String jsonData) {
        try (Connection conn = DriverManager.getConnection(dbUrl)) {
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
                System.out.println("No se encontraron paradas en la respuesta de la API.");
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
            System.out.println("Datos insertados correctamente en la base de datos.");
        } catch (SQLException e) {
            System.out.println("Error al guardar los datos en la base de datos:");
            e.printStackTrace();
        }
    }
}
