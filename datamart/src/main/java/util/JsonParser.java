package util;

import com.google.gson.*;
import model.Hotel;
import model.Trip;

import java.sql.Time;
import java.time.LocalDate;

public class JsonParser {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, jsonDeserializationContext) ->
                    LocalDate.parse(json.getAsJsonPrimitive().getAsString()))
            .registerTypeAdapter(Time.class, (JsonDeserializer<Time>) (json, type, jsonDeserializationContext) ->
                    Time.valueOf(json.getAsJsonPrimitive().getAsString()))
            .create();

    public static Trip parseTrip(String json) throws JsonSyntaxException {
        if (json == null || json.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON string cannot be null or empty");
        }

        Trip trip = gson.fromJson(json, Trip.class);

        if (trip.getDestination() == null || trip.getDestination().isEmpty()) {
            throw new IllegalArgumentException("Trip destination cannot be empty");
        }
        if (trip.getDepartureTime() == null) {
            throw new IllegalArgumentException("Departure time cannot be null");
        }
        if (trip.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }

        return trip;
    }

    public static Hotel parseHotel(String json) throws JsonSyntaxException {
        if (json == null || json.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON string cannot be null or empty");
        }

        Hotel hotel = gson.fromJson(json, Hotel.class);

        if (hotel.getCity() == null || hotel.getCity().isEmpty()) {
            throw new IllegalArgumentException("Hotel city cannot be empty");
        }
        if (hotel.getStartDate() == null || hotel.getEndDate() == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }
        if (hotel.getStartDate().isAfter(hotel.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        return hotel;
    }
}
