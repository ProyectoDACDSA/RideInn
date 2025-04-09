import api.BlablacarApiClient;
import database.StopsRepository;
import scheduler.ApiScheduler;

public class Main {
    public static void main(String[] args) {
        String apiKey = System.getenv("BLABLACAR_API_KEY");
        String dbUrl = System.getenv("DB_URL");

        if (apiKey == null || dbUrl == null || apiKey.isEmpty() || dbUrl.isEmpty()) {
            System.err.println("Error: Faltan variables de entorno.");
            System.exit(1);
        }

        BlablacarApiClient client = new BlablacarApiClient(apiKey);
        StopsRepository repository = new StopsRepository(dbUrl);
        ApiScheduler scheduler = new ApiScheduler(client, repository);

        scheduler.start();
    }
}