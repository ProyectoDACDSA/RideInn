package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.Objects;

public class Trip {
    private long id;
    private String origin;
    private String destination;
    private final LocalDate departureDate;
    private final LocalTime departureTime;
    private final LocalDateTime departureDateTime; // Ahora es final
    private double price;
    private int available;

    public Trip(String origin, String destination,
                String departureTime, String departureDate,
                double price, int available) {
        this.origin = Objects.requireNonNull(origin, "Origin cannot be null");
        this.destination = Objects.requireNonNull(destination, "Destination cannot be null");
        this.departureTime = LocalTime.parse(Objects.requireNonNull(departureTime));
        this.departureDate = LocalDate.parse(Objects.requireNonNull(departureDate));
        this.departureDateTime = LocalDateTime.of(this.departureDate, this.departureTime);
        this.price = price;
        this.available = available;
    }

    public Trip(String origin, String destination,
                LocalTime departureTime, LocalDate departureDate,
                double price, int available) {
        this.origin = Objects.requireNonNull(origin);
        this.destination = Objects.requireNonNull(destination);
        this.departureTime = Objects.requireNonNull(departureTime);
        this.departureDate = Objects.requireNonNull(departureDate);
        this.departureDateTime = LocalDateTime.of(departureDate, departureTime);
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

    public LocalDate getDepartureDate() { return departureDate; }

    public LocalTime getDepartureTime() { return departureTime; }

    public LocalDateTime getDepartureDateTime() { return departureDateTime; }

    public int getAvailable() { return available; }
    public void setAvailable(int available) { this.available = available; }

    @Override
    public String toString() {
        return String.format("Trip[from=%s to=%s, at=%s, day=%s, price=%.2f, available=%d]",
                origin, destination, departureTime, departureDate, price, available);
    }
}