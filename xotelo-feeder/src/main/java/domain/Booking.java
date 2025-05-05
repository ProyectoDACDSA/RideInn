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
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalPrice;
    private String city;

    public Booking(long ts, String ss, Hotel hotel,
                   LocalDate today, int days) {
        int averagePrice = (hotel.getPriceMin()+ hotel.getPriceMax())/2;
        this.ts = ts;
        this.ss = ss;
        this.hotelName = hotel.getName();
        this.key = hotel.getKey();
        this.accommodationType = hotel.getAccommodationType();
        this.url = hotel.getUrl();
        this.rating = hotel.getRating();
        this.averagePricePerNight = averagePrice;
        this.startDate = today;
        this.endDate = today.plusDays(days);
        this.totalPrice = averagePrice*days;
        this.city = hotel.getCity();
    }

    public long getTs() { return ts; }
    public void setTs(long ts) { this.ts = ts; }

    public String getSs() { return ss; }
    public void setSs(String ss) { this.ss = ss; }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getHotelName() { return hotelName;}
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }

    public String getAccommodationType() { return accommodationType;}
    public void setAccommodationType(String accommodationType) { this.accommodationType = accommodationType;}

    public String getUrl() {return url;}
    public void setUrl(String url) { this.url = url;}

    public double getRating() {return rating;}
    public void setRating(double rating) {this.rating = rating;}

    public int getAveragePricePerNight() {return averagePricePerNight;}
    public void setAveragePricePerNight(int averagePrice) {this.averagePricePerNight = averagePrice;}

    public LocalDate getStartDate() {return startDate;}
    public void setStartDate(LocalDate startDate) {this.startDate = startDate;}

    public LocalDate getEndDate() {return endDate;}
    public void setEndDate(LocalDate endDate) {this.endDate = endDate;}

    public int getTotalPrice() {return totalPrice;}
    public void setTotalPrice(int totalPrice) {this.totalPrice = totalPrice;}

    public String getCity() {return city;}
    public void setCity(String city) {this.city = city;}

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
        json.addProperty("startDate", startDate.toString());
        json.addProperty("endDate", endDate.toString());
        json.addProperty("totalPrice", totalPrice);
        json.addProperty("city", city);
        return json.toString();
    }
}
