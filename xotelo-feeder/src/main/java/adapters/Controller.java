package adapters;

import domain.HotelEvent;
import ports.HotelProvider;
import ports.HotelEventStorage;
import domain.Hotel;
import java.util.List;
import java.util.concurrent.*;

public class Controller {
    private final HotelProvider hotelProvider;
    private final HotelEventStorage hotelEventStorage;
    private final ScheduledExecutorService scheduler;

    public Controller(HotelProvider hotelProvider, HotelEventStorage hotelEventStorage) {
        this.hotelProvider = hotelProvider;
        this.hotelEventStorage = hotelEventStorage;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::fetchAndStoreHotelEvents,
                0,
                1,
                TimeUnit.HOURS);
    }

    public void stop() {scheduler.shutdown();}

    private void fetchAndStoreHotelEvents() {
        System.out.println("Fetching hotel data...");
        hotelProvider.getCityUrls().forEach((city, url) -> {
            List<Hotel> hotels = hotelProvider.fetchHotelsForCity(city, url);
            hotels.forEach(hotel -> {
                HotelEvent hotelEvent = new HotelEvent(
                        System.currentTimeMillis(),
                        "Xotelo",
                        hotel
                );
                hotelEventStorage.store(hotelEvent);
            });
        });
    }
}