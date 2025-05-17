import adapters.ActiveMqHotelEventStorage;
import adapters.XoteloApiClient;
import adapters.XoteloHotelProvider;
import ports.HotelEventStorage;
import ports.HotelProvider;

public class Main {
    public static void main(String[] args) {
        XoteloApiClient apiClient = new XoteloApiClient();
        HotelProvider hotelProvider = new XoteloHotelProvider(apiClient);
        HotelEventStorage hotelEventStorage = new ActiveMqHotelEventStorage();
        Controller controller = new Controller(hotelProvider, hotelEventStorage);

        controller.execute();
        System.out.println("Xotelo Feeder started successfully");
    }
}

