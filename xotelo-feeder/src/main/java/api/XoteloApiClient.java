package api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;
import java.util.Date;

public class XoteloApiClient {
    private static final Logger LOGGER = Logger.getLogger(XoteloApiClient.class.getName());
    private static final SimpleDateFormat SDF = new SimpleDateFormat("MMM dd, yyyy h:mm:ss a");

    private static final Map<String, String> CITY_URLS = Map.of(
            "Paris", "https://data.xotelo.com/api/list?location_key=g187147&offset=0&limit=30&sort=best_value",
            "Lyon", "https://data.xotelo.com/api/list?location_key=g187265&offset=0&limit=30&sort=best_value",
            "Toulouse", "https://data.xotelo.com/api/list?location_key=g187175&offset=0&limit=30&sort=best_value",
            "Niza", "https://data.xotelo.com/api/list?location_key=g187234&offset=0&limit=30&sort=best_value",
            "Estrasburgo", "https://data.xotelo.com/api/list?location_key=g187075&offset=0&limit=30&sort=best_value"
    );

    public Map<String, String> getCityUrls() {
        return CITY_URLS;
    }

    public String fetchData(String city, String apiUrl) {
        try {
            HttpURLConnection connection = openConnection(apiUrl);
            return readResponse(connection, city);
        } catch (Exception e) {
            LOGGER.warning("Connection error for " + city);
            return null;
        }
    }

    private HttpURLConnection openConnection(String apiUrl) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        return connection;
    }

    private String readResponse(HttpURLConnection connection, String city) throws Exception {
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            LOGGER.warning("Connection error for " + city + ": " + responseCode);
            return null;
        }
        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }
        logSuccess(city);
        return response.toString();
    }

    private void logSuccess(String city) {
        String timestamp = SDF.format(new Date());
        System.out.println(timestamp + " - API connected successfully for " + city);
    }
}

