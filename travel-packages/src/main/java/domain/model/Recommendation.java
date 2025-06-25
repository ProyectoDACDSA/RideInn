package domain.model;

public class Recommendation {
    private final Trip trip;
    private final Hotel hotel;
    private double totalPrice;

    public Recommendation(Trip trip, Hotel hotel, double totalPrice) {
        this.trip = trip;
        this.hotel = hotel;
        this.totalPrice = totalPrice;
    }

    public Trip getTrip() { return trip; }
    public Hotel getHotel() { return hotel; }
    public double getTotalPrice() { return totalPrice; }

    public void setTotalPrice(){
        this.totalPrice = this.hotel.getTotalPrice() + this.trip.getPrice();
    }


    @Override
    public String toString() {
        return String.format("Recomendación: %s + %s | Precio total: %.2f",
                trip, hotel, totalPrice);
    }
}