package adapters;

import ports.TripProvider;
import domain.Trip;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import com.google.gson.*;

public class BlablacarApiClient implements TripProvider {
    private static final Logger LOGGER = Logger.getLogger(BlablacarApiClient.class.getName());
    private static final String BASE_API_URL = "https://bus-api.blablacar.com/v2/fares";
    private final String apiKey;

    private static final Map<String, Integer> CITY_IDS = Map.of(
            "Paris", 90,
            "Estrasburgo", 27,
            "Lyon", 137,
            "Niza", 210,
            "Toulouse", 16
    );

    public BlablacarApiClient(String apiKey) {this.apiKey = apiKey;}

    @Override
    public Map<String, Integer> getCityIds() {return CITY_IDS;}

    @Override
    public List<Trip> fetchTripsForRoute(int originId, int destinationId) {
        try {
            String jsonResponse = fetchFare(originId, destinationId);
            if (jsonResponse == null) {
                return List.of();
            }
            return parseTripsFromResponse(jsonResponse, originId, destinationId);
        } catch (Exception e) {
            LOGGER.warning(String.format("Error fetching trips %d->%d: %s",
                    originId, destinationId, e.getMessage()));
            return List.of();
        }
    }

    private String fetchFare(int originId, int destinationId) throws Exception {
        HttpURLConnection connection = createConnection(originId, destinationId);
        try {
            return readResponse(connection);
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

    private String readResponse(HttpURLConnection connection) throws Exception {
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            LOGGER.warning("API request failed with code: " + responseCode);
            return null;
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }
        return response.toString();
    }

    private List<Trip> parseTripsFromResponse(String jsonResponse, int originId, int destinationId) {
        JsonArray fares = JsonParser.parseString(jsonResponse)
                .getAsJsonObject()
                .getAsJsonArray("fares");

        if (fares == null || fares.isEmpty()) {
            return List.of();
        }

        List<Trip> trips = new ArrayList<>();
        for (JsonElement fare : fares) {
            trips.add(createTripFromJson(fare.getAsJsonObject(), originId, destinationId));
        }
        return trips;
    }

    private Trip createTripFromJson(JsonObject fare, int originId, int destinationId) {
        String originCity = CITY_IDS.entrySet().stream()
                .filter(entry -> entry.getValue() == originId)
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse("");

        String destinationCity = CITY_IDS.entrySet().stream()
                .filter(entry -> entry.getValue() == destinationId)
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse("");

        return new Trip(
                fare.get("id").getAsLong(),
                ZonedDateTime.parse(fare.get("departure").getAsString()),
                ZonedDateTime.parse(fare.get("arrival").getAsString()),
                ZonedDateTime.parse(fare.get("schedule").getAsString()),
                fare.get("available").getAsBoolean(),
                fare.get("price_cents").getAsInt(),
                fare.get("price_currency").getAsString(),
                originId,
                originCity,
                destinationId,
                destinationCity,
                ZonedDateTime.now()
        );
    }
}