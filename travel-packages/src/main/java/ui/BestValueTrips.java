package ui;

import model.Recommendation;
import service.RecommendationAnalysisService;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BestValueTrips {
    private final RecommendationAnalysisService analysisService = new RecommendationAnalysisService();
    private final Scanner scanner;

    public BestValueTrips(Scanner scanner) { this.scanner = scanner; }

    private String ask(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private String getValueRating(double ratio) {
        return ratio < 100 ? "★ Excelente" : ratio < 150 ? "▲ Bueno" : "▼ Regular";
    }

    private String truncate(String text, int length) {
        return text.length() > length ? text.substring(0, length - 3) + "..." : text;
    }

    private void displayResults(List<Recommendation> results, String originCity, int stayDuration, Double minRating, boolean specifyStay) {
        System.out.printf("\n════════════════════ MEJORES OFERTAS%s%s\n══════════════════════════════════════════════\n",
                originCity == null ? "" : " DESDE " + originCity.toUpperCase(),
                minRating == null ? "" : " (Rating ≥ " + minRating + ")");
        if (specifyStay) System.out.println("Estancia: " + stayDuration + " noches");
        if (results.isEmpty()) {
            System.out.println("\nNo se encontraron recomendaciones con los criterios especificados");
            return;
        }
        System.out.printf("\n%7s | %-12s | %-20s | %-15s | %-15s | %-25s | %6s | %10s | %10s | %12s\n",
                "Ratio", "Valoración", "Fecha y Hora Viaje", "Origen", "Destino",
                "Hotel", "Rating", "Precio Viaje", specifyStay ? "Precio Hotel" : "Precio Noche", "TOTAL");
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        results.forEach(r -> {
            double ratio = r.getTotalPrice() / (r.getHotel().getRating() != null ? r.getHotel().getRating() : 1);
            double hotelPrice = specifyStay ? r.getHotel().getAveragePricePerNight() * stayDuration : r.getHotel().getAveragePricePerNight();
            double totalPrice = r.getTrip().getPrice() + hotelPrice;
            System.out.printf("%7.2f | %-12s | %-20s | %-15s | %-15s | %-25s | %6.1f/5 | %10.2f€ | %10.2f€ | %12.2f€\n",
                    ratio, getValueRating(ratio),
                    r.getTrip().getDepartureDateTime().format(DateTimeFormatter.ofPattern("dd/MM HH:mm")),
                    r.getTrip().getOrigin(), r.getTrip().getDestination(),
                    truncate(r.getHotel().getHotelName(), 25),
                    r.getHotel().getRating() != null ? r.getHotel().getRating() : 0.0,
                    r.getTrip().getPrice(), r.getHotel().getAveragePricePerNight(), totalPrice);
        });
        System.out.println("\nLeyenda:");
        System.out.println("★ Excelente - Ratio < 100 (Mejor relación calidad-precio)");
        System.out.println("▲ Bueno     - Ratio 100-150");
        System.out.println("▼ Regular   - Ratio > 150");
        if (specifyStay) System.out.println("\n* Precio Total incluye viaje + " + stayDuration + " noches de hotel");
        else {
            System.out.println("\n* Precio Noche muestra el costo aproximado por noche del hotel");
            System.out.println("\n* Precio Total incluye viaje + " + stayDuration + " noches de hotel por defecto");
        }
    }

    public void execute() throws SQLException {
        System.out.println("\n════════════ VIAJES CON MEJOR RELACIÓN CALIDAD-PRECIO (Paris, Toulouse, Niza, Lyon, Estrasburgo) ════════════");
        String city = ask("\nIngrese ciudad destino: ");
        boolean filterByOrigin = ask("¿Desea filtrar por ciudad de origen? (si/no): ").equalsIgnoreCase("si");
        String originCity = filterByOrigin ? ask("Ingrese ciudad de origen: ") : null;
        boolean specifyStay = ask("¿Desea especificar duración de estancia? (si/no): ").equalsIgnoreCase("si");
        int stayDuration = specifyStay ? Integer.parseInt(ask("Ingrese duración de estancia (noches): ")) : 1;
        boolean filterByRating = ask("¿Filtrar por rating mínimo del hotel? (si/no): ").equalsIgnoreCase("si");
        Double minRating = filterByRating ? Double.parseDouble(ask("Ingrese rating mínimo (1-5): ")) : null;
        int maxResults = Integer.parseInt(ask("Número máximo de resultados a mostrar: "));

        List<Recommendation> allRecs = analysisService.getTravelPackages(city);
        List<Recommendation> processed = allRecs.stream()
                .filter(r -> originCity == null || r.getTrip().getOrigin().equalsIgnoreCase(originCity))
                .filter(r -> !filterByRating || (r.getHotel().getRating() != null && r.getHotel().getRating() >= minRating))
                .peek(r -> {
                    var start = r.getTrip().getDepartureDateTime().toLocalDate();
                    r.getHotel().setStartDate(start);
                    r.getHotel().setEndDate(start.plusDays(stayDuration));
                })
                .sorted(Comparator.comparingDouble(r -> {
                    double hotelPrice = specifyStay ? r.getHotel().getAveragePricePerNight() * stayDuration : r.getHotel().getAveragePricePerNight();
                    double total = r.getTrip().getPrice() + hotelPrice;
                    return total / (r.getHotel().getRating() != null ? r.getHotel().getRating() : 1.0);
                }))
                .toList();

        var unique = processed.stream().collect(Collectors.toMap(
                r -> r.getHotel().getKey() + "-" + r.getTrip().getId(),
                Function.identity(),
                (e, r) -> e.getTotalPrice() < r.getTotalPrice() ? e : r,
                LinkedHashMap::new
        ));
        displayResults(unique.values().stream().limit(maxResults).toList(), originCity, stayDuration, minRating, specifyStay);
    }
}