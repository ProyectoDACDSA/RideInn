import adapters.ActiveMqTripEventStorage;
import adapters.BlablacarApiClient;
import adapters.Controller;
import application.BlablacarTripProvider;
import ports.TripEventStorage;
import ports.TripProvider;

public class Main {
    public static void main(String[] args) {
        String apiKey = System.getenv("BLABLACAR_API_KEY");

        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("Error: BLABLACAR_API_KEY not found in environment variables.");
            return;
        }

        TripEventStorage eventStorage = new ActiveMqTripEventStorage();
        TripProvider apiClient = new BlablacarApiClient(apiKey);
        BlablacarTripProvider tripProvider = new BlablacarTripProvider(apiClient, eventStorage);

        Controller controller = new Controller(tripProvider);
        controller.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down gracefully...");
            controller.stop();
        }));
    }
}
