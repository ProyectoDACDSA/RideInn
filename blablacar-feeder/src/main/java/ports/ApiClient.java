package ports;

public interface ApiClient {
    String fetchFare(int originId, int destinationId);
    void processFareAndSendEvent(String origin, String destination,
                                 String departureTime, double price, int available);
}
