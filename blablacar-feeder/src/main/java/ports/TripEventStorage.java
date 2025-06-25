package ports;

import domain.TripEvent;

public interface TripEventStorage {
    void store(TripEvent tripEvent);
}
