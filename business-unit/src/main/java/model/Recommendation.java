package model;

public class Recommendation {
    private final Trip trip;
    private final Hotel hotel;
    private final double totalPrice;

    public Recommendation(Trip trip, Hotel hotel, double totalPrice) {
        this.trip = trip;
        this.hotel = hotel;
        this.totalPrice = totalPrice;
    }

    public Trip getTrip() { return trip; }
    public Hotel getHotel() { return hotel; }
    public double getTotalPrice() { return totalPrice; }

    @Override
    public String toString() {
        return String.format("Recomendación: %s + %s | Precio total: %.2f",
                trip, hotel, totalPrice);
    }
}