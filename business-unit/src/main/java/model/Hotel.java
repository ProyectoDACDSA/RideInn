package model;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDate;

public class Hotel {
    private Long id;
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

    public Hotel(long id, String hotelName, String key, String accommodationType,
                 String url, Double rating, double averagePricePerNight,
                 LocalDate startDate, LocalDate endDate, double totalPrice, String city) {
        this.id = id;
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

    // Asegúrate de que este método exista
    public Double getRating() {
        return rating;
    }

    // Resto de getters
    public long getId() { return id; }
    public String getHotelName() { return hotelName; }
    public String getKey() { return key; }
    public String getUrl() { return url; }
    public String getAccommodationType() { return accommodationType; }
    public double getAveragePricePerNight() { return averagePricePerNight; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public double getTotalPrice() { return totalPrice; }
    public String getCity() { return city; }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("Hotel[id=%d, name=%s, city=%s, rating=%.1f, price=%.2f]",
                id, hotelName, city, rating, totalPrice);
    }
}