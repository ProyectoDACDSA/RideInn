package domain.service;

import domain.ports.RecommendationInputPort;
import domain.ports.HotelRepositoryPort;
import domain.ports.TripRepositoryPort;
import domain.model.Hotel;
import domain.model.Trip;
import domain.model.Recommendation;
import java.sql.SQLException;
import java.util.*;

public class RecommendationAnalysisService implements RecommendationInputPort {
    private final TripRepositoryPort tripRepo;
    private final HotelRepositoryPort hotelRepo;

    public RecommendationAnalysisService() {
        this.tripRepo = new repository.TripRepository();
        this.hotelRepo = new repository.HotelRepository();
    }

    @Override
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