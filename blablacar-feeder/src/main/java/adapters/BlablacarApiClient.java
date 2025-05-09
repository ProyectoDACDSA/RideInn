package adapters;

import ports.ApiClient;
import ports.EventSender;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

public class BlablacarApiClient implements ApiClient {
    private static final Logger LOGGER = Logger.getLogger(BlablacarApiClient.class.getName());
    private static final String BASE_API_URL = "https://bus-api.blablacar.com/v2/fares";

    private final String apiKey;
    private EventSender eventSender;

    public BlablacarApiClient(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public String fetchFare(int originId, int destinationId) {
        try {
            HttpURLConnection connection = createConnection(originId, destinationId);
            try {
                return handleResponse(connection, originId, destinationId);
            } finally {
                connection.disconnect();
            }
        } catch (Exception e) {
            LOGGER.warning(String.format("Error fetching fare %d->%d: %s",
                    originId, destinationId, e.getMessage()));
            return null;
        }
    }

    private HttpURLConnection createConnection(int originId, int destinationId) throws Exception {
        String apiUrl = BASE_API_URL + "?origin_id=" + originId + "&destination_id=" + destinationId;
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Token " + apiKey);
        connection.setRequestProperty("Accept", "application/json");
        return connection;
    }

    private String handleResponse(HttpURLConnection connection, int originId, int destinationId) throws Exception {
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            return readResponse(connection);
        }
        LOGGER.warning("Error connecting to " + originId + " -> " + destinationId + ": Code " + responseCode);
        return null;
    }

    private String readResponse(HttpURLConnection connection) throws Exception {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            return response.toString();
        }
    }

    @Override
    public void processFareAndSendEvent(String origin, String destination,
                                        String departureTime, double price, int available) {
        if (eventSender != null) {
            eventSender.sendEvent(origin, destination, departureTime, price, available);
        }
    }

    public void setEventSender(EventSender eventSender) {
        this.eventSender = eventSender;
    }
}
