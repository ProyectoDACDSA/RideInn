package application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Map;

import org.junit.jupiter.api.Test;
import ports.TripProvider;
import ports.TripEventStorage;

public class BlablacarTripProviderTest {
    @Test
    public void testGetCityIds() {
        TripProvider mockApiClient = mock(TripProvider.class);
        TripEventStorage mockSender = mock(TripEventStorage.class);

        BlablacarTripProvider provider = new BlablacarTripProvider(mockApiClient, mockSender);
        Map<String, Integer> cityIds = provider.getCityIds();

        assertEquals(5, cityIds.size());
        assertEquals(90, cityIds.get("Paris"));
        assertEquals(27, cityIds.get("Estrasburgo"));
    }

    @Test
    public void testFetchAndProcessAllTrips_ValidResponse() {
        String mockResponse = "{\"fares\":[{\"price_cents\":2550,\"available\":true,\"departure\":\"2023-12-01T10:00:00Z\"}]}";

        TripProvider mockApiClient = mock(TripProvider.class);
        when(mockApiClient.fetchFare(anyInt(), anyInt())).thenReturn(mockResponse);

        TripEventStorage mockSender = mock(TripEventStorage.class);

        BlablacarTripProvider provider = new BlablacarTripProvider(mockApiClient, mockSender);
        provider.fetchAndProcessAllTrips();

        verify(mockSender, atLeastOnce()).store(anyString(), anyString(), anyString(), anyDouble(), anyInt());
    }

    @Test
    public void testFetchAndProcessAllTrips_NoFares() {
        String mockResponse = "{\"fares\":[]}";

        TripProvider mockApiClient = mock(TripProvider.class);
        when(mockApiClient.fetchFare(anyInt(), anyInt())).thenReturn(mockResponse);

        TripEventStorage mockSender = mock(TripEventStorage.class);

        BlablacarTripProvider provider = new BlablacarTripProvider(mockApiClient, mockSender);
        provider.fetchAndProcessAllTrips();

        verify(mockSender, never()).store(any(), any(), any(), anyDouble(), anyInt());
    }

    @Test
    public void testFetchAndProcessAllTrips_ApiError() {
        TripProvider mockApiClient = mock(TripProvider.class);
        when(mockApiClient.fetchFare(anyInt(), anyInt())).thenReturn(null);

        TripEventStorage mockSender = mock(TripEventStorage.class);

        BlablacarTripProvider provider = new BlablacarTripProvider(mockApiClient, mockSender);
        assertDoesNotThrow(() -> provider.fetchAndProcessAllTrips());
    }
}
