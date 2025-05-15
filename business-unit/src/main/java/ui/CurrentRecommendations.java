package ui;

import model.Hotel;
import model.Recommendation;
import model.Trip;
import service.RecommendationAnalysisService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CurrentRecommendations {
    private final RecommendationAnalysisService analysisService;
    private final Scanner scanner;
    private final DateTimeFormatter dateFormatter;

    public CurrentRecommendations(Scanner scanner) {
        this.analysisService = new RecommendationAnalysisService();
        this.scanner = scanner;
        this.dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }

    private static String askForCity(Scanner scanner) {
        System.out.print("Ingrese ciudad de origen: ");
        return scanner.nextLine().trim();
    }

    private static LocalDate askForDate(Scanner scanner, DateTimeFormatter formatter) {
        System.out.print("Ingrese fecha (formato dd/mm/yyyy): ");
        String dateStr = scanner.nextLine().trim();
        return LocalDate.parse(dateStr, formatter);
    }

    private boolean filterRecommendation(Recommendation recommendation, String originCity,
                                         LocalDate departureDateFilter, Double minPrice, Double maxPrice, LocalDateTime now) {

        boolean originValid = originCity == null ||
                recommendation.getTrip().getOrigin().equalsIgnoreCase(originCity);

        LocalDateTime departure = recommendation.getTrip().getDepartureDateTime();
        boolean dateValid = departure.isAfter(now) ||
                (departure.toLocalDate().equals(now.toLocalDate()) &&
                        departure.toLocalTime().isAfter(now.toLocalTime()));

        if (departureDateFilter != null) {
            dateValid = dateValid && departure.toLocalDate().equals(departureDateFilter);
        }

        if (minPrice != null && maxPrice != null) {
            dateValid = dateValid &&
                    recommendation.getTotalPrice() >= minPrice &&
                    recommendation.getTotalPrice() <= maxPrice;
        }

        return originValid && dateValid;
    }

    private void updatePrices(Recommendation recommendation, LocalDate departureDate, LocalDate returnDate) {
        Hotel hotel = recommendation.getHotel();
        if (departureDate != null && returnDate != null) {
            hotel.setStartDate(departureDate);
            hotel.setEndDate(returnDate);
            hotel.calculateTotalPrice();
            recommendation.setTotalPrice();
        } else {
            recommendation.setTotalPrice();
        }
    }

    private void displayResultsForBestFiltredTrips(String destinationCity, String originCity,
                                                   LocalDate departureDateFilter, LocalDate returnDateFilter,
                                                   Double minPrice, Double maxPrice, LocalDateTime now,
                                                   Map<String, Recommendation> latestRecommendations,
                                                   List<Recommendation> allRecommendations) {

        System.out.println("\n══════════════════════════════════════════");
        System.out.println("   RECOMENDACIONES ACTUALES PARA " + destinationCity.toUpperCase());
        if (originCity != null) {
            System.out.println("   Origen: " + originCity.toUpperCase());
        }
        if (departureDateFilter != null) {
            System.out.println("   Fecha de salida: " + departureDateFilter.format(dateFormatter));
        }
        if (returnDateFilter != null) {
            System.out.println("   Fecha de regreso: " + returnDateFilter.format(dateFormatter));
        }
        if (minPrice != null && maxPrice != null) {
            System.out.println("   Rango de precios: " + minPrice + "€ - " + maxPrice + "€");
        }
        System.out.println("   Fecha actual: " + now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        System.out.println("══════════════════════════════════════════");

        if (latestRecommendations.isEmpty()) {
            System.out.println("\nNo se encontraron recomendaciones disponibles para " + destinationCity);
            if (originCity != null || departureDateFilter != null || minPrice != null) {
                System.out.println("   con los filtros especificados");
            } else {
                System.out.println("   (No hay viajes futuros a esta ciudad)");
            }
        } else {
            System.out.println("\nSe encontraron " + latestRecommendations.size() + " hoteles únicos:");
            System.out.println("--------------------------------------------------");

            latestRecommendations.values().forEach(latestRec -> {
                Hotel hotel = latestRec.getHotel();
                System.out.println("\n★ " + hotel.getHotelName() + " ★");
                System.out.println("  - Valoración: " + hotel.getRating() + "/5");
                System.out.println("  - Tipo: " + hotel.getAccommodationType());
                System.out.println("  - Precio medio/noche: " + String.format("%.2f€", hotel.getAveragePricePerNight()));
                System.out.println("  - Reserva y consulta disponibilidad en: " + String.format(hotel.getUrl()));
                System.out.println("\n  Opciones de viaje disponibles:");
                allRecommendations.stream()
                        .filter(rec -> rec.getHotel().getKey().equals(hotel.getKey()))
                        .forEach(rec -> {
                            Trip trip = rec.getTrip();
                            System.out.println("    ✈ " + trip.getOrigin() + " → " + trip.getDestination());
                            System.out.println("      Fecha: " + trip.getDepartureDateTime()
                                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                            System.out.println("      Precio viaje: " + String.format("%.2f€", trip.getPrice()));

                            if (hotel.getStartDate() != null && hotel.getEndDate() != null) {
                                System.out.println("      Precio total de la combinación sugerida: " +
                                        String.format("%.2f€", rec.getTotalPrice()));
                            }
                        });

                System.out.println("--------------------------------------------------");
            });
        }
    }

    public void execute() throws SQLException {
        System.out.println("\n════════════════════════════════════════════");
        System.out.println("      BUSCAR RECOMENDACIONES ACTUALES      ");
        System.out.println(" (Paris, Toulouse, Niza, Lyon, Estrasburgo) ");
        System.out.println("════════════════════════════════════════════");

        System.out.print("\nIngrese ciudad destino: ");
        final String destinationCity = scanner.nextLine();

        System.out.print("¿Desea insertar ciudad de origen? (si/no): ");
        final String respuestaOrigen = scanner.nextLine().trim().toLowerCase();
        final String originCity = respuestaOrigen.equals("si") ? askForCity(scanner) : null;

        System.out.print("¿Desea insertar fecha de salida? (si/no): ");
        final String respuestaSalida = scanner.nextLine().trim().toLowerCase();
        final LocalDate finalDepartureDateFilter = respuestaSalida.equals("si") ? askForDate(scanner, dateFormatter) : null;

        System.out.print("¿Desea insertar fecha de regreso? (si/no): ");
        final String respuestaRegreso = scanner.nextLine().trim().toLowerCase();
        final LocalDate finalReturnDateFilter = respuestaRegreso.equals("si") ? askForDate(scanner, dateFormatter) : null;

        System.out.print("¿Desea establecer intervalo de precio? (si/no): ");
        final Double finalMinPrice, finalMaxPrice;
        if (scanner.nextLine().trim().equalsIgnoreCase("si")) {
            System.out.print("Ingrese precio mínimo: ");
            finalMinPrice = Double.parseDouble(scanner.nextLine());
            System.out.print("Ingrese precio máximo: ");
            finalMaxPrice = Double.parseDouble(scanner.nextLine());
        } else {
            finalMinPrice = null;
            finalMaxPrice = null;
        }

        final LocalDateTime now = LocalDateTime.now();

        List<Recommendation> allRecommendations = analysisService.getTravelPackages(destinationCity)
                .stream()
                .filter(recommendation -> filterRecommendation(recommendation, originCity, finalDepartureDateFilter,
                        finalMinPrice, finalMaxPrice, now))
                .peek(recommendation -> updatePrices(recommendation, finalDepartureDateFilter, finalReturnDateFilter))
                .collect(Collectors.toList());

        Map<String, Recommendation> latestHotelRecommendations = allRecommendations.stream()
                .collect(Collectors.toMap(
                        rec -> rec.getHotel().getKey(),
                        Function.identity(),
                        (existing, replacement) -> existing.getHotel().getTimestamp().compareTo(replacement.getHotel().getTimestamp()) > 0
                                ? existing : replacement
                ));

        displayResultsForBestFiltredTrips(destinationCity, originCity, finalDepartureDateFilter,
                finalReturnDateFilter, finalMinPrice, finalMaxPrice, now,
                latestHotelRecommendations, allRecommendations);
    }

}
