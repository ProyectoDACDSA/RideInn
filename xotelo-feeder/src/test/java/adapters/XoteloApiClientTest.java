package adapters;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.net.HttpURLConnection;
import java.util.logging.Logger;
import static org.junit.jupiter.api.Assertions.*;

class XoteloApiClientTest {
    private static final Logger LOGGER = Logger.getLogger(XoteloApiClientTest.class.getName());

    @Test
    void testFetchHotelData_success() {
        XoteloApiClient client = new XoteloApiClient() {
            @Override
            protected HttpURLConnection openConnection(String apiUrl) {
                return new MockHttpURLConnection();
            }
        };

        String result = client.fetchHotelData("http://mocked-url");
        assertNotNull(result);
        assertTrue(result.contains("\"result\""));
    }

    @Test
    void testFetchHotelData_httpError() {
        XoteloApiClient client = new XoteloApiClient() {
            @Override
            protected HttpURLConnection openConnection(String apiUrl) {
                return new MockHttpURLConnection(500);
            }
        };

        String result = client.fetchHotelData("http://mocked-url");
        assertNull(result);
    }

    @Test
    void testFetchHotelData_exception() {
        XoteloApiClient client = new XoteloApiClient() {
            @Override
            protected HttpURLConnection openConnection(String apiUrl) throws Exception {
                throw new Exception("Mocked exception");
            }
        };

        String result = client.fetchHotelData("http://mocked-url");
        assertNull(result);
    }

    // MockHttpURLConnection interna para simular respuestas
    private static class MockHttpURLConnection extends HttpURLConnection {
        private final int responseCode;
        private final String responseBody = "{ \"result\": { \"list\": [] } }";

        protected MockHttpURLConnection() {
            super(null);
            this.responseCode = HTTP_OK;
        }

        protected MockHttpURLConnection(int responseCode) {
            super(null);
            this.responseCode = responseCode;
        }

        @Override
        public int getResponseCode() {
            return responseCode;
        }

        @Override
        public java.io.InputStream getInputStream() {
            return new ByteArrayInputStream(responseBody.getBytes());
        }

        @Override
        public void disconnect() { }

        @Override
        public boolean usingProxy() { return false; }

        @Override
        public void connect() { }
    }
}