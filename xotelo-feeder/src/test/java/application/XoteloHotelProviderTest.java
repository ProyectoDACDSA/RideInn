package application;

import adapters.XoteloApiClient;
import domain.Hotel;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class XoteloHotelProviderTest {
    private static class FakeApiClient extends XoteloApiClient {
        private String responseToReturn;

        public FakeApiClient(String response) {
            super();
            this.responseToReturn = response;
        }

        @Override
        public String fetchHotelData(String apiUrl) {
            return responseToReturn;
        }
    }

    @Test
    void getCityUrls_devuelve_las_urls_correctas() {
        XoteloHotelProvider provider = new XoteloHotelProvider(null);
        Map<String, String> urls = provider.getCityUrls();

        assertEquals(5, urls.size());
        assertTrue(urls.containsKey("Paris"));
        assertTrue(urls.get("Paris").contains("g187147"));
    }

    @Test
    void fetchHotelsForCity_con_datos_nulos_devuelve_lista_vacia() {
        XoteloHotelProvider provider = new XoteloHotelProvider(new FakeApiClient(null));
        List<Hotel> hoteles = provider.fetchHotelsForCity("Paris", "cualquier-url");

        assertTrue(hoteles.isEmpty());
    }

    @Test
    void fetchHotelsForCity_con_datos_validos_devuelve_hoteles() {
        String json = "{'result':{'list':[{'name':'Hotel Bonito','key':'h123','accommodation_type':'Hotel'," +
                "'url':'http://hotel.com','price_ranges':{'minimum':100,'maximum':200}," +
                "'review_summary':{'rating':4.5}}]}}".replace("'", "\"");

        XoteloHotelProvider provider = new XoteloHotelProvider(new FakeApiClient(json));
        List<Hotel> hoteles = provider.fetchHotelsForCity("Paris", "cualquier-url");

        assertEquals(1, hoteles.size());
        Hotel hotel = hoteles.get(0);
        assertEquals("Hotel Bonito", hotel.name());
        assertEquals(100, hotel.priceMin());
        assertEquals(4.5, hotel.rating());
    }

}