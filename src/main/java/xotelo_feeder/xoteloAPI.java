package xotelo_feeder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class xoteloAPI {
    public static void main(String[] args) {
        Map<String, String> cityUrls = Map.of(
                "Madrid", "https://data.xotelo.com/api/list?location_key=g187514&offset=0&limit=30&sort=best_value",
                "Barcelona", "https://data.xotelo.com/api/list?location_key=g187497&offset=0&limit=30&sort=best_value",
                "Valencia", "https://data.xotelo.com/api/list?location_key=g187529&offset=0&limit=30&sort=best_value",
                "Granada", "https://data.xotelo.com/api/list?location_key=g187441&offset=0&limit=30&sort=best_value",
                "Seville", "https://data.xotelo.com/api/list?location_key=g187443&offset=0&limit=30&sort=best_value"
        );

        for (Map.Entry<String, String> entry : cityUrls.entrySet()) {
            fetchData(entry.getKey(), entry.getValue());
        }
    }

    private static void fetchData(String city, String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                System.out.println("API connected successfully for " + city );
            } else {
                System.out.println("Connection error for " + city + " (" + apiUrl + "): " + responseCode);
            }

            connection.disconnect();
        } catch (Exception e) {
            System.out.println("Connection error for " + city + " (" + apiUrl + ")");
            e.printStackTrace();
        }
    }
}
