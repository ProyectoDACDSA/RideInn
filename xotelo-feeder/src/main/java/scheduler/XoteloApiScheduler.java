package scheduler;

import ports.HotelProvider;
import ports.BookingStorage;
import domain.Hotel;
import domain.Booking;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.*;

public class XoteloApiScheduler {
    private final HotelProvider hotelProvider;
    private final BookingStorage bookingStorage;

    public XoteloApiScheduler(HotelProvider hotelProvider, BookingStorage bookingStorage) {
        this.hotelProvider = hotelProvider;
        this.bookingStorage = bookingStorage;
    }

    public void start() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::fetchAndProcessHotels, 0, 1, TimeUnit.DAYS);
    }

    private void fetchAndProcessHotels() {
        hotelProvider.getCityUrls().forEach((city, url) -> {
            List<Hotel> hotels = hotelProvider.fetchHotelsForCity(city, url);
            hotels.forEach(hotel -> {
                Booking booking = new Booking(
                        System.currentTimeMillis(),
                        "Xotelo",
                        hotel,
                        LocalDate.now(),
                        3 // Default stay duration
                );
                bookingStorage.store(booking);
            });
        });
    }
}






