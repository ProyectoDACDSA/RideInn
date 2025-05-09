import adapters.ActiveMqBookingStorage;
import adapters.XoteloApiClient;
import adapters.XoteloHotelProvider;
import ports.BookingStorage;
import ports.HotelProvider;

public class Main {
    public static void main(String[] args) {
        XoteloApiClient apiClient = new XoteloApiClient();
        HotelProvider hotelProvider = new XoteloHotelProvider(apiClient);
        BookingStorage bookingStorage = new ActiveMqBookingStorage();
        Controller controller = new Controller(hotelProvider, bookingStorage);

        controller.execute();
        System.out.println("Xotelo Feeder started successfully");
    }
}

