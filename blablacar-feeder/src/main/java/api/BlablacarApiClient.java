package api;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BlablacarApiClient {
    private static final String BASE_API_URL = "https://bus-api.blablacar.com/v2/fares";
    private final String apiKey;

    public BlablacarApiClient(String apiKey) {
        this.apiKey = apiKey;
    }

    public String fetchFare(int originId, int destinationId) throws Exception {
        String apiUrl = BASE_API_URL + "?origin_id=" + originId + "&destination_id=" + destinationId;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(apiUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Token " + apiKey);
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                System.out.println("Data received for " + originId + " -> " + destinationId);
                return response.toString();
            } else {
                System.out.println("Error connecting to " + originId + " -> " + destinationId + ": Code " + responseCode);
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }
}