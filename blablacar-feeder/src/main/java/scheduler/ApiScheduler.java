package scheduler;
import api.BlablacarApiClient;
import database.DatabaseManager;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Timer;
import java.util.TimerTask;

public class ApiScheduler {
    private final String[] cities = {"Paris", "Marsella", "Lyon", "Niza", "Toulouse"};
    private final int[] cityIds = {90, 1633, 137, 210, 16};
    private final BlablacarApiClient apiClient;
    private final DatabaseManager databaseManager;

    public ApiScheduler(BlablacarApiClient apiClient, DatabaseManager databaseManager) {
        this.apiClient = apiClient;
        this.databaseManager = databaseManager;
    }

    public void start() {
        updateAllFares();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                updateAllFares();
            }
        };
        long delay = 24 * 60 * 60 * 1000;
        timer.scheduleAtFixedRate(task, delay, delay);
        System.out.println("Scheduled daily update.");
    }

    private void updateAllFares() {
        System.out.println("Updating routes..." + "\n");
        for (int i = 0; i < cities.length; i++) {
            for (int j = 0; j < cities.length; j++) {
                if (i != j) { // Evitar rutas a sí mismas
                    try {
                        String response = apiClient.fetchFare(cityIds[i], cityIds[j]);
                        if (response != null) {
                            parseAndSave(response, cities[i], cities[j], cityIds[i], cityIds[j]);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        System.out.println("Update completed.");
    }

    private void parseAndSave(String jsonResponse, String origin, String destination, int originId, int destinationId) {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray faresArray = jsonObject.optJSONArray("fares");

        if (faresArray != null && faresArray.length() > 0) {
            JSONObject fare = faresArray.getJSONObject(0); // Tomamos el primer fare
            String updatedAt = fare.optString("updated_at", null);
            boolean available = fare.optBoolean("available", false);
            int priceCents = fare.optInt("price_cents", 0);
            String priceCurrency = fare.optString("price_currency", null);

            JSONArray legs = fare.optJSONArray("legs");
            String departure = null;
            String arrival = null;
            if (legs != null && legs.length() > 0) {
                departure = legs.getJSONObject(0).optString("departure", null);
                arrival = legs.getJSONObject(legs.length() - 1).optString("arrival", null);
            }

            databaseManager.saveFare(origin, destination, originId, destinationId,
                    departure, arrival, available, priceCents, priceCurrency, updatedAt, jsonResponse);
        } else {
            System.out.println("There are no fares available for " + origin + " -> " + destination + "\n");
        }
    }
}
