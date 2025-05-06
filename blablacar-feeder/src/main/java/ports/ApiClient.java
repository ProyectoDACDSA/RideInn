package ports;
import java.util.Map;

public interface ApiClient {
    Map<String, Integer> getCityIds();
    String fetchFare(int originId, int destinationId);
    void processFareAndSendEvent(String origin, String destination,
                                 String departureTime, double price, int available);
}
