package scheduler;

import api.BlablacarApiClient;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Timer;
import java.util.TimerTask;

public class BlablacarApiScheduler {
    private final String[] cities = {"Paris", "Marsella", "Lyon", "Niza", "Toulouse"};
    private final int[] cityIds = {90, 1633, 137, 210, 16};
    private final BlablacarApiClient apiClient;

    public BlablacarApiScheduler(BlablacarApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public void start() {
        updateAllFares();
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(createUpdateTask(), getDelay(), getDelay());
        System.out.println("Scheduled daily update.");
    }

    private TimerTask createUpdateTask() {
        return new TimerTask() {
            public void run() {
                updateAllFares();
            }
        };
    }

    private long getDelay() {
        return 24 * 60 * 60 * 1000L;
    }

    private void updateAllFares() {
        System.out.println("Updating routes...\n");
        forEachCityPair(this::fetchAndSaveFare);
        System.out.println("Update completed.");
    }

    private void forEachCityPair(CityPairAction action) {
        for (int i = 0; i < cities.length; i++) {
            for (int j = 0; j < cities.length; j++) {
                if (i != j) {
                    action.execute(i, j);
                }
            }
        }
    }

    private void fetchAndSaveFare(int i, int j) {
        try {
            String response = apiClient.fetchFare(cityIds[i], cityIds[j]);
            if (response != null) {
                parseAndSave(response, cities[i], cities[j], cityIds[i], cityIds[j]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseAndSave(String jsonResponse, String origin, String destination, int originId, int destinationId) {
        JSONArray fares = extractFares(jsonResponse);
        if (fares != null && !fares.isEmpty()) {
            insertFares(fares, origin, destination, originId, destinationId);
        } else {
            System.out.println("There are no fares available for " + origin + " -> " + destination + "\n");
        }
    }

    private JSONArray extractFares(String jsonResponse) {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        return jsonObject.optJSONArray("fares");
    }

    private void insertFares(JSONArray fares, String origin, String destination, int originId, int destinationId) {
        int limit = Math.min(25, fares.length());
        for (int i = 0; i < limit; i++) {
            saveSingleFare(fares.getJSONObject(i), origin, destination, originId, destinationId);
        }
        System.out.println("Inserted " + limit + " fares for " + origin + " -> " + destination + "\n");
    }

    private void saveSingleFare(JSONObject fare, String origin, String destination, int originId, int destinationId) {
        sendFareToTopic(fare, origin, destination);
    }

    private void sendFareToTopic(JSONObject fare, String origin, String destination) {
        String departure = getDeparture(fare);
        double price = fare.optDouble("price_cents", 0) / 100.0;
        boolean available = fare.optBoolean("available", false);

        apiClient.processFareAndSendEvent(
                origin, destination, departure, price, available ? 1 : 0
        );
    }


    private String getDeparture(JSONObject fare) {
        JSONArray legs = fare.optJSONArray("legs");
        return legs != null && !legs.isEmpty() ? legs.getJSONObject(0).optString("departure", null) :
                fare.optString("departure", null);
    }

    private String getArrival(JSONObject fare) {
        JSONArray legs = fare.optJSONArray("legs");
        return legs != null && !legs.isEmpty() ? legs.getJSONObject(legs.length() - 1).optString("arrival", null) :
                fare.optString("arrival", null);
    }

    private interface CityPairAction {
        void execute(int i, int j);
    }
}
