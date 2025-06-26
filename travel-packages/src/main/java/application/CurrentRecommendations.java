package application;

import domain.ports.RecommendationInputPort;
import domain.model.Recommendation;
import domain.model.Trip;
import domain.model.Hotel;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class CurrentRecommendations {
    private final RecommendationInputPort recommendationService;
    private final Scanner scanner;
    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public CurrentRecommendations(Scanner scanner, RecommendationInputPort recommendationService) {
        this.scanner = scanner;
        this.recommendationService = recommendationService;
    }

    private String ask(String p) { System.out.print(p); return scanner.nextLine().trim(); }
    private boolean yes(String p) { return ask(p).equalsIgnoreCase("si"); }
    private LocalDate askDate(String p) { return LocalDate.parse(ask(p), df); }
    private double askDouble(String p) { return Double.parseDouble(ask(p)); }

    private boolean validTrip(Trip t, String orig, LocalDateTime now) {
        if (orig != null && !t.getOrigin().equalsIgnoreCase(orig)) return false;
        LocalDateTime d = t.getDepartureDateTime();
        return d.isAfter(now) || (d.toLocalDate().equals(now.toLocalDate()) && d.toLocalTime().isAfter(now.toLocalTime()));
    }

    private boolean priceInRange(double p, Double min, Double max) {
        return (min == null || max == null) || (p >= min && p <= max);
    }

    private void updatePrices(Recommendation r, LocalDate start, LocalDate end) {
        Hotel h = r.getHotel();
        if (start != null && end != null) {
            h.setStartDate(start);
            h.setEndDate(end);
            h.setNights(java.time.temporal.ChronoUnit.DAYS.between(start, end));
        } else {
            h.setNights(1);
        }
        h.calculateTotalPrice();
        r.setTotalPrice();
    }

    private List<Recommendation> filterRecs(List<Recommendation> recs, String orig, LocalDate depF,
                                            Double minP, Double maxP, LocalDateTime now, LocalDate start, LocalDate end) {
        return recs.stream()
                .filter(r -> validTrip(r.getTrip(), orig, now))
                .filter(r -> depF == null || r.getTrip().getDepartureDateTime().toLocalDate().equals(depF))
                .peek(r -> updatePrices(r, start, end))
                .filter(r -> priceInRange(r.getTotalPrice(), minP, maxP))
                .distinct()
                .collect(Collectors.toList());
    }

    private Map<String, Recommendation> latestRecs(List<Recommendation> recs) {
        return recs.stream()
                .collect(Collectors.toMap(r -> r.getHotel().getKey(), r -> r,
                        (a, b) -> a.getHotel().getTimestamp().toLocalDateTime()
                                .compareTo(b.getHotel().getTimestamp().toLocalDateTime()) > 0 ? a : b));
    }

    private void printHeader(String dest, String orig, LocalDate dep, LocalDate ret, Double min, Double max,
                             LocalDateTime now) {
        System.out.println("\n════════════════════════════════════");
        System.out.println("   RECOMENDACIONES PARA " + dest.toUpperCase());
        if (orig != null) System.out.println("   Origen: " + orig.toUpperCase());
        if (dep != null) System.out.println("   Salida: " + dep.format(df));
        if (ret != null) System.out.println("   Regreso: " + ret.format(df));
        if (min != null && max != null) System.out.println("   Precio: " + min + "€ - " + max + "€");
        System.out.println("   Fecha actual: " + now.format(dtf));
        System.out.println("════════════════════════════════════");
    }

    private void printHotel(Hotel h) {
        System.out.printf("\n★ %s ★\n - Valoración: %s/5\n - Tipo: %s\n - Precio/noche: %.2f€\n - Reserva: %s\n",
                h.getHotelName(),
                h.getRating() != null ? h.getRating() : "N/A",
                h.getAccommodationType(),
                h.getAveragePricePerNight(),
                h.getUrl());
    }

    private void printTrips(List<Recommendation> recs, String key) {
        System.out.println("\n Opciones de viaje:");
        recs.stream().filter(r -> r.getHotel().getKey().equals(key)).forEach(r -> {
            Trip t = r.getTrip();
            Hotel h = r.getHotel();
            long n = h.getNights();
            System.out.printf("  ✈ %s → %s\n    Salida: %s\n    Noches: %d\n    Precio viaje: %.2f€\n    Precio hotel: %.2f€\n    Total: %.2f€\n",
                    t.getOrigin(),
                    t.getDestination(),
                    t.getDepartureDateTime().format(dtf),
                    n,
                    t.getPrice(),
                    h.getTotalPrice(),
                    r.getTotalPrice());
        });
    }

    private void displayResults(String dest, String orig, LocalDate dep, LocalDate ret, Double min, Double max,
                                LocalDateTime now, Map<String, Recommendation> latest, List<Recommendation> all) {
        printHeader(dest, orig, dep, ret, min, max, now);
        if (latest.isEmpty()) {
            System.out.println("\nNo se encontraron recomendaciones para " + dest +
                    ((orig != null || dep != null || min != null) ? " con esos filtros" : " (sin viajes futuros)"));
        } else {
            System.out.println("\n" + latest.size() + " hoteles únicos encontrados:");
            System.out.println("---------------------------------------");
            latest.values().forEach(r -> {
                printHotel(r.getHotel());
                printTrips(all, r.getHotel().getKey());
                System.out.println("---------------------------------------");
            });
        }
    }

    public void execute() throws SQLException {
        System.out.println("\n════════════════════════════════════");
        System.out.println("    BUSCAR RECOMENDACIONES ACTUALES");
        System.out.println(" (Paris, Toulouse, Niza, Lyon, Estrasburgo)");
        System.out.println("════════════════════════════════════");

        String dest = ask("\nCiudad destino: ");
        String orig = yes("¿Insertar ciudad de origen? (si/no): ") ? ask("Ciudad origen: ") : null;

        LocalDate depF = null;
        Long numNoches = 1L;
        if (yes("¿Insertar fecha salida? (si/no): ")) {
            depF = askDate("Fecha salida (dd/mm/yyyy): ");
            numNoches = (long) Integer.parseInt(ask("Número de noches de estancia: "));
        }

        Double minP = null, maxP = null;
        if (yes("¿Establecer rango precio? (si/no): ")) {
            minP = askDouble("Precio mínimo: ");
            maxP = askDouble("Precio máximo: ");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDate endDate = (depF != null) ? depF.plusDays(numNoches) : null;

        List<Recommendation> allRecs = filterRecs(recommendationService.getTravelPackages(dest), orig, depF, minP,
                maxP, now, depF, endDate);
        Map<String, Recommendation> latest = latestRecs(allRecs);
        displayResults(dest, orig, depF, endDate, minP, maxP, now, latest, allRecs);
    }
}