package model;

import java.sql.Time;

public class Trip {
    private Long id;
    private long timestamp;
    private String origin;
    private String destination;
    private Time departureTime;
    private double price;
    private int available;

    public Trip(long id, long timestamp, String origin, String destination, Time departureTime, double price, int available) {
        this.id = id;
        this.timestamp = timestamp;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.price = price;
        this.available = available;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public Time getDepartureTime() { return departureTime; }
    public void setDepartureTime(Time departureTime) { this.departureTime = departureTime; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getAvailable() { return available; }
    public void setAvailable(int available) { this.available = available; }

    @Override
    public String toString() {
        return String.format("Trip[id=%d, from=%s to=%s, at=%s, price=%.2f]",
                id, origin, destination, departureTime, price);
    }
}
