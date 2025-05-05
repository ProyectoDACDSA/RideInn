package ports;

import domain.Hotel;
import java.util.List;
import java.util.Map;

public interface HotelProvider {
    Map<String, String> getCityUrls();
    List<Hotel> fetchHotelsForCity(String city, String apiUrl);
}
