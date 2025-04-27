package scheduler;

import api.XoteloApiClient;
import database.HotelRepository;
import java.util.Map;
import java.util.concurrent.*;

public class XoteloApiScheduler {
    private final XoteloApiClient apiClient;
    private final HotelRepository repository;

    public XoteloApiScheduler() {
        this(new XoteloApiClient(), new HotelRepository(getDbUrl()));
    }

    public XoteloApiScheduler(XoteloApiClient apiClient, HotelRepository repository) {
        this.apiClient = apiClient;
        this.repository = repository;
    }

    public void start() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::fetchAndSaveHotels, 0, 1, TimeUnit.DAYS);
    }

    public void runOnce() {
        fetchAndSaveHotels();
    }

    private void fetchAndSaveHotels() {
        apiClient.getCityUrls().forEach((city, url) -> {
            String jsonData = apiClient.fetchData(city, url);
            if (jsonData != null) repository.saveHotels(jsonData, city);
        });
    }

    private static String getDbUrl() {
        String dbUrl = System.getenv("DB_URL");
        if (dbUrl == null || dbUrl.isEmpty()) {
            throw new IllegalArgumentException("Falta la variable de entorno DB_URL");
        }
        return dbUrl;
    }
}



