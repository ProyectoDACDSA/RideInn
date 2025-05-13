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
            "INSERT INTO hotels(hotel_name, hotel_key, accommodation_type, " +
                    "url, rating, avg_price_per_night, start_date, end_date, total_price, city) " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
            if (e.getMessage().contains("UNIQUE constraint failed: hotels.hotel_key, hotels.start_date")) {
                logger.info("Hotel ya insertado - Clave: {}, Fecha inicio: {}", hotel.getKey(), hotel.getStartDate());
            } else {
                logger.error("Error al guardar hotel en la base de datos", e);
            }
        }
    }

    public List<String> getAllCities() throws SQLException {
        List<String> cities = new ArrayList<>();
        String sql = "SELECT DISTINCT city FROM hotels";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                cities.add(rs.getString("city"));
            }
        }
        return cities;
    }

    private void setParameters(PreparedStatement pstmt, Hotel hotel) throws SQLException {
        pstmt.setString(1, hotel.getHotelName());
        pstmt.setString(2, hotel.getKey());
        pstmt.setString(3, hotel.getAccommodationType());
        pstmt.setString(4, hotel.getUrl());
        pstmt.setObject(5, hotel.getRating(), Types.DOUBLE);
        pstmt.setDouble(6, hotel.getAveragePricePerNight());
        pstmt.setString(7, hotel.getStartDate().toString());
        pstmt.setString(8, hotel.getEndDate().toString());
        pstmt.setDouble(9, hotel.getTotalPrice());
        pstmt.setString(10, hotel.getCity());
    }

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
                    Hotel hotel = new Hotel(
                            rs.getLong("id"),
                            rs.getString("hotel_name"),
                            rs.getString("hotel_key"),
                            rs.getString("accommodation_type"),
                            rs.getString("url"),
                            rs.getDouble("rating"),
                            rs.getDouble("avg_price_per_night"),
                            LocalDate.parse(rs.getString("start_date")),
                            LocalDate.parse(rs.getString("end_date")),
                            rs.getDouble("total_price"),
                            city);
                    hotels.add(hotel);
                }
            }
        }
        return hotels;
    }

    public List<Hotel> findByCityAndDateRange(String city, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT * FROM hotels WHERE city = ? AND start_date BETWEEN ? AND ?";
        return List.of();
    }
}