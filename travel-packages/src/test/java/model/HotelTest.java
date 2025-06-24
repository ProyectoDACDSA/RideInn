package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class HotelTest {

    private Hotel hotel;

    @BeforeEach
    void setUp() {
        hotel = new Hotel(
                1L,
                "Hotel Central",
                "H123",
                "Hotel",
                "http://hotel.com",
                4.5,
                100.0,
                "Paris",
                LocalDateTime.of(2024, 5, 17, 12, 0)
        );
    }

    @Test
    void testGetters() {
        assertEquals("Hotel Central", hotel.getHotelName());
        assertEquals("H123", hotel.getKey());
        assertEquals("Hotel", hotel.getAccommodationType());
        assertEquals("http://hotel.com", hotel.getUrl());
        assertEquals(4.5, hotel.getRating());
        assertEquals(100.0, hotel.getAveragePricePerNight());
        assertEquals("Paris", hotel.getCity());
        assertEquals(Timestamp.valueOf(LocalDateTime.of(2024, 5, 17, 12, 0)), hotel.getTimestamp());
    }

    @Test
    void testCalculateTotalPriceWithDefaultNights() {
        hotel.calculateTotalPrice();
        assertEquals(100.0, hotel.getTotalPrice());
    }

    @Test
    void testSetNightsAndRecalculatePrice() {
        hotel.setNights(3);
        hotel.calculateTotalPrice();
        assertEquals(300.0, hotel.getTotalPrice());
    }

    @Test
    void testStartDateAndEndDateDoNotAffectPriceByDefault() {
        hotel.setStartDate(LocalDate.of(2025, 5, 20));
        hotel.setEndDate(LocalDate.of(2025, 5, 23));
        assertEquals(100.0, hotel.getTotalPrice());
    }
}

