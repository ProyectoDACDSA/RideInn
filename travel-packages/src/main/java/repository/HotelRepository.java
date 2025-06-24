package repository;

import configuration.DatabaseConfig;
import model.Hotel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HotelRepository {
    private static final Logger logger = LoggerFactory.getLogger(HotelRepository.class);
    private static final String INSERT_SQL =
            "INSERT INTO hotels(hotel_name, hotel_key, accommodation_type, " +
                    "url, rating, avg_price_per_night, city) " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_BY_CITY_SQL =
            "SELECT * FROM hotels WHERE city = ? ORDER BY avg_price_per_night";

    public void save(Hotel hotel) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            setParameters(pstmt, hotel);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Error al insertar hotel, ninguna fila afectada");
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    hotel.setId(generatedKeys.getLong(1));
                } else {
                    logger.warn("No se obtuvieron claves generadas para el hotel insertado");
                }
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                logger.info("Hotel ya insertado con mismo precio - Clave: {}", hotel.getKey());
            } else {
                logger.error("Error al guardar hotel en la base de datos", e);
                throw e;
            }
        }
    }

    private void setParameters(PreparedStatement pstmt, Hotel hotel) throws SQLException {
        pstmt.setString(1, hotel.getHotelName());
        pstmt.setString(2, hotel.getKey());
        pstmt.setString(3, hotel.getAccommodationType());
        pstmt.setString(4, hotel.getUrl());
        if (hotel.getRating() != null) {
            pstmt.setDouble(5, hotel.getRating());
        } else {
            pstmt.setNull(5, Types.DOUBLE);
        }
        pstmt.setDouble(6, hotel.getAveragePricePerNight());
        pstmt.setString(7, hotel.getCity());
    }

    public List<Hotel> findByCity(String city) throws SQLException {
        List<Hotel> hotels = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_CITY_SQL)) {
            pstmt.setString(1, city);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Hotel hotel = new Hotel(
                            rs.getLong("id"),
                            rs.getString("hotel_name"),
                            rs.getString("hotel_key"),
                            rs.getString("accommodation_type"),
                            rs.getString("url"),
                            rs.getObject("rating", Double.class),
                            rs.getDouble("avg_price_per_night"),
                            city,
                            rs.getTimestamp("processed_at").toLocalDateTime());
                    hotels.add(hotel);
                }
            }
        }
        return hotels;
    }
}