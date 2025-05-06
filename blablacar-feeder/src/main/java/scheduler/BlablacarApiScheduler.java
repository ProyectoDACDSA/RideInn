package scheduler;
import ports.ApiClient;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map;

public class BlablacarApiScheduler {
    private final ApiClient apiClient;

    public BlablacarApiScheduler(ApiClient apiClient) {
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
        Map<String, Integer> cityIds = apiClient.getCityIds();
        String[] cities = cityIds.keySet().toArray(new String[0]);

        for (int i = 0; i < cities.length; i++) {
            for (int j = 0; j < cities.length; j++) {
                if (i != j) {
                    action.execute(cities[i], cities[j], cityIds.get(cities[i]), cityIds.get(cities[j]));
                }
            }
        }
    }

    private void fetchAndSaveFare(String originCity, String destCity, int originId, int destId) {
        try {
            String response = apiClient.fetchFare(originId, destId);
            if (response != null) {
                parseAndSave(response, originCity, destCity, originId, destId);
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
            System.out.println("No fares available for " + origin + " -> " + destination + "\n");
        }
    }

    private JSONArray extractFares(String jsonResponse) {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        return jsonObject.optJSONArray("fares");
    }

    private void insertFares(JSONArray fares, String origin, String destination, int originId, int destinationId) {
        int limit = Math.min(25, fares.length());
        for (int i = 0; i < limit; i++) {
            saveSingleFare(fares.getJSONObject(i), origin, destination);
        }
        System.out.println("Inserted " + limit + " fares for " + origin + " -> " + destination + "\n");
    }

    private void saveSingleFare(JSONObject fare, String origin, String destination) {
        String departure = getDeparture(fare);
        double price = fare.optDouble("price_cents", 0) / 100.0;
        boolean available = fare.optBoolean("available", false);

        apiClient.processFareAndSendEvent(origin, destination, departure, price, available ? 1 : 0);
    }

    private String getDeparture(JSONObject fare) {
        JSONArray legs = fare.optJSONArray("legs");
        return legs != null && !legs.isEmpty() ? legs.getJSONObject(0).optString("departure", null) :
                fare.optString("departure", null);
    }

    private interface CityPairAction {
        void execute(String originCity, String destCity, int originId, int destId);
    }
}
