package ports;

import domain.Booking;

public interface BookingStorage {
    void store(Booking booking);
}
