package scheduler;

import api.XoteloApiClient;
import api.Hotel;
import publisher.Booking;
import publisher.XoteloEventSender;
import com.google.gson.*;
import java.time.LocalDate;
import java.util.concurrent.*;

public class XoteloApiScheduler {
    private final XoteloApiClient apiClient;

    public XoteloApiScheduler() {
        this(new XoteloApiClient());
    }

    public XoteloApiScheduler(XoteloApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public void start() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::fetchAndProcessHotels, 0, 1, TimeUnit.DAYS);
    }

    private void processHotels(String jsonData, String city) {
        try {
            JsonArray hotels = JsonParser.parseString(jsonData)
                    .getAsJsonObject()
                    .getAsJsonObject("result")
                    .getAsJsonArray("list");

            XoteloEventSender sender = new XoteloEventSender();
            LocalDate today = LocalDate.now();
            int defaultStayDays = 3; // Default stay duration

            for (JsonElement el : hotels) {
                JsonObject hotelJson = el.getAsJsonObject();
                Hotel hotel = createHotelFromJson(hotelJson, city);
                Booking booking = new Booking(
                        System.currentTimeMillis(),
                        "Xotelo",
                        hotel,
                        today,
                        defaultStayDays
                );
                sender.sendBookingEvent(booking);
            }
        } catch (Exception e) {
            System.err.println("Error processing hotels for city " + city + ": " + e.getMessage());
        }
    }

    private Hotel createHotelFromJson(JsonObject hotelJson, String city) {
        String name = hotelJson.get("name").getAsString();
        String key = hotelJson.get("key").getAsString();
        String accommodationType = hotelJson.get("accommodation_type").getAsString();
        String url = hotelJson.get("url").getAsString();

        JsonObject priceRanges = hotelJson.getAsJsonObject("price_ranges");
        int priceMin = priceRanges.get("minimum").getAsInt();
        int priceMax = priceRanges.get("maximum").getAsInt();

        JsonObject reviewSummary = hotelJson.getAsJsonObject("review_summary");
        double rating = reviewSummary.get("rating").getAsDouble();

        return new Hotel(name, key, priceMin, priceMax, rating, accommodationType, url, city);
    }

    private void fetchAndProcessHotels() {
        apiClient.getCityUrls().forEach((city, url) -> {
            String jsonData = apiClient.fetchData(city, url);
            if (jsonData != null) {
                processHotels(jsonData, city);
            }
        });
    }
}






