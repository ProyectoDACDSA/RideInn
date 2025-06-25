package application;

import domain.model.Hotel;
import domain.model.Recommendation;
import domain.model.Trip;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CurrentRecommendationTest {

    private Trip trip;
    private Hotel hotel;
    private Recommendation recommendation;

    @BeforeEach
    void setUp() {
        trip = mock(Trip.class);
        hotel = mock(Hotel.class);

        when(trip.getPrice()).thenReturn(150.0);
        when(hotel.getTotalPrice()).thenReturn(300.0);

        recommendation = new Recommendation(trip, hotel, trip.getPrice() + hotel.getTotalPrice());
    }

    @Test
    void testGetTrip() {
        assertEquals(trip, recommendation.getTrip());
    }

    @Test
    void testGetHotel() {
        assertEquals(hotel, recommendation.getHotel());
    }

    @Test
    void testGetTotalPrice() {
        assertEquals(450.0, recommendation.getTotalPrice(), 0.001);
    }

    @Test
    void testSetTotalPrice_Recalculate() {
        when(hotel.getTotalPrice()).thenReturn(350.0);
        recommendation.setTotalPrice();
        assertEquals(500.0, recommendation.getTotalPrice(), 0.001);
    }
}

