package ui;

import model.Hotel;
import model.Recommendation;
import model.Trip;
import service.RecommendationAnalysisService;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CheapestTrips {
    private final RecommendationAnalysisService analysisService;
    private final Scanner scanner;

    public CheapestTrips(Scanner scanner) {
        this.analysisService = new RecommendationAnalysisService();
        this.scanner = scanner;
    }

    private String truncate(String text, int length) {
        return text.length() > length ? text.substring(0, length-3) + "..." : text;
    }

    private void displayCheapestTrips(List<Recommendation> results, String originCity, int stayDuration) {
        System.out.println("\n══════════════════════════════════════════════════════");
        System.out.print("                  VIAJES MÁS BARATOS");
        if (originCity != null) {
            System.out.print(" DESDE " + originCity.toUpperCase());
        }
        System.out.println();
        System.out.println("                  Estancia: " + stayDuration + " noche" + (stayDuration > 1 ? "s" : ""));
        System.out.println("══════════════════════════════════════════════════════");

        if (results.isEmpty()) {
            System.out.println("\nNo se encontraron recomendaciones con los criterios especificados.");
        } else {
            System.out.printf("\n%-17s | %-14s | %-14s | %-22s | %3s | %14s | %14s | %12s\n",
                    "Fecha", "Origen", "Destino", "Hotel", "⭐", "Precio viaje", "Precio hotel", "Total");
            System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------");

            for (Recommendation rec : results) {
                Trip trip = rec.getTrip();
                Hotel hotel = rec.getHotel();
                String fecha = trip.getDepartureDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                double hotelTotal = hotel.getTotalPrice();

                System.out.printf(
                        "%-17s | %-14s | %-14s | %-22s | %4.1f | %13.1f€ | %13.1f€ | %12.1f€\n",
                        fecha,
                        trip.getOrigin(),
                        trip.getDestination(),
                        truncate(hotel.getHotelName(), 22),
                        hotel.getRating() != null ? hotel.getRating() : 0.0,
                        trip.getPrice(),
                        hotelTotal,
                        rec.getTotalPrice());
            }

            System.out.println("\n--------------------------------------------------------------------------------------------------------------------------------------------");
            System.out.println("* Precio Total incluye viaje + " + stayDuration + " noche" + (stayDuration > 1 ? "s" : "") + " de hotel");
        }
    }

    private static String askForCity(Scanner scanner) {
        System.out.print("Ingrese ciudad de origen: ");
        return scanner.nextLine().trim();
    }

    private int askForStayDuration(Scanner scanner) {
        System.out.print("Ingrese duración de estancia (noches): ");
        return Integer.parseInt(scanner.nextLine());
    }

    public void execute() throws SQLException {
        System.out.println("\n══════════════════════════════════════════════");
        System.out.println("             VIAJES MÁS BARATOS");
        System.out.println(" (Paris, Toulouse, Niza, Lyon, Estrasburgo) ");
        System.out.println("══════════════════════════════════════════════");

        System.out.print("\nIngrese ciudad destino: ");
        final String city = scanner.nextLine();

        System.out.print("\n¿Desea filtrar por ciudad de origen? (si/no): ");
        final boolean filterByOrigin = scanner.nextLine().trim().equalsIgnoreCase("si");
        final String originCity = filterByOrigin ? askForCity(scanner) : null;

        System.out.print("\n¿Desea especificar duración de estancia? (si/no): ");
        final boolean specifyStay = scanner.nextLine().trim().equalsIgnoreCase("si");
        final int stayDuration = specifyStay ? askForStayDuration(scanner) : 1;

        List<Recommendation> allRecommendations = analysisService.getTravelPackages(city);

        List<Recommendation> filteredRecommendations = allRecommendations.stream()
                .filter(r -> originCity == null || r.getTrip().getOrigin().equalsIgnoreCase(originCity))
                .peek(r -> {
                    r.getHotel().setStartDate(r.getTrip().getDepartureDateTime().toLocalDate());
                    r.getHotel().setEndDate(r.getHotel().getStartDate().plusDays(stayDuration));
                    r.getHotel().calculateTotalPrice();
                    r.setTotalPrice();
                })
                .sorted(Comparator.comparingDouble(Recommendation::getTotalPrice))
                .limit(10)
                .collect(Collectors.toList());

        displayCheapestTrips(filteredRecommendations, originCity, stayDuration);
    }
}
