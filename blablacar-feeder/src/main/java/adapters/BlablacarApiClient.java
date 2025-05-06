package adapters;
import ports.ApiClient;
import ports.EventSender;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class BlablacarApiClient implements ApiClient {
    private static final String BASE_API_URL = "https://bus-api.blablacar.com/v2/fares";
    private static final Map<String, Integer> CITY_IDS = Map.of(
            "Paris", 90,
            "Estrasburgo", 27,
            "Lyon", 137,
            "Niza", 210,
            "Toulouse", 16
    );

    private final String apiKey;
    private EventSender eventSender;

    public BlablacarApiClient(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setEventSender(EventSender eventSender) {
        this.eventSender = eventSender;
    }

    @Override
    public Map<String, Integer> getCityIds() {
        return CITY_IDS;
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
            throw new RuntimeException("Error fetching fare", e);
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
        System.out.println("Error connecting to " + originId + " -> " + destinationId + ": Code " + responseCode);
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
}
