package domain;

import java.time.ZonedDateTime;

public record Trip(long id, ZonedDateTime departure, ZonedDateTime arrival, ZonedDateTime schedule,
                   boolean available, int priceCents, String priceCurrency,
                   int originId, String originCity, int destinationId, String destinationCity, ZonedDateTime updatedAt) {
}