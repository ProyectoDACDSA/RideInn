package adapters;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.io.ByteArrayInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import ports.TripEventStorage;

public class BlablacarApiClientTest {
    private static final String API_KEY = "test-api-key";

    @Test
    public void testFetchFare_Success() throws Exception {
        String mockResponse = "{\"fares\":[{\"price_cents\":2550,\"available\":true}]}";
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);

        when(mockConnection.getResponseCode()).thenReturn(200);
        when(mockConnection.getInputStream())
                .thenReturn(new ByteArrayInputStream(mockResponse.getBytes()));

        try (MockedConstruction<URL> ignored = mockConstruction(URL.class,
                (mock, context) -> {
                    when(mock.openConnection()).thenReturn(mockConnection);
                })) {

            BlablacarApiClient client = new BlablacarApiClient(API_KEY);
            String result = client.fetchFare(90, 137);

            assertEquals(mockResponse, result);
            verify(mockConnection).setRequestMethod("GET");
            verify(mockConnection).setRequestProperty("Authorization", "Token " + API_KEY);
        }
    }

    @Test
    public void testFetchFare_Non200Response() throws Exception {
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        when(mockConnection.getResponseCode()).thenReturn(404);

        try (MockedConstruction<URL> ignored = mockConstruction(URL.class,
                (mock, context) -> {
                    when(mock.openConnection()).thenReturn(mockConnection);
                })) {

            BlablacarApiClient client = new BlablacarApiClient(API_KEY);
            String result = client.fetchFare(90, 137);

            assertNull(result);
            verify(mockConnection).disconnect();
        }
    }

    @Test
    public void testProcessFareAndSendEvent_WithSender() {
        TripEventStorage mockSender = mock(TripEventStorage.class);
        BlablacarApiClient client = new BlablacarApiClient(API_KEY);
        client.setEventSender(mockSender);

        client.processFareAndSendEvent("Paris", "Lyon", "2023-12-01T10:00:00Z", 25.50, 1);

        verify(mockSender).store("Paris", "Lyon", "2023-12-01T10:00:00Z", 25.50, 1);
    }

    @Test
    public void testProcessFareAndSendEvent_NoSender() {
        BlablacarApiClient client = new BlablacarApiClient(API_KEY);

        assertDoesNotThrow(() ->
                client.processFareAndSendEvent("Paris", "Lyon", "2023-12-01T10:00:00Z", 25.50, 1));
    }
}
