package api;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class XoteloApiClientTest {

    @Test
    public void testCityUrlsNotEmpty() {
        XoteloApiClient client = new XoteloApiClient();
        assertFalse(client.getCityUrls().isEmpty(), "City URLs should not be empty");
    }

    @Test
    public void testFetchDataReturnsJson() {
        XoteloApiClient client = new XoteloApiClient();
        String json = client.fetchData("Madrid", client.getCityUrls().get("Madrid"));
        assertNotNull(json, "fetchData should return non-null JSON");
        assertTrue(json.contains("result"), "JSON should contain 'result'");
    }
}


