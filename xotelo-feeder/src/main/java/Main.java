import adapters.ActiveMqHotelEventStorage;
import adapters.Controller;
import adapters.XoteloApiClient;
import application.XoteloHotelProvider;
import ports.HotelEventStorage;
import ports.HotelProvider;

public class Main {
    public static void main(String[] args) {
        XoteloApiClient apiClient = new XoteloApiClient();
        HotelProvider hotelProvider = new XoteloHotelProvider(apiClient);
        HotelEventStorage hotelEventStorage = new ActiveMqHotelEventStorage();
        Controller controller = new Controller(hotelProvider, hotelEventStorage);

        controller.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down gracefully...");
            controller.stop();
        }));
    }
}