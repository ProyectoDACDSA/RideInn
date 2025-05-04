package scheduler;

import api.XoteloApiClient;
import com.google.gson.*;
import publisher.XoteloEventSender;

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
        scheduler.scheduleAtFixedRate(this::fetchAndSaveHotels, 0, 1, TimeUnit.DAYS);
    }

    private void sendHotelsToTopic(String jsonData, String city) {
        XoteloEventSender sender = new XoteloEventSender();
        JsonArray hotels = JsonParser.parseString(jsonData)
                .getAsJsonObject()
                .getAsJsonObject("result")
                .getAsJsonArray("list");

        for (JsonElement el : hotels) {
            JsonObject hotel = el.getAsJsonObject();
            String name = hotel.get("name").getAsString();
            double price = hotel.getAsJsonObject("price_ranges").get("minimum").getAsDouble();

            sender.sendEvent(name, price, city);
        }
    }


    private void fetchAndSaveHotels() {
        XoteloEventSender sender = new XoteloEventSender();
        apiClient.getCityUrls().forEach((city, url) -> {
            String jsonData = apiClient.fetchData(city, url);
            if (jsonData != null) {
                sendHotelsToTopic(jsonData, city);

                JsonArray hotels = JsonParser.parseString(jsonData)
                        .getAsJsonObject()
                        .getAsJsonObject("result")
                        .getAsJsonArray("list");

                for (JsonElement el : hotels) {
                    JsonObject hotel = el.getAsJsonObject();
                    String name = hotel.get("name").getAsString();
                    double price = hotel.getAsJsonObject("price_ranges").get("minimum").getAsDouble();

                    sender.sendEvent(name, price, city);
                }
            }
        });
    }


}



