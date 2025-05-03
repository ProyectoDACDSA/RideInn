package api;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class BlablacarApiClientTest {

    @Test
    public void testCrearEvent() {
        // Datos de prueba
        String origin = "Paris";
        String destination = "Lyon";
        String departureTime = "2025-05-01T10:00:00";
        double price = 50.0;
        int seatsAvailable = 10;

        // Crear una instancia de BlablacarApiClient
        BlablacarApiClient apiClient = new BlablacarApiClient("your_api_key");

        // Generar evento
        String eventJson = apiClient.crearTripEventJson(origin, destination, departureTime, price, seatsAvailable);
        System.out.println("Evento generado: " + eventJson);

        // Asegúrate de que el JSON tiene los atributos correctos
        assertTrue(eventJson.contains("\"origin\":\"Paris\""));
        assertTrue(eventJson.contains("\"destination\":\"Lyon\""));
        assertTrue(eventJson.contains("\"price\":50.0"));
        assertTrue(eventJson.contains("\"seatsAvailable\":10"));
    }
}
