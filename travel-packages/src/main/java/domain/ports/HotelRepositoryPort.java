package domain.ports;

import domain.model.Hotel;
import java.sql.SQLException;
import java.util.List;

public interface HotelRepositoryPort {
    void save(Hotel hotel) throws SQLException;
    List<Hotel> findByCity(String city) throws SQLException;
}