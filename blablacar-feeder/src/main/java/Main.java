import adapters.ActiveMqEventSender;
import adapters.BlablacarApiClient;
import adapters.BlablacarTripProvider;
import ports.EventSender;

public class Main {
    public static void main(String[] args) {
        String apiKey = System.getenv("BLABLACAR_API_KEY");

        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("Error: BLABLACAR_API_KEY not found in environment variables.");
            return;
        }

        EventSender eventSender = new ActiveMqEventSender();
        BlablacarApiClient apiClient = new BlablacarApiClient(apiKey);
        BlablacarTripProvider tripProvider = new BlablacarTripProvider(apiClient, eventSender);

        Controller controller = new Controller(tripProvider);
        controller.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down gracefully...");
            controller.stop();
        }));
    }
}
