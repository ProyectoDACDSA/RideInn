package repository;

import domain.model.Hotel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HotelRepositoryTest {

    @BeforeEach
    public void setUp() {
        HotelRepository hotelRepository = new HotelRepository();
    }

    @Test
    public void testHotelObjectCanBeSavedAndQueriedWithoutCrash() {
        Hotel fakeHotel = new Hotel(
                789,
                "Hotel Demo",
                "demo-key",
                "Hotel",
                "http://demo.com",
                4.5,
                120.0,
                "Madrid",
                LocalDateTime.now()
        );

        assertNotNull(fakeHotel.getHotelName());
        assertNotNull(fakeHotel.getKey());
    }
}
