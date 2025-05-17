import domain.HotelEvent;
import ports.HotelProvider;
import ports.HotelEventStorage;
import domain.Hotel;

import java.util.List;
import java.util.concurrent.*;

public class Controller {
    private final HotelProvider hotelProvider;
    private final HotelEventStorage hotelEventStorage;

    public Controller(HotelProvider hotelProvider, HotelEventStorage hotelEventStorage) {
        this.hotelProvider = hotelProvider;
        this.hotelEventStorage = hotelEventStorage;
    }

    public void execute() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("Fetching hotel data and creating bookings...");
            hotelProvider.getCityUrls().forEach((city, url) -> {
                List<Hotel> hotels = hotelProvider.fetchHotelsForCity(city, url);
                hotels.forEach(hotel -> {
                    HotelEvent hotelEvent = new HotelEvent(
                            System.currentTimeMillis(),
                            "Xotelo",
                            hotel
                    );
                    hotelEventStorage.store(hotelEvent);
                    System.out.println("Created hotelEvent for: " + hotel.name());
                });
            });
        }, 0, 1, TimeUnit.DAYS);
    }
}