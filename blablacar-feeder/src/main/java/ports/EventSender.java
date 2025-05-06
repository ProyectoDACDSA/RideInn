package ports;

public interface EventSender {
    void sendEvent(String origin, String destination,
                   String departureTime, double price, int available);
}
