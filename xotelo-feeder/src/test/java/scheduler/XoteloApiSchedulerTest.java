package scheduler;

import api.XoteloApiClient;

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


}

