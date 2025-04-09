package scheduler;

import api.BlablacarApiClient;
import database.StopsRepository;

import java.util.concurrent.*;

public class ApiScheduler {
    private final BlablacarApiClient apiClient;
    private final StopsRepository repository;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public ApiScheduler(BlablacarApiClient apiClient, StopsRepository repository) {
        this.apiClient = apiClient;
        this.repository = repository;
    }

    public void start() {
        Runnable task = () -> {
            try {
                String jsonData = apiClient.fetchData();
                if (jsonData != null) {
                    repository.save(jsonData);
                } else {
                    System.out.println("No se pudo obtener datos de la API.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.DAYS);
    }
}
