package adapters;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Map;

import domain.Hotel;
import org.junit.jupiter.api.Test;
import com.google.gson.JsonObject;

public class XoteloHotelProviderTest {
    @Test
    public void testFetchHotelsForCity() {
        XoteloApiClient mockApiClient = mock(XoteloApiClient.class);
        when(mockApiClient.fetchHotelData(anyString()))
                .thenReturn("{\"result\":{\"list\":[{\"name\":\"Hotel A\",\"key\":\"key1\",\"accommodation_type\":\"Hotel\",\"url\":\"http://a.com\",\"price_ranges\":{\"minimum\":100,\"maximum\":200},\"review_summary\":{\"rating\":4.5}}]}}");

        XoteloHotelProvider provider = new XoteloHotelProvider(mockApiClient);
        List<Hotel> hotels = provider.fetchHotelsForCity("Paris", "http://fake-api.com");

        assertEquals(1, hotels.size());
        assertEquals("Hotel A", hotels.get(0).getName());
        assertEquals(100, hotels.get(0).getPriceMin());
    }

    @Test
    public void testGetCityUrls() {
        XoteloHotelProvider provider = new XoteloHotelProvider(mock(XoteloApiClient.class));
        Map<String, String> urls = provider.getCityUrls();

        assertEquals(5, urls.size());
        assertTrue(urls.containsKey("Paris"));
    }
}