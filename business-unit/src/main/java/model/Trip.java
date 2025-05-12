package model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Trip {
    private long id;
    private String origin;
    private String destination;
    private final LocalDate departureDate;
    private final LocalTime departureTime;
    private double price;
    private int available;

    public Trip(String origin, String destination,
                String departureTime,String departureDate, double price, int available) {
        this.origin = origin;
        this.destination = destination;
        this.departureTime = LocalTime.parse(departureTime);
        this.departureDate = LocalDate.parse(departureDate);
        this.price = price;
        this.available = available;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public LocalDate getDepartureDate() {return departureDate;}

    public LocalTime getDepartureTime() {return departureTime;}

    public int getAvailable() { return available; }
    public void setAvailable(int available) { this.available = available; }

    @Override
    public String toString() {
        return String.format("Trip[from=%s to=%s, at=%s, day=%s, price=%.2f]",
                origin, destination, departureTime, departureDate, price);
    }
}
