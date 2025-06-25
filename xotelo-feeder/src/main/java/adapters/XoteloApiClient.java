package adapters;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

public class XoteloApiClient {
    private static final Logger LOGGER = Logger.getLogger(XoteloApiClient.class.getName());

    public String fetchHotelData(String apiUrl) {
        try {
            HttpURLConnection connection = openConnection(apiUrl);
            return readResponse(connection);
        } catch (Exception e) {
            LOGGER.warning("Connection error: " + e.getMessage());
            return null;
        }
    }

    protected HttpURLConnection openConnection(String apiUrl) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        return connection;
    }

    private String readResponse(HttpURLConnection connection) throws Exception {
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            LOGGER.warning("API request failed with code: " + responseCode);
            return null;
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }
        return response.toString();
    }
}
