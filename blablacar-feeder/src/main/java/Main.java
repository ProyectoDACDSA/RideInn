import adapters.ActiveMqEventSender;
import adapters.BlablacarApiClient;
import ports.ApiClient;
import ports.EventSender;
import scheduler.BlablacarApiScheduler;

public class Main {
    public static void main(String[] args) {
        String apiKey = System.getenv("BLABLACAR_API_KEY");

        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("Error: BLABLACAR_API_KEY not found in environment variables.");
            return;
        }

        EventSender eventSender = new ActiveMqEventSender();
        ApiClient apiClient = new BlablacarApiClient(apiKey);

        ((BlablacarApiClient) apiClient).setEventSender(eventSender);

        BlablacarApiScheduler scheduler = new BlablacarApiScheduler(apiClient);
        scheduler.start();
    }
}
