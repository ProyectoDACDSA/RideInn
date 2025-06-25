package adapters;

import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class BlablacarApiClientTest {

    @Test
    public void testGetCityIds() {
        BlablacarApiClient client = new BlablacarApiClient("test-key");
        Map<String, Integer> cityIds = client.getCityIds();

        assertNotNull(cityIds);
        assertFalse(cityIds.isEmpty());
    }
}