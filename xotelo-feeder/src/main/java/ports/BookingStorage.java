package ports;

import domain.HotelEvent;

public interface BookingStorage {
    void store(HotelEvent booking);
}
