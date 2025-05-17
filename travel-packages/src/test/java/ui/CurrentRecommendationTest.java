package ui;

import model.*;
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
        // Creamos mocks de Trip y Hotel
        trip = mock(Trip.class);
        hotel = mock(Hotel.class);

        // Definimos comportamiento esperado para los mocks
        when(trip.getPrice()).thenReturn(150.0);
        when(hotel.getTotalPrice()).thenReturn(300.0);

        // Creamos la recomendación con los mocks
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
        // Cambiamos el comportamiento del mock del hotel
        when(hotel.getTotalPrice()).thenReturn(350.0);
        // Recalculamos precio total
        recommendation.setTotalPrice();
        assertEquals(500.0, recommendation.getTotalPrice(), 0.001);
    }
}
