package domain;

import com.google.gson.JsonObject;
import java.time.ZonedDateTime;

public class TripEvent {
    private final long ts;
    private final String ss;
    private final ZonedDateTime departure;
    private final boolean available;
    private final float price;
    private final String originCity;
    private final String destinationCity;

    public TripEvent(long ts, String ss, Trip trip){
        this.ts = ts;
        this.ss = ss;
        this.departure = trip.departure();
        this.available = trip.available();
        this.price = (float) trip.priceCents() /100;
        this.originCity = trip.originCity();
        this.destinationCity = trip.destinationCity();
    }

    public boolean isAvailable() {return available;}
    public float getPrice() {return price;}
    public String getOriginCity() {return originCity;}
    public String getDestinationCity() {return destinationCity;}

    public String toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("ts", ts);
        json.addProperty("ss", ss);
        json.addProperty("origin", originCity);
        json.addProperty("destination", destinationCity);
        json.addProperty("departureTime", String.valueOf(departure));
        json.addProperty("price", price);
        json.addProperty("avalable", available);
        return json.toString();
    }
}
