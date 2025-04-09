package api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Logger;

public class XoteloApiClient {
    private static final Logger LOGGER = Logger.getLogger(XoteloApiClient.class.getName());

    private static final Map<String, String> cityUrls = Map.of(
            "Madrid", "https://data.xotelo.com/api/list?location_key=g187514&offset=0&limit=30&sort=best_value",
            "Barcelona", "https://data.xotelo.com/api/list?location_key=g187497&offset=0&limit=30&sort=best_value",
            "Valencia", "https://data.xotelo.com/api/list?location_key=g187529&offset=0&limit=30&sort=best_value",
            "Granada", "https://data.xotelo.com/api/list?location_key=g187441&offset=0&limit=30&sort=best_value",
            "Seville", "https://data.xotelo.com/api/list?location_key=g187443&offset=0&limit=30&sort=best_value"
    );

    public Map<String, String> getCityUrls() {
        return cityUrls;
    }

    public String fetchData(String city, String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                LOGGER.info("API connected successfully for " + city);
                return response.toString();
            } else {
                LOGGER.warning("Connection error for " + city + ": " + responseCode);
            }
            connection.disconnect();
        } catch (Exception e) {
            LOGGER.warning("Connection error for " + city);
            e.printStackTrace();
        }
        return null;
    }
}


