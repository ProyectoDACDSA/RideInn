import api.BlablacarApiClient;
import scheduler.BlablacarApiScheduler;

public class Main {
    public static void main(String[] args) {
        String apiKey = System.getenv("BLABLACAR_API_KEY");

        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("Error: API_KEY not found in environment variables.");
            return;
        }

        BlablacarApiClient apiClient = new BlablacarApiClient(apiKey);
        BlablacarApiScheduler scheduler = new BlablacarApiScheduler(apiClient);
        scheduler.start();
    }
}
