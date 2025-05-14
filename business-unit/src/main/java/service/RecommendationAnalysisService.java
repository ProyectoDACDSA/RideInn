package service;

import model.Hotel;
import model.Trip;
import model.Recommendation;
import repository.HotelRepository;
import repository.TripRepository;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public List<Hotel> getHotelsByCity(String city) throws SQLException {
        return hotelRepo.findByCity(city);
    }

    private List<Recommendation> combineData(List<Trip> trips, List<Hotel> hotels) {
        List<Recommendation> recommendations = new ArrayList<>();

        for (Trip trip : trips) {
            for (Hotel hotel : hotels) {
                double totalPrice = trip.getPrice() + hotel.getTotalPrice();
                recommendations.add(new Recommendation(trip, hotel, totalPrice));
            }
        }
        return recommendations;
    }

    public List<Recommendation> getHistoricalTrends(String city, LocalDate startDate, LocalDate endDate) throws SQLException {
        List<Trip> trips = tripRepo.findByDestinationAndDateRange(city, startDate, endDate);
        List<Hotel> hotels = hotelRepo.findByCityAndDateRange(city, startDate, endDate);
        return combineData(trips, hotels);
    }

    public Map<String, Double> getPriceEvolution(String city, int months) throws SQLException {
        Map<String, Double> priceTrends = new LinkedHashMap<>();
        LocalDate current = LocalDate.now();

        for (int i = months; i >= 0; i--) {
            LocalDate monthStart = current.minusMonths(i).withDayOfMonth(1);
            LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);

            List<Recommendation> monthlyData = getHistoricalTrends(city, monthStart, monthEnd);
            double avgPrice = monthlyData.stream()
                    .mapToDouble(Recommendation::getTotalPrice)
                    .average()
                    .orElse(0.0);

            priceTrends.put(monthStart.getMonth().toString(), avgPrice);
        }

        return priceTrends;
    }
}