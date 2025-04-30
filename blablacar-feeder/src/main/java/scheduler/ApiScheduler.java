package scheduler;

import api.BlablacarApiClient;
import database.DatabaseManager;
import events.TripEvent;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class ApiScheduler {
    private final String[] cities = {"Paris", "Estrasburgo", "Lyon", "Niza", "Toulouse"};
    private final int[] cityIds = {90, 27, 137, 210, 16};
    private final BlablacarApiClient apiClient;
    private final DatabaseManager databaseManager;

    public ApiScheduler(BlablacarApiClient apiClient, DatabaseManager databaseManager) {
        this.apiClient = apiClient;
        this.databaseManager = databaseManager;
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
        TripEvent event = new TripEvent(
                origin,
                destination,
                originId,
                destinationId,
                getDeparture(fare),
                getArrival(fare),
                fare.optBoolean("available", false),
                fare.optInt("price_cents", 0),
                fare.optString("price_currency", null),
                fare.optString("updated_at", null)
        );

        databaseManager.saveFare(
                event.getOrigin(),
                event.getDestination(),
                event.getOriginId(),
                event.getDestinationId(),
                event.getDeparture(),
                event.getArrival(),
                event.isAvailable(),
                event.getPriceCents(),
                event.getPriceCurrency(),
                event.getUpdatedAt()
        );

        writeEventToJsonFile(event);
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

    private void writeEventToJsonFile(TripEvent event) {
        try (FileWriter file = new FileWriter("fares.json", true)) {
            org.json.JSONObject eventJson = event.toJson();

            file.write(eventJson.toString() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private interface CityPairAction {
        void execute(int i, int j);
    }
}