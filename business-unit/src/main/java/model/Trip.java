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
    private final LocalDateTime departureDateTime;
    private double price;
    private int available;

    public Trip(String origin, String destination,
                String departureTime, String departureDate,
                double price, int available) {
        this.origin = Objects.requireNonNull(origin);
        this.destination = Objects.requireNonNull(destination);
        this.departureTime = LocalTime.parse(Objects.requireNonNull(departureTime));
        this.departureDate = LocalDate.parse(Objects.requireNonNull(departureDate));
        this.departureDateTime = LocalDateTime.of(this.departureDate, this.departureTime);
        this.price = price;
        this.available = available;
    }

    public long getId() { return id; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public double getPrice() { return price; }
    public LocalDate getDepartureDate() { return departureDate; }
    public LocalTime getDepartureTime() { return departureTime; }
    public LocalDateTime getDepartureDateTime() { return departureDateTime; }
    public int getAvailable() { return available; }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("Trip[from=%s to=%s, at=%s, day=%s, price=%.2f, available=%d]",
                origin, destination, departureTime, departureDate, price, available);
    }
}