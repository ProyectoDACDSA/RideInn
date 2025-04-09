package api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BlablacarApiClient {
    private static final String API_URL = "https://bus-api.blablacar.com/v3/stops";
    private final String apiKey;

    public BlablacarApiClient(String apiKey) {
        this.apiKey = apiKey;
    }

    public String fetchData() throws Exception {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(API_URL);
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
                System.out.println("Conexión exitosa con la API.");
                return response.toString();
            } else {
                System.out.println("Error en la conexión con la API: Código de respuesta " + responseCode);
            }
        } catch (Exception e) {
            System.out.println("Error al conectar con la API:");
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }
}
