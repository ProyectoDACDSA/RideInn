import ports.HotelProvider;
import ports.BookingStorage;
import domain.Hotel;
import domain.Booking;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.*;

public class Controller {
    private final HotelProvider hotelProvider;
    private final BookingStorage bookingStorage;

    public Controller(HotelProvider hotelProvider, BookingStorage bookingStorage) {
        this.hotelProvider = hotelProvider;
        this.bookingStorage = bookingStorage;
    }

    public void execute() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("Fetching hotel data and creating bookings...");
            hotelProvider.getCityUrls().forEach((city, url) -> {
                List<Hotel> hotels = hotelProvider.fetchHotelsForCity(city, url);
                hotels.forEach(hotel -> {
                    Booking booking = new Booking(
                            System.currentTimeMillis(),
                            "Xotelo",
                            hotel,
                            LocalDate.now(),
                            3
                    );
                    bookingStorage.store(booking);
                    System.out.println("Created booking for: " + hotel.getName());
                });
            });
        }, 0, 1, TimeUnit.DAYS);
    }
}