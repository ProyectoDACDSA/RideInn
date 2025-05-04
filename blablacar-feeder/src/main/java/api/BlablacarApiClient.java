package api;

import publisher.BlablacarEventSender;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.JsonObject;
import com.google.gson.Gson;

public class BlablacarApiClient {
    private static final String BASE_API_URL = "https://bus-api.blablacar.com/v2/fares";
    private final String apiKey;
    private final BlablacarEventSender eventSender = new BlablacarEventSender();

    public BlablacarApiClient(String apiKey) {
        this.apiKey = apiKey;
    }

    public String fetchFare(int originId, int destinationId) throws Exception {
        HttpURLConnection connection = createConnection(originId, destinationId);
        try {
            return handleResponse(connection, originId, destinationId);
        } finally {
            connection.disconnect();
        }
    }

    private HttpURLConnection createConnection(int originId, int destinationId) throws Exception {
        String apiUrl = BASE_API_URL + "?origin_id=" + originId + "&destination_id=" + destinationId;
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Token " + apiKey);
        connection.setRequestProperty("Accept", "application/json");
        return connection;
    }

    private String handleResponse(HttpURLConnection connection, int originId, int destinationId) throws Exception {
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            return readResponse(connection, originId, destinationId);
        }
        System.out.println("Error connecting to " + originId + " -> " + destinationId + ": Code " + responseCode);
        return null;
    }

    private String readResponse(HttpURLConnection connection, int originId, int destinationId) throws Exception {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            System.out.println("Data received for " + originId + " -> " + destinationId);
            return response.toString();
        }
    }

    public void processFareAndSendEvent(String origin, String destination, String departureTime, double price, int available) {
        eventSender.sendEvent(origin, destination, departureTime, price, available);
    }
    public String crearTripEventJson(String origin, String destination, String departureTime, double price, int available) {
        JsonObject json = new JsonObject();
        long timestamp = System.currentTimeMillis();
        json.addProperty("ts", timestamp);
        json.addProperty("ss", "Blablacar");
        json.addProperty("origin", origin);
        json.addProperty("destination", destination);
        json.addProperty("departureTime", departureTime);
        json.addProperty("price", price);
        json.addProperty("seatsAvailable", available);
        return new Gson().toJson(json);
    }

}
