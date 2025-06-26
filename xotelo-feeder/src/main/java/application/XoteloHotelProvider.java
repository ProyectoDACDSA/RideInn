package application;

import adapters.XoteloApiClient;
import domain.Hotel;
import ports.HotelProvider;
import java.util.*;
import java.util.logging.Logger;
import com.google.gson.*;

public class XoteloHotelProvider implements HotelProvider {
    private static final Logger LOGGER = Logger.getLogger(XoteloHotelProvider.class.getName());
    private final XoteloApiClient apiClient;

    private static final Map<String, String> CITY_URLS = Map.of(
            "Paris", "https://data.xotelo.com/api/list?location_key=g187147&offset=0&limit=30&sort=best_value",
            "Lyon", "https://data.xotelo.com/api/list?location_key=g187265&offset=0&limit=30&sort=best_value",
            "Toulouse", "https://data.xotelo.com/api/list?location_key=g187175&offset=0&limit=30&sort=best_value",
            "Niza", "https://data.xotelo.com/api/list?location_key=g187234&offset=0&limit=30&sort=best_value",
            "Estrasburgo", "https://data.xotelo.com/api/list?location_key=g187075&offset=0&limit=30&sort=best_value"
    );

    public XoteloHotelProvider(XoteloApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public Map<String, String> getCityUrls() {
        return CITY_URLS;
    }

    @Override
    public List<Hotel> fetchHotelsForCity(String city, String apiUrl) {
        String jsonData = apiClient.fetchHotelData(apiUrl);
        if (jsonData == null) {
            return Collections.emptyList();
        }

        try {
            JsonArray hotelsJson = parseHotelsJson(jsonData);
            List<Hotel> hotels = new ArrayList<>();
            for (JsonElement el : hotelsJson) {
                hotels.add(createHotelFromJson(el.getAsJsonObject(), city));
            }
            return hotels;
        } catch (Exception e) {
            LOGGER.warning("Error processing hotel data for " + city + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private JsonArray parseHotelsJson(String jsonData) {
        return JsonParser.parseString(jsonData)
                .getAsJsonObject()
                .getAsJsonObject("result")
                .getAsJsonArray("list");
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
}