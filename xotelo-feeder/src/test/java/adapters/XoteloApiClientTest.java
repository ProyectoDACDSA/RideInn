package adapters;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.io.ByteArrayInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

public class XoteloApiClientTest {
    @Test
    public void testFetchHotelData_Success() throws Exception {
        String mockResponse = "{\"result\":{\"list\":[]}}";
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);

        when(mockConnection.getResponseCode()).thenReturn(200);
        when(mockConnection.getInputStream())
                .thenReturn(new ByteArrayInputStream(mockResponse.getBytes()));

        try (MockedConstruction<URL> ignored = mockConstruction(URL.class,
                (mock, context) -> {
                    when(mock.openConnection()).thenReturn(mockConnection);
                })) {

            XoteloApiClient client = new XoteloApiClient();
            String result = client.fetchHotelData("http://fake-api.com");

            assertEquals(mockResponse, result);
            verify(mockConnection).setRequestMethod("GET");
            verify(mockConnection).getInputStream();
        }
    }

    @Test
    public void testFetchHotelData_Failure() throws Exception {
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        when(mockConnection.getResponseCode()).thenReturn(404);

        try (MockedConstruction<URL> ignored = mockConstruction(URL.class,
                (mock, context) -> {
                    when(mock.openConnection()).thenReturn(mockConnection);
                })) {

            XoteloApiClient client = new XoteloApiClient();
            String result = client.fetchHotelData("http://fake-api.com");

            assertNull(result);
            verify(mockConnection).disconnect();
        }
    }

    @Test
    public void testFetchHotelData_IOException() throws Exception {
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        when(mockConnection.getResponseCode()).thenReturn(200);
        when(mockConnection.getInputStream())
                .thenThrow(new java.io.IOException("Simulated error"));

        try (MockedConstruction<URL> ignored = mockConstruction(URL.class,
                (mock, context) -> {
                    when(mock.openConnection()).thenReturn(mockConnection);
                })) {

            XoteloApiClient client = new XoteloApiClient();
            String result = client.fetchHotelData("http://fake-api.com");

            assertNull(result);
        }
    }
}
