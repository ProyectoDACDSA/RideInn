package adapters;

import ports.*;
import domain.*;
import static org.mockito.Mockito.*;
import java.util.*;
import org.junit.jupiter.api.Test;

public class ControllerTest {
    @Test
    public void testExecute() {
        HotelProvider mockHotelProvider = mock(HotelProvider.class);
        HotelEventStorage mockHotelEventStorage = mock(HotelEventStorage.class);

        when(mockHotelProvider.getCityUrls())
                .thenReturn(Map.of("Paris", "http://fake-api.com"));

        when(mockHotelProvider.fetchHotelsForCity(anyString(), anyString()))
                .thenReturn(List.of(
                        new Hotel("Hotel A", "key1", 100, 200, 4.5,
                                "Hotel", "http://a.com", "Paris")
                ));

        Controller controller = new Controller(mockHotelProvider, mockHotelEventStorage);
        controller.execute();

        verify(mockHotelEventStorage, atLeastOnce()).store(any(HotelEvent.class));
    }
}
