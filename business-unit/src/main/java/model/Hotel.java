package model;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDate;

public class Hotel {
    private Long id;
    private long timestamp;
    @SerializedName(value = "hotelName", alternate = "hotel")
    private String hotelName;
    private String key;
    private String accommodationType;
    private String url;
    private Double rating;
    private double averagePricePerNight;
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalPrice;
    private String city;

    public Hotel(long id, long timestamp, String hotelName, String key, String accommodationType, String url, Double rating, double averagePricePerNight, LocalDate startDate, LocalDate endDate, double totalPrice, String city) {
        this.id = id;
        this.timestamp = timestamp;
        this.hotelName = hotelName;
        this.key = key;
        this.accommodationType = accommodationType;
        this.url = url;
        this.rating = rating;
        this.averagePricePerNight = averagePricePerNight;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.city = city;
    }

    public long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public long getTimestamp() {return timestamp;}
    public void setTimestamp(long timestamp) {this.timestamp = timestamp;}

    public String getHotelName() {return hotelName;}
    public void setHotelName(String hotelName) {this.hotelName = hotelName;}

    public String getKey() {return key;}
    public void setKey(String key) {this.key = key;}

    public String getUrl() {return url;}
    public void setUrl(String url) {this.url = url;}

    public String getAccommodationType() {return accommodationType;}
    public void setAccommodationType(String accommodationType) {this.accommodationType = accommodationType;}

    public Double getRating() {return rating;}
    public void setRating(Double rating) {this.rating = rating;}

    public double getAveragePricePerNight() {return averagePricePerNight;}
    public void setAveragePricePerNight(double averagePricePerNight) {this.averagePricePerNight = averagePricePerNight;}

    public LocalDate getStartDate() {return startDate;}
    public void setStartDate(LocalDate startDate) {this.startDate = startDate;}

    public LocalDate getEndDate() {return endDate;}
    public void setEndDate(LocalDate endDate) {this.endDate = endDate;}

    public double getTotalPrice() {return totalPrice;}
    public void setTotalPrice(double totalPrice) {this.totalPrice = totalPrice;}

    public String getCity() {return city;}
    public void setCity(String city) {this.city = city;}


    @Override
    public String toString() {
        return String.format("Hotel[id=%d, name=%s, city=%s, rating=%.1f, price=%.2f]",
                id, hotelName, city, rating, totalPrice);
    }
}