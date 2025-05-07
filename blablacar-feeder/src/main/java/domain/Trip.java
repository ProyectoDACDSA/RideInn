package domain;
import java.time.ZonedDateTime;

public class Trip {

    private final long id;
    private final ZonedDateTime departure;
    private final ZonedDateTime arrival;
    private final ZonedDateTime schedule;
    private final boolean available;
    private final int priceCents;
    private final String priceCurrency;
    private final int originId;
    private String originCity;
    private final int destinationId;
    private String destinationCity;
    private final ZonedDateTime updatedAt;

    public Trip(long id, ZonedDateTime departure, ZonedDateTime arrival, ZonedDateTime schedule,
                boolean available, int priceCents, String priceCurrency,
                int originId, String originCity, int destinationId, String destinationCity, ZonedDateTime updatedAt) {
        this.id = id;
        this.departure = departure;
        this.arrival = arrival;
        this.schedule = schedule;
        this.available = available;
        this.priceCents = priceCents;
        this.priceCurrency = priceCurrency;
        this.originId = originId;
        this.originCity = originCity;
        this.destinationId = destinationId;
        this.destinationCity = destinationCity;
        this.updatedAt = updatedAt;
    }

    public long getId() {return id;}
    public ZonedDateTime getDeparture() {return departure;}
    public ZonedDateTime getArrival() {return arrival;}
    public ZonedDateTime getSchedule() {return schedule;}
    public boolean isAvailable() {return available;}
    public int getPriceCents() {return priceCents;}
    public String getPriceCurrency() {return priceCurrency;}
    public int getOriginId() {return originId;}
    public String getOriginCity() {return originCity;}
    public int getDestinationId() {return destinationId;}
    public String getDestinationCity() {return destinationCity;}
    public ZonedDateTime getUpdatedAt() {return updatedAt;}

}
