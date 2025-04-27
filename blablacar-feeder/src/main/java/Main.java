import api.BlablacarApiClient;
import database.DatabaseManager;
import scheduler.ApiScheduler;

public class Main {
    public static void main(String[] args) {
        String apiKey = System.getenv("BLABLACAR_API_KEY");

        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("Error: API_KEY not found in environment variables.");
            return;
        }

        BlablacarApiClient apiClient = new BlablacarApiClient(apiKey);
        DatabaseManager databaseManager = new DatabaseManager();
        ApiScheduler scheduler = new ApiScheduler(apiClient, databaseManager);
        scheduler.start();
    }
}
