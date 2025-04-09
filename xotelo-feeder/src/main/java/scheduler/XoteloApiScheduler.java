package scheduler;

import api.XoteloApiClient;
import database.HotelRepository;

import java.util.Map;
import java.util.concurrent.*;

public class XoteloApiScheduler {
    private final XoteloApiClient apiClient = new XoteloApiClient();
    private final HotelRepository repository;

    public XoteloApiScheduler() {
        String dbUrl = System.getenv("DB_URL");
        if (dbUrl == null || dbUrl.isEmpty()) {
            throw new IllegalArgumentException("Falta la variable de entorno DB_URL");
        }
        this.repository = new HotelRepository(dbUrl);
    }

    public void start() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        Runnable task = () -> {
            Map<String, String> cityUrls = apiClient.getCityUrls();
            for (Map.Entry<String, String> entry : cityUrls.entrySet()) {
                String city = entry.getKey();
                String jsonData = apiClient.fetchData(city, entry.getValue());
                if (jsonData != null) {
                    repository.saveHotels(jsonData, city);
                }
            }
        };
        scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.DAYS);
    }
}


