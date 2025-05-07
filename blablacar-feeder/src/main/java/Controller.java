import ports.ApiClient;
import ports.EventSender;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class Controller {
    private final ApiClient apiClient;
    private final EventSender eventSender;
    private final ScheduledExecutorService scheduler;

    public Controller(ApiClient apiClient, EventSender eventSender) {
        this.apiClient = apiClient;
        this.eventSender = eventSender;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::fetchAndSendEvents, 0, 1, TimeUnit.HOURS);
        System.out.println("Controller started. Will fetch and send events every hour.");
    }

    public void stop() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        System.out.println("Controller stopped.");
    }

    private void fetchAndSendEvents() {
        System.out.println("Fetching fares from Blablacar API...");
        Map<String, Integer> cityIds = apiClient.getCityIds();
        String[] cities = cityIds.keySet().toArray(new String[0]);

        int maxTrips = 50;
        int tripCount = 0;

        for (int i = 0; i < cities.length && tripCount < maxTrips; i++) {
            for (int j = 0; j < cities.length && tripCount < maxTrips; j++) {
                if (i != j) {
                    try {
                        String response = apiClient.fetchFare(cityIds.get(cities[i]), cityIds.get(cities[j]));
                        if (response != null) {
                            System.out.printf("Successfully fetched fares for %s -> %s%n", cities[i], cities[j]);
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray fares = jsonObject.optJSONArray("fares");
                            if (fares != null) {
                                for (int k = 0; k < fares.length() && tripCount < maxTrips; k++) {
                                    JSONObject fare = fares.getJSONObject(k);
                                    String departure = fare.optString("departure");
                                    double price = fare.optDouble("price_cents", 0) / 100.0;
                                    boolean available = fare.optBoolean("available", false);

                                    apiClient.processFareAndSendEvent(
                                            cities[i],
                                            cities[j],
                                            departure,
                                            price,
                                            available ? 1 : 0
                                    );
                                    tripCount++;
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.err.printf("Error fetching fares for %s -> %s: %s%n",
                                cities[i], cities[j], e.getMessage());
                    }
                }
            }
        }

        System.out.printf("Finished fetching. Total trips processed: %d%n", tripCount);
    }
}
