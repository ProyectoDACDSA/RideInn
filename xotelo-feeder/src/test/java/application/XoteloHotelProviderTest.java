package application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import adapters.XoteloApiClient;
import domain.Hotel;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class XoteloHotelProviderTest {

    @Test
    public void testFetchHotelsForCity() {
        XoteloApiClient mockApiClient = mock(XoteloApiClient.class);
        when(mockApiClient.fetchHotelData(anyString()))
                .thenReturn("{\"result\":{\"list\":[{"
                        + "\"name\":\"Hotel A\","
                        + "\"key\":\"key1\","
                        + "\"accommodation_type\":\"Hotel\","
                        + "\"url\":\"http://a.com\","
                        + "\"price_ranges\":{\"minimum\":100,\"maximum\":200},"
                        + "\"review_summary\":{\"rating\":4.5}"
                        + "}]}}");

        XoteloHotelProvider provider = new XoteloHotelProvider(mockApiClient);
        List<Hotel> hotels = provider.fetchHotelsForCity("Paris", "http://fake-api.com");

        assertEquals(1, hotels.size());

        Hotel hotel = hotels.get(0);
        assertEquals("Hotel A", hotel.name());
        assertEquals("key1", hotel.key());
        assertEquals("Hotel", hotel.accommodationType());
        assertEquals("http://a.com", hotel.url());
        assertEquals(100, hotel.priceMin());
        assertEquals(200, hotel.priceMax());
        assertEquals(4.5, hotel.rating(), 0.01);
        assertEquals("Paris", hotel.city());
    }

    @Test
    public void testGetCityUrls() {
        XoteloHotelProvider provider = new XoteloHotelProvider(mock(XoteloApiClient.class));
        Map<String, String> urls = provider.getCityUrls();

        assertEquals(5, urls.size());
        assertTrue(urls.containsKey("Paris"));
        assertEquals("https://data.xotelo.com/api/list?location_key=g187147&offset=0&limit=30&sort=best_value", urls.get("Paris"));
    }
}
