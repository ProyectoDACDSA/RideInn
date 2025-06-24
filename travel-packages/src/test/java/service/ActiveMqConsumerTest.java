package service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

public class ActiveMqConsumerTest {

    @Test
    public void testParseTrip() {
        Trip trip = getTripFromSomewhere();
        String expectedTime = "15:30:00";
        LocalTime actualTime = trip.getTime();
        String actualFormatted = actualTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        assertEquals(expectedTime, actualFormatted);
    }

    private Trip getTripFromSomewhere() {
        return new Trip(LocalTime.of(15, 30));
    }

    public static class Trip {
        private final LocalTime time;

        public Trip(LocalTime time) {
            this.time = time;
        }

        public LocalTime getTime() {
            return time;
        }
    }
}

