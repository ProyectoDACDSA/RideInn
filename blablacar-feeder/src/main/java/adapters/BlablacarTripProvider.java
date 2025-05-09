package adapters;

import ports.ApiClient;
import ports.EventSender;
import java.util.Map;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class BlablacarTripProvider {
    private static final Logger LOGGER = Logger.getLogger(BlablacarTripProvider.class.getName());
    private static final int MAX_TRIPS_PER_ROUTE = 25;

    private static final Map<String, Integer> CITY_IDS = Map.of(
            "Paris", 90,
            "Estrasburgo", 27,
            "Lyon", 137,
            "Niza", 210,
            "Toulouse", 16
    );

    private final ApiClient apiClient;
    private final EventSender eventSender;

    public BlablacarTripProvider(ApiClient apiClient, EventSender eventSender) {
        this.apiClient = apiClient;
        this.eventSender = eventSender;
    }

    public Map<String, Integer> getCityIds() {
        return CITY_IDS;
    }

    public void fetchAndProcessAllTrips() {
        CITY_IDS.forEach((originCity, originId) -> {
            CITY_IDS.forEach((destCity, destId) -> {
                if (!originCity.equals(destCity)) {
                    processCityPair(originCity, destCity, originId, destId);
                }
            });
        });
    }

    private void processCityPair(String origin, String destination, int originId, int destId) {
        try {
            System.out.println(String.format("Processing trips from %s to %s", origin, destination)); // Cambiado a System.out
            String response = apiClient.fetchFare(originId, destId);
            if (response != null) {
                processApiResponse(response, origin, destination);
            }
        } catch (Exception e) {
            LOGGER.warning(String.format("Error processing %s -> %s: %s", origin, destination, e.getMessage()));
        }
    }

    private void processApiResponse(String jsonResponse, String origin, String destination) {
        JSONArray fares = new JSONObject(jsonResponse).optJSONArray("fares");
        if (fares == null || fares.isEmpty()) {
            System.out.println(String.format("No fares available for %s -> %s", origin, destination)); // Cambiado a System.out
            return;
        }

        int processedCount = 0;
        for (int i = 0; i < fares.length() && processedCount < MAX_TRIPS_PER_ROUTE; i++) {
            JSONObject fare = fares.getJSONObject(i);
            processSingleFare(fare, origin, destination);
            processedCount++;
        }
        System.out.println(String.format("Processed %d fares for %s -> %s", processedCount, origin, destination)); // Cambiado a System.out
    }

    private void processSingleFare(JSONObject fare, String origin, String destination) {
        try {
            String departure = extractDepartureTime(fare);
            double price = fare.optDouble("price_cents", 0) / 100.0;
            boolean available = fare.optBoolean("available", false);

            eventSender.sendEvent(origin, destination, departure, price, available ? 1 : 0);
        } catch (Exception e) {
            LOGGER.warning("Error processing fare: " + e.getMessage());
        }
    }

    private String extractDepartureTime(JSONObject fare) {
        JSONArray legs = fare.optJSONArray("legs");
        if (legs != null && !legs.isEmpty()) {
            return legs.getJSONObject(0).optString("departure");
        }
        return fare.optString("departure");
    }
}
