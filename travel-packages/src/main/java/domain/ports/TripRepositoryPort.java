package domain.ports;

import domain.model.Trip;
import java.sql.SQLException;
import java.util.List;

public interface TripRepositoryPort {
    void save(Trip trip) throws SQLException;
    List<Trip> findByDestination(String city) throws SQLException;
}