package domain;

import com.google.gson.JsonObject;
import java.time.LocalDate;

public class Booking {
    private long ts;
    private String ss;
    private String hotelName;
    private String key;
    private String accommodationType;
    private String url;
    private double rating;
    private int averagePricePerNight;
    private String city;

    public Booking(long ts, String ss, Hotel hotel) {
        int averagePrice = (hotel.getPriceMin()+ hotel.getPriceMax())/2;
        this.ts = ts;
        this.ss = ss;
        this.hotelName = hotel.getName();
        this.key = hotel.getKey();
        this.accommodationType = hotel.getAccommodationType();
        this.url = hotel.getUrl();
        this.rating = hotel.getRating();
        this.averagePricePerNight = averagePrice;
        this.city = hotel.getCity();
    }

    public long getTs() { return ts; }

    public String getSs() { return ss; }

    public String getKey() { return key; }

    public String getHotelName() { return hotelName;}

    public String getAccommodationType() { return accommodationType;}

    public String getUrl() {return url;}

    public double getRating() {return rating;}

    public int getAveragePricePerNight() {return averagePricePerNight;}

    public String getCity() {return city;}

    public String toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("ts", ts);
        json.addProperty("ss", ss);
        json.addProperty("hotelName", hotelName);
        json.addProperty("key", key);
        json.addProperty("accommodationType", accommodationType);
        json.addProperty("url", url);
        json.addProperty("rating", rating);
        json.addProperty("averagePricePerNight", averagePricePerNight);
        json.addProperty("city", city);
        return json.toString();
    }
}
