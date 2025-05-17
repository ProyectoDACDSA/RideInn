package ports;

import domain.HotelEvent;

public interface HotelEventStorage {
    void store(HotelEvent hotelEvent);
}
