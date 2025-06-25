package application;

import domain.Trip;
import domain.TripEvent;
import ports.TripProvider;
import ports.TripEventStorage;
import java.util.*;
import java.util.logging.Logger;

public class BlablacarTripProvider {
    private static final Logger LOGGER = Logger.getLogger(BlablacarTripProvider.class.getName());

    private final TripProvider apiClient;
    private final TripEventStorage eventStorage;

    public BlablacarTripProvider(TripProvider apiClient, TripEventStorage eventStorage) {
        this.apiClient = apiClient;
        this.eventStorage = eventStorage;
    }

    public void fetchAndProcessAllTrips() {
        apiClient.getCityIds().forEach((originCity, originId) -> {
            apiClient.getCityIds().forEach((destCity, destId) -> {
                if (!originCity.equals(destCity)) {
                    processRoute(originCity, originId, destCity, destId);
                }
            });
        });
    }

    private void processRoute(String originCity, int originId, String destCity, int destId) {
        try {
            System.out.println(String.format("Processing trips from %s to %s", originCity, destCity));
            List<Trip> trips = apiClient.fetchTripsForRoute(originId, destId);
            trips.forEach(this::processTrip);
        } catch (Exception e) {
            LOGGER.warning(String.format("Error processing %s -> %s: %s",
                    originCity, destCity, e.getMessage()));
        }
    }

    private void processTrip(Trip trip) {
        try {
            eventStorage.store(new TripEvent(
                    System.currentTimeMillis(),
                    "Blablacar",
                    trip
            ));
        } catch (Exception e) {
            LOGGER.warning("Error processing trip: " + e.getMessage());
        }
    }
}