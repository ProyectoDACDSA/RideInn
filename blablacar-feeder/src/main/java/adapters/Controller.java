package adapters;

import application.BlablacarTripProvider;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Controller {
    private final BlablacarTripProvider tripProvider;
    private final ScheduledExecutorService scheduler;

    public Controller(BlablacarTripProvider tripProvider) {
        this.tripProvider = tripProvider;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("Fetching and processing trips from Blablacar...");
            tripProvider.fetchAndProcessAllTrips();
        }, 0, 1, TimeUnit.HOURS);
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
        System.out.println("Blablacar feeder stopped.");
    }
}
