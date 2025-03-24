package blablacar_feeder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class blablacarAPI {
    public static void main(String[] args) {
        String apiUrl = "https://bus-api.blablacar.com/v3/stops";
        String apiKey = "RPQn87Lb1z2OPQTwsOQsAQ";

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Token " + apiKey);
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                System.out.println("API connected succesfully");
            } else {
                System.out.println("Connection error: " + responseCode);
            }

            connection.disconnect();
        } catch (Exception e) {
            System.out.println("Connection error: ");
            e.printStackTrace();
        }
    }
}
