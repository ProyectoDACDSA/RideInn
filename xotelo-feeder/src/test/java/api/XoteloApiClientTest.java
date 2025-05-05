package api;

import adapters.XoteloApiHotelProvider;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class XoteloApiClientTest {

    @Test
    public void testCityUrlsNotEmpty() {
        XoteloApiHotelProvider client = new XoteloApiHotelProvider();
        assertFalse(client.getCityUrls().isEmpty(), "City URLs should not be empty");
    }

    @Test
    public void testFetchDataReturnsJson() {
        XoteloApiHotelProvider client = new XoteloApiHotelProvider();
        String url = client.getCityUrls().get("Paris");
        assertNotNull(url, "Paris URL should exist in cityUrls");

        String json = client.fetchData("Paris", url);
        assertNotNull(json, "fetchData should return non-null JSON");
        assertFalse(json.isEmpty(), "JSON should contain 'result' or be non-empty");
    }

    @Test
    public void testFetchDataWithInvalidUrl() {
        XoteloApiHotelProvider client = new XoteloApiHotelProvider();
        String json = client.fetchData("FakeCity", "http://invalid.url");
        assertNull(json, "fetchData should return null for invalid URL");
    }

    @Test
    public void testCityUrlsContainsExpectedCities() {
        XoteloApiHotelProvider client = new XoteloApiHotelProvider();
        Map<String, String> urls = client.getCityUrls();

        assertTrue(urls.containsKey("Paris"), "City URLs should contain Paris");
        assertTrue(urls.containsKey("Lyon"), "City URLs should contain Lyon");
        assertTrue(urls.containsKey("Toulouse"), "City URLs should contain Toulouse");
        assertTrue(urls.containsKey("Niza"), "City URLs should contain Niza");
        assertTrue(urls.containsKey("Marsella"), "City URLs should contain Marsella");
    }

}
