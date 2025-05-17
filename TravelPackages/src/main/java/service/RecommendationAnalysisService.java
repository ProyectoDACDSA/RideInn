package service;

import model.Hotel;
import model.Trip;
import model.Recommendation;
import repository.HotelRepository;
import repository.TripRepository;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class RecommendationAnalysisService {
    private final TripRepository tripRepo;
    private final HotelRepository hotelRepo;

    public RecommendationAnalysisService() {
        this.tripRepo = new TripRepository();
        this.hotelRepo = new HotelRepository();
    }

    public List<Recommendation> getTravelPackages(String city) throws SQLException {
        List<Trip> trips = tripRepo.findByDestination(city);
        List<Hotel> hotels = hotelRepo.findByCity(city);
        return combineData(trips, hotels);
    }

    private List<Recommendation> combineData(List<Trip> trips, List<Hotel> hotels) {
        Map<String, Recommendation> uniqueMap = new LinkedHashMap<>();

        for (Trip trip : trips) {
            for (Hotel hotel : hotels) {
                if (Objects.equals(hotel.getCity(), trip.getDestination())) {
                    String key = trip.getId() + "-" + hotel.getKey();
                    if (!uniqueMap.containsKey(key)) {
                        double totalPrice = trip.getPrice() + hotel.getTotalPrice();
                        uniqueMap.put(key, new Recommendation(trip, hotel, totalPrice));
                    }
                }
            }
        }
        return new ArrayList<>(uniqueMap.values());
    }

}