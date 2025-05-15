package ui;

import model.Recommendation;
import service.RecommendationAnalysisService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BestValueTrips {
    private final RecommendationAnalysisService analysisService;
    private final Scanner scanner;

    public BestValueTrips(Scanner scanner) {
        this.analysisService = new RecommendationAnalysisService();
        this.scanner = scanner;
    }

    private static String askForCity(Scanner scanner) {
        System.out.print("Ingrese ciudad de origen: ");
        return scanner.nextLine().trim();
    }

    private int askForStayDuration(Scanner scanner) {
        System.out.print("Ingrese duración de estancia (noches): ");
        return Integer.parseInt(scanner.nextLine());
    }

    private double askForMinRating(Scanner scanner) {
        System.out.print("Ingrese rating mínimo (1-5): ");
        return Double.parseDouble(scanner.nextLine());
    }

    private String getValueRating(double ratio) {
        if (ratio < 100) return "★ Excelente";
        if (ratio < 150) return "▲ Bueno";
        return "▼ Regular";
    }

    private String truncate(String text, int length) {
        return text.length() > length ? text.substring(0, length-3) + "..." : text;
    }

    private void displayResultsForBestTrips(List<Recommendation> results, String originCity,
                                            int stayDuration, Double minRating, boolean specifyStay) {

        System.out.println("\n══════════════════════════════════════════════════════════════════════");
        System.out.println("                      MEJORES OFERTAS" +
                (originCity != null ? " DESDE " + originCity.toUpperCase() : "") +
                (minRating != null ? " (Rating ≥ " + minRating + ")" : ""));
        if (specifyStay) {
            System.out.println("                      Estancia: " + stayDuration + " noches");
        }
        System.out.println("══════════════════════════════════════════════════════════════════════");

        if (results.isEmpty()) {
            System.out.println("\nNo se encontraron recomendaciones con los criterios especificados");
        } else {
            System.out.printf("\n%7s | %-12s | %-20s | %-15s | %-15s | %-25s | %6s | %10s | %10s | %12s\n",
                    "Ratio", "Valoración", "Fecha y Hora Viaje", "Origen", "Destino",
                    "Hotel", "Rating", "Precio Viaje", specifyStay ? "Precio Hotel" : "Precio Noche", "TOTAL");
            System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------");

            results.forEach(rec -> {
                double ratio = rec.getTotalPrice() / (rec.getHotel().getRating() != null ? rec.getHotel().getRating() : 1.0);
                String valoracion = getValueRating(ratio);
                String hotelName = truncate(rec.getHotel().getHotelName(), 25);
                String fechaHoraViaje = rec.getTrip().getDepartureDateTime()
                        .format(DateTimeFormatter.ofPattern("dd/MM HH:mm"));

                double precioHotel = specifyStay ? rec.getHotel().getTotalPrice() : rec.getHotel().getAveragePricePerNight();
                rec.setTotalPrice();
                double precioTotal = rec.getTotalPrice();

                System.out.printf("%7.2f | %-12s | %-20s | %-15s | %-15s | %-25s | %6.1f/5 | %10.2f€ | %10.2f€ | %12.2f€\n",
                        ratio,
                        valoracion,
                        fechaHoraViaje,
                        rec.getTrip().getOrigin(),
                        rec.getTrip().getDestination(),
                        hotelName,
                        rec.getHotel().getRating() != null ? rec.getHotel().getRating() : 0.0,
                        rec.getTrip().getPrice(),
                        precioHotel,
                        precioTotal);
            });

            System.out.println("\nLeyenda:");
            System.out.println("★ Excelente - Ratio < 100 (Mejor relación calidad-precio)");
            System.out.println("▲ Bueno     - Ratio 100-150");
            System.out.println("▼ Regular   - Ratio > 150");
            if (specifyStay) {
                System.out.println("\n* Precio Total incluye viaje + " + stayDuration + " noches de hotel");
            } else {
                System.out.println("\n* Precio Noche muestra el costo aproximado por noche del hotel");
                System.out.println("\n* Precio Total incluye viaje + " + stayDuration + " noches de hotel por defecto");
            }
        }
    }

    public void execute() throws SQLException {
        System.out.println("\n══════════════════════════════════════════");
        System.out.println("  VIAJES CON MEJOR RELACIÓN CALIDAD-PRECIO ");
        System.out.println("══════════════════════════════════════════");

        System.out.print("\nIngrese ciudad destino: ");
        final String city = scanner.nextLine();

        System.out.print("\n¿Desea filtrar por ciudad de origen? (si/no): ");
        final boolean filterByOrigin = scanner.nextLine().trim().equalsIgnoreCase("si");
        final String originCity = filterByOrigin ? askForCity(scanner) : null;

        System.out.print("¿Desea especificar duración de estancia? (si/no): ");
        final boolean specifyStay = scanner.nextLine().trim().equalsIgnoreCase("si");
        final int stayDuration = specifyStay ? askForStayDuration(scanner) : 1;

        System.out.print("¿Filtrar por rating mínimo del hotel? (si/no): ");
        final boolean filterByRating = scanner.nextLine().trim().equalsIgnoreCase("si");
        final Double minRating = filterByRating ? askForMinRating(scanner) : null;

        System.out.print("Número máximo de resultados a mostrar: ");
        final int maxResults = Integer.parseInt(scanner.nextLine());

        List<Recommendation> allRecommendations = analysisService.getTravelPackages(city);

        List<Recommendation> processedRecommendations = allRecommendations.stream()
                .filter(r -> originCity == null || r.getTrip().getOrigin().equalsIgnoreCase(originCity))
                .filter(r -> !filterByRating || (r.getHotel().getRating() != null && r.getHotel().getRating() >= minRating))
                .peek(r -> {
                    LocalDate startDate = r.getTrip().getDepartureDateTime().toLocalDate();
                    r.getHotel().setStartDate(startDate);
                    r.getHotel().setEndDate(startDate.plusDays(stayDuration));
                    r.getHotel().calculateTotalPrice();
                    r.setTotalPrice();
                })
                .sorted(Comparator.comparingDouble(r -> {
                    double rating = r.getHotel().getRating() != null ? r.getHotel().getRating() : 1.0;
                    return r.getTotalPrice() / rating;
                }))
                .collect(Collectors.toList());

        Map<String, Recommendation> uniqueRecommendations = processedRecommendations.stream()
                .collect(Collectors.toMap(
                        r -> r.getHotel().getKey() + "-" + r.getTrip().getId(),
                        Function.identity(),
                        (existing, replacement) -> existing.getTotalPrice() < replacement.getTotalPrice() ? existing : replacement,
                        LinkedHashMap::new
                ));

        List<Recommendation> finalResults = uniqueRecommendations.values().stream()
                .limit(maxResults)
                .collect(Collectors.toList());

        displayResultsForBestTrips(finalResults, originCity, stayDuration, minRating, specifyStay);
    }
}