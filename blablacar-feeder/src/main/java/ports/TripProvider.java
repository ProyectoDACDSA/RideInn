package ports;

import domain.Trip;
import java.util.List;
import java.util.Map;

public interface TripProvider {
    Map<String, Integer> getCityIds();
    List<Trip> fetchTripsForRoute(int originId, int destinationId);
}