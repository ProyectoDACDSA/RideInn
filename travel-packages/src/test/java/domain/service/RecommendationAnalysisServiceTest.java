package domain.service;

import domain.model.*;
import domain.ports.*;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class RecommendationAnalysisServiceTest {

    static class TestableRecommendationService extends RecommendationAnalysisService {
        TripRepositoryPort tripRepo;
        HotelRepositoryPort hotelRepo;

        TestableRecommendationService(TripRepositoryPort t, HotelRepositoryPort h) {
            tripRepo = t; hotelRepo = h;
        }

        @Override
        public List<Recommendation> getTravelPackages(String city) throws SQLException {
            List<Trip> trips = tripRepo.findByDestination(city);
            List<Hotel> hotels = hotelRepo.findByCity(city);
            List<Recommendation> recs = new ArrayList<>();
            for (Trip t : trips)
                for (Hotel h : hotels)
                    if (h.getCity().equalsIgnoreCase(t.getDestination()))
                        recs.add(new Recommendation(t, h, t.getPrice() + h.getAveragePricePerNight()));
            return recs;
        }
    }

    static class FakeTripRepo implements TripRepositoryPort {
        List<Trip> trips = new ArrayList<>();
        public void save(Trip t) { trips.add(t); }
        public List<Trip> findByDestination(String c) {
            return trips.stream().filter(t -> t.getDestination().equalsIgnoreCase(c)).toList();
        }
    }

    static class FakeHotelRepo implements HotelRepositoryPort {
        List<Hotel> hotels = new ArrayList<>();
        public void save(Hotel h) { hotels.add(h); }
        public List<Hotel> findByCity(String c) {
            return hotels.stream().filter(h -> h.getCity().equalsIgnoreCase(c)).toList();
        }
    }

    FakeTripRepo tripRepo;
    FakeHotelRepo hotelRepo;
    TestableRecommendationService service;

    @BeforeEach
    void setUp() {
        tripRepo = new FakeTripRepo();
        hotelRepo = new FakeHotelRepo();
        service = new TestableRecommendationService(tripRepo, hotelRepo);
    }

    @Test
    void shouldCreateRecommendationWhenTripAndHotelMatch() throws SQLException {
        tripRepo.save(new Trip("Lyon", "Paris", "10:00:00", "2023-12-15", 120, true));
        hotelRepo.save(new Hotel(1L, "Hotel Paris", "paris-1", "Hotel", "url", 4.5, 80, "Paris", LocalDateTime.now()));
        var r = service.getTravelPackages("Paris").get(0);
        assertEquals(200.0, r.getTotalPrice(), 0.01);
    }

    @Test
    void shouldNotCreateRecommendationWhenCitiesDontMatch() throws SQLException {
        tripRepo.save(new Trip("Lyon", "Paris", "10:00:00", "2023-12-15", 120, true));
        hotelRepo.save(new Hotel(1L, "Hotel Nice", "nice-1", "Hotel", "url", 4.5, 80, "Nice", LocalDateTime.now()));
        assertTrue(service.getTravelPackages("Paris").isEmpty());
    }

    @Test
    void shouldHandleDatabaseErrors() {
        TripRepositoryPort failingRepo = new TripRepositoryPort() {
            public void save(Trip t) throws SQLException { throw new SQLException(); }
            public List<Trip> findByDestination(String c) throws SQLException { throw new SQLException(); }
        };
        var failingService = new TestableRecommendationService(failingRepo, hotelRepo);
        assertThrows(SQLException.class, () -> failingService.getTravelPackages("Paris"));
    }
}