package service;

import model.Hotel;
import model.Trip;
import repository.HotelRepository;
import repository.TripRepository;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Recommendation;

public class AnalysisService {
    private final TripRepository tripRepo;
    private final HotelRepository hotelRepo;

    public AnalysisService() {
        this.tripRepo = new TripRepository();
        this.hotelRepo = new HotelRepository();
    }

    public List<Recommendation> getTravelPackages(String city) throws SQLException {
        List<Trip> trips = tripRepo.findByDestination(city);
        List<Hotel> hotels = hotelRepo.findByCity(city);
        return combineData(trips, hotels);
    }

    private List<Recommendation> combineData(List<Trip> trips, List<Hotel> hotels) {
        List<Recommendation> recommendations = new ArrayList<>();

        for (Trip trip : trips) {
            for (Hotel hotel : hotels) {
                double totalPrice = trip.getPrice() + hotel.getTotalPrice();
                recommendations.add(new Recommendation(
                        trip,
                        hotel,
                        totalPrice
                ));
            }
        }
        return recommendations;
    }
}