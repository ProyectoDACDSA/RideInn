import adapters.ActiveMqBookingStorage;
import adapters.XoteloApiHotelProvider;
import scheduler.XoteloApiScheduler;
import ports.BookingStorage;
import ports.HotelProvider;

public class Main {
    public static void main(String[] args) {
        HotelProvider hotelProvider = new XoteloApiHotelProvider();
        BookingStorage bookingStorage = new ActiveMqBookingStorage();
        XoteloApiScheduler scheduler = new XoteloApiScheduler(hotelProvider, bookingStorage);

        scheduler.start();

        System.out.println("Xotelo Feeder started successfully");
    }
}

