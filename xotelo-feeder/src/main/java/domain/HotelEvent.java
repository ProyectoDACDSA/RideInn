package domain;

import com.google.gson.JsonObject;

public class HotelEvent {
    private final long ts;
    private final String ss;
    private final String hotelName;
    private final String key;
    private final String accommodationType;
    private final String url;
    private final double rating;
    private final int averagePricePerNight;
    private final String city;

    public HotelEvent(long ts, String ss, Hotel hotel) {
        int averagePrice = (hotel.priceMin()+ hotel.priceMax())/2;
        this.ts = ts;
        this.ss = ss;
        this.hotelName = hotel.name();
        this.key = hotel.key();
        this.accommodationType = hotel.accommodationType();
        this.url = hotel.url();
        this.rating = hotel.rating();
        this.averagePricePerNight = averagePrice;
        this.city = hotel.city();
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
