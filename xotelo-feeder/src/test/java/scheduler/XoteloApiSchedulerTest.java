package scheduler;

import adapters.XoteloApiHotelProvider;

import java.util.*;

public class XoteloApiSchedulerTest {

    static class FakeApiClient extends XoteloApiHotelProvider {
        @Override
        public Map<String, String> getCityUrls() {
            return Map.of("Madrid", "http://fakeurl.com");
        }

        @Override
        public String fetchData(String city, String apiUrl) {
            return "{\"result\":{\"list\":[]}}";
        }
    }


}

