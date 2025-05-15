package model;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Hotel {
    private Long id;
    @SerializedName(value = "hotelName", alternate = "hotel")
    private final String hotelName;
    private final String key;
    private final String accommodationType;
    private final String url;
    private final Double rating;
    private final double averagePricePerNight;
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalPrice;
    private final String city;
    private final LocalDateTime timestamp;

    public Hotel(long id, String hotelName, String key, String accommodationType,
                 String url, Double rating, double averagePricePerNight, String city, LocalDateTime timestamp) {
        this.id = id;
        this.hotelName = hotelName;
        this.key = key;
        this.accommodationType = accommodationType;
        this.url = url;
        this.rating = rating;
        this.averagePricePerNight = averagePricePerNight;
        this.city = city;
        this.timestamp = timestamp;
    }

    public Double getRating() {
        return rating;
    }
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
    public Timestamp getTimestamp(){return Timestamp.valueOf(timestamp);}

    public void setId(Long id) {
        this.id = id;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        calculateTotalPrice();
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        calculateTotalPrice();
    }

    public void calculateTotalPrice() {
        if (this.startDate != null && this.endDate != null && !this.endDate.isBefore(this.startDate)) {
            long nights = ChronoUnit.DAYS.between(this.startDate, this.endDate);
            this.totalPrice = this.averagePricePerNight * nights;
        } else {
            this.totalPrice = this.averagePricePerNight;
        }
    }


    @Override
    public String toString() {
        return String.format("Hotel[id=%d, name=%s, city=%s, rating=%.1f, price=%.2f]",
                id, hotelName, city, rating, totalPrice);
    }
}