package scheduler;

import api.XoteloApiClient;
import database.HotelRepository;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class XoteloApiSchedulerTest {

    static class FakeApiClient extends XoteloApiClient {
        @Override
        public Map<String, String> getCityUrls() {
            return Map.of("Madrid", "http://fakeurl.com");
        }

        @Override
        public String fetchData(String city, String apiUrl) {
            return "{\"result\":{\"list\":[]}}";
        }
    }

    static class FakeHotelRepository extends HotelRepository {
        public String lastCity;
        public String lastJsonData;

        public FakeHotelRepository() {
            super("jdbc:sqlite::memory:"); // In-memory DB, no real file
        }

        @Override
        public void saveHotels(String jsonData, String city) {
            this.lastCity = city;
            this.lastJsonData = jsonData;
        }
    }

    @Test
    public void testRunOnceSavesHotels() {
        FakeApiClient fakeApiClient = new FakeApiClient();
        FakeHotelRepository fakeRepo = new FakeHotelRepository();

        XoteloApiScheduler scheduler = new XoteloApiScheduler(fakeApiClient, fakeRepo);
        scheduler.runOnce();

        assertEquals("Madrid", fakeRepo.lastCity);
        assertEquals("{\"result\":{\"list\":[]}}", fakeRepo.lastJsonData);
    }
}

