package application;

import domain.Trip;
import org.junit.jupiter.api.Test;
import ports.TripEventStorage;
import ports.TripProvider;
import java.util.List;
import java.util.Map;
import static org.mockito.Mockito.*;

public class BlablacarTripProviderTest {

    @Test
    public void testProcessTrip() {
        TripProvider mockApi = mock(TripProvider.class);
        TripEventStorage mockStorage = mock(TripEventStorage.class);
        when(mockApi.getCityIds()).thenReturn(Map.of("Paris", 1, "Lyon", 2));

        BlablacarTripProvider provider = new BlablacarTripProvider(mockApi, mockStorage);
        provider.fetchAndProcessAllTrips();

        verify(mockApi, atLeastOnce()).fetchTripsForRoute(anyInt(), anyInt());
    }
}