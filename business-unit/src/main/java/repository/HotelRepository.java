package repository;

import config.DatabaseConfig;
import model.Hotel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HotelRepository {
    private static final Logger logger = LoggerFactory.getLogger(HotelRepository.class);
    private static final String INSERT_SQL =
            "INSERT INTO hotels(timestamp, hotel_name, hotel_key, accommodation_type, " +
                    "url, rating, avg_price_per_night, start_date, end_date, total_price, city) " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_BY_CITY_SQL =
            "SELECT * FROM hotels WHERE city = ? ORDER BY start_date";

    public void save(Hotel hotel) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            setParameters(pstmt, hotel);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Error al insertar hotel, ninguna fila afectada");
            }
            retrieveGeneratedId(pstmt, hotel);
        } catch (SQLException e) {
            logger.error("Error al guardar hotel en la base de datos", e);
            throw e;
        }
    }

    private void setParameters(PreparedStatement pstmt, Hotel hotel) throws SQLException {
        pstmt.setLong(1, hotel.getTimestamp());
        pstmt.setString(2, hotel.getHotelName());
        pstmt.setString(3, hotel.getKey());
        pstmt.setString(4, hotel.getAccommodationType());
        pstmt.setString(5, hotel.getUrl());
        pstmt.setObject(6, hotel.getRating(), Types.DOUBLE);
        pstmt.setDouble(7, hotel.getAveragePricePerNight());
        pstmt.setString(8, hotel.getStartDate().toString());
        pstmt.setString(9, hotel.getEndDate().toString());
        pstmt.setDouble(10, hotel.getTotalPrice());
        pstmt.setString(11, hotel.getCity());
    }

    //TODO: Establecer key como id
    private void retrieveGeneratedId(PreparedStatement pstmt, Hotel hotel) throws SQLException {
        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                hotel.setId(generatedKeys.getLong(1));
            } else {
                logger.warn("No se obtuvieron claves generadas para el hotel insertado");
            }
        }
    }

    public List<Hotel> findByCity(String city) throws SQLException {
        List<Hotel> hotels = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_CITY_SQL)) {

            pstmt.setString(1, city);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    long id = rs.getLong("id");
                    long timestamp = rs.getLong("timestamp");
                    String hotelName = rs.getString("hotel_name");
                    String key = rs.getString("key");
                    String accommodationType = rs.getString("accommodation_type");
                    String url = rs.getString("url");
                    double rating = rs.getDouble("rating");
                    double averagePricePerNight = rs.getDouble("average_price_per_night");
                    LocalDate startDate = rs.getDate("start_date").toLocalDate();
                    LocalDate endDate = rs.getDate("end_date").toLocalDate();
                    double totalPrice = rs.getDouble("total_price");
                    Hotel hotel = new Hotel(id, timestamp, hotelName, key, accommodationType, url,
                            rating, averagePricePerNight, startDate, endDate,
                            totalPrice, city);
                    hotels.add(hotel);
                }
            }
        }
        return hotels;
    }
}