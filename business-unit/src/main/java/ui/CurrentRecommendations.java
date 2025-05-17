package ui;

import model.Hotel;
import model.Recommendation;
import model.Trip;
import service.RecommendationAnalysisService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CurrentRecommendations {
    private final RecommendationAnalysisService analysisService;
    private final Scanner scanner;
    private final DateTimeFormatter dateFormatter;
    private final DateTimeFormatter dateTimeFormatter;

    public CurrentRecommendations(Scanner scanner) {
        this.analysisService = new RecommendationAnalysisService();
        this.scanner = scanner;
        this.dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    }

    private String askForCity(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private LocalDate askForDate(String prompt) {
        System.out.print(prompt);
        String dateStr = scanner.nextLine().trim();
        return LocalDate.parse(dateStr, dateFormatter);
    }

    private boolean getYesNoInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim().equalsIgnoreCase("si");
    }

    private double getPriceInput(String prompt) {
        System.out.print(prompt);
        return Double.parseDouble(scanner.nextLine());
    }

    private boolean isTripValid(Trip trip, String originCity, LocalDateTime now) {
        if (originCity != null && !trip.getOrigin().equalsIgnoreCase(originCity)) {
            return false;
        }
        LocalDateTime departure = trip.getDepartureDateTime();
        return departure.isAfter(now) ||
                (departure.toLocalDate().equals(now.toLocalDate()) &&
                        departure.toLocalTime().isAfter(now.toLocalTime()));
    }

    private boolean matchesDateFilter(LocalDateTime departure, LocalDate dateFilter) {
        return dateFilter == null || departure.toLocalDate().equals(dateFilter);
    }

    private boolean matchesPriceFilter(double totalPrice, Double minPrice, Double maxPrice) {
        if (minPrice == null || maxPrice == null) return true;
        return totalPrice >= minPrice && totalPrice <= maxPrice;
    }

    private long calculateNights(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return 1;
        }
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    private void updateRecommendationPrices(Recommendation recommendation, LocalDate departureDate, LocalDate returnDate) {
        Hotel hotel = recommendation.getHotel();
        if (departureDate != null && returnDate != null) {
            hotel.setStartDate(departureDate);
            hotel.setEndDate(returnDate);
            long nights = calculateNights(departureDate, returnDate);
            hotel.setNights(nights);
        }
        hotel.calculateTotalPrice();
        recommendation.setTotalPrice();
    }

    private List<Recommendation> filterRecommendations(List<Recommendation> recommendations,
                                                       String originCity, LocalDate departureDateFilter,
                                                       Double minPrice, Double maxPrice,
                                                       LocalDateTime now, LocalDate departureDate,
                                                       LocalDate returnDate) {
        return recommendations.stream()
                .filter(recommendation -> {
                    Trip trip = recommendation.getTrip();
                    if (!isTripValid(trip, originCity, now)) return false;
                    if (!matchesDateFilter(trip.getDepartureDateTime(), departureDateFilter)) return false;
                    updateRecommendationPrices(recommendation, departureDate, returnDate);
                    return matchesPriceFilter(recommendation.getTotalPrice(), minPrice, maxPrice);
                })
                .distinct()
                .collect(Collectors.toList());
    }

    private Map<String, Recommendation> getLatestHotelRecommendations(List<Recommendation> recommendations) {
        return recommendations.stream()
                .collect(Collectors.toMap(
                        rec -> rec.getHotel().getKey(),
                        Function.identity(),
                        (existing, replacement) -> existing.getHotel().getTimestamp()
                                .compareTo(replacement.getHotel().getTimestamp()) > 0 ?
                                existing : replacement
                ));
    }

    private void printHeader(String destinationCity, String originCity,
                             LocalDate departureDateFilter, LocalDate returnDateFilter,
                             Double minPrice, Double maxPrice, LocalDateTime now) {
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
        System.out.println("   Fecha actual: " + now.format(dateTimeFormatter));
        System.out.println("══════════════════════════════════════════");
    }

    private void printNoResults(String destinationCity, String originCity,
                                LocalDate departureDateFilter, Double minPrice) {
        System.out.println("\nNo se encontraron recomendaciones disponibles para " + destinationCity);
        if (originCity != null || departureDateFilter != null || minPrice != null) {
            System.out.println("   con los filtros especificados");
        } else {
            System.out.println("   (No hay viajes futuros a esta ciudad)");
        }
    }

    private void printHotelDetails(Hotel hotel) {
        System.out.println("\n★ " + hotel.getHotelName() + " ★");
        System.out.println("  - Valoración: " + hotel.getRating() + "/5");
        System.out.println("  - Tipo: " + hotel.getAccommodationType());
        System.out.println("  - Precio medio/noche: " + String.format("%.2f€", hotel.getAveragePricePerNight()));
        System.out.println("  - Reserva y consulta disponibilidad en: " + hotel.getUrl());
    }

    private void printTripOptions(List<Recommendation> recommendations, String hotelKey) {
        System.out.println("\n  Opciones de viaje disponibles:");
        recommendations.stream()
                .filter(rec -> rec.getHotel().getKey().equals(hotelKey))
                .forEach(rec -> {
                    Trip trip = rec.getTrip();
                    Hotel hotel = rec.getHotel();
                    long nights = hotel.getNights();
                    System.out.println("    ✈ " + trip.getOrigin() + " → " + trip.getDestination());
                    System.out.println("      Fecha salida: " + trip.getDepartureDateTime().format(dateTimeFormatter));
                    System.out.println("      Fecha regreso: " + hotel.getEndDate().format(dateFormatter));
                    System.out.println("      Noches: " + nights);
                    System.out.println("      Precio viaje: " + String.format("%.2f€", trip.getPrice()));
                    System.out.println("      Precio hotel (" + nights + " noches): " +
                            String.format("%.2f€", hotel.getTotalPrice()));
                    System.out.println("      Precio total: " +
                            String.format("%.2f€", rec.getTotalPrice()));
                });
    }

    private void displayResults(String destinationCity, String originCity,
                                LocalDate departureDateFilter, LocalDate returnDateFilter,
                                Double minPrice, Double maxPrice, LocalDateTime now,
                                Map<String, Recommendation> latestRecommendations,
                                List<Recommendation> allRecommendations) {
        printHeader(destinationCity, originCity, departureDateFilter, returnDateFilter,
                minPrice, maxPrice, now);
        if (latestRecommendations.isEmpty()) {
            printNoResults(destinationCity, originCity, departureDateFilter, minPrice);
        } else {
            System.out.println("\nSe encontraron " + latestRecommendations.size() + " hoteles únicos:");
            System.out.println("--------------------------------------------------");
            latestRecommendations.values().forEach(latestRec -> {
                printHotelDetails(latestRec.getHotel());
                printTripOptions(allRecommendations, latestRec.getHotel().getKey());
                System.out.println("--------------------------------------------------");
            });
        }
    }

    private List<Trip> findReturnTrips(String originCity, String destinationCity,
                                       LocalDateTime now, LocalDate departureDateFilter,
                                       LocalDate returnDateFilter) throws SQLException {
        Set<String> seenTripKeys = new HashSet<>();
        return analysisService.getTravelPackages(originCity)
                .stream()
                .map(Recommendation::getTrip)
                .filter(trip -> trip.getOrigin().equalsIgnoreCase(destinationCity)
                        && trip.getDestination().equalsIgnoreCase(originCity))
                .filter(trip -> isValidReturnTrip(trip, now, departureDateFilter, returnDateFilter))
                .filter(trip -> seenTripKeys.add(trip.getOrigin() + trip.getDestination() + trip.getDepartureDateTime()))
                .sorted(Comparator.comparing(Trip::getDepartureDateTime))
                .collect(Collectors.toList());
    }

    private boolean isValidReturnTrip(Trip trip, LocalDateTime now,
                                      LocalDate departureDateFilter, LocalDate returnDateFilter) {
        LocalDateTime departure = trip.getDepartureDateTime();
        boolean isAfterNow = departure.isAfter(now) ||
                (departure.toLocalDate().equals(now.toLocalDate()) &&
                        departure.toLocalTime().isAfter(now.toLocalTime()));
        if (returnDateFilter != null && departureDateFilter == null) {
            return isAfterNow && !departure.toLocalDate().isBefore(returnDateFilter);
        }
        if (returnDateFilter != null) {
            isAfterNow = isAfterNow && departure.toLocalDate().equals(returnDateFilter);
        }
        if (departureDateFilter != null) {
            isAfterNow = isAfterNow && !departure.toLocalDate().isBefore(departureDateFilter);
        }
        return isAfterNow;
    }

    private void displayReturnTrips(List<Trip> returnTrips) {
        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("     OPCIONES DE VIAJE DE REGRESO ENCONTRADAS");
        System.out.println("══════════════════════════════════════════════════");
        if (returnTrips.isEmpty()) {
            System.out.println("No se encontraron viajes de regreso.");
        } else {
            returnTrips.forEach(trip -> {
                System.out.println("✈ " + trip.getOrigin() + " → " + trip.getDestination());
                System.out.println("   Fecha: " + trip.getDepartureDateTime().format(dateTimeFormatter));
                System.out.println("   Precio: " + String.format("%.2f€", trip.getPrice()));
                System.out.println("--------------------------------------------------");
            });
        }
    }

    public void execute() throws SQLException {
        System.out.println("\n════════════════════════════════════════════");
        System.out.println("      BUSCAR RECOMENDACIONES ACTUALES      ");
        System.out.println(" (Paris, Toulouse, Niza, Lyon, Estrasburgo) ");
        System.out.println("════════════════════════════════════════════");

        String destinationCity = askForCity("\nIngrese ciudad destino: ");
        String originCity = getYesNoInput("¿Desea insertar ciudad de origen? (si/no): ") ?
                askForCity("Ingrese ciudad de origen: ") : null;

        LocalDate departureDateFilter = null;
        LocalDate returnDateFilter = null;

        if (getYesNoInput("¿Desea insertar fecha de salida? (si/no): ")) {
            departureDateFilter = askForDate("Ingrese fecha (formato dd/mm/yyyy): ");

            if (getYesNoInput("¿Desea insertar fecha de regreso? (si/no): ")) {
                returnDateFilter = askForDate("Ingrese fecha (formato dd/mm/yyyy): ");

                while (!returnDateFilter.isAfter(departureDateFilter)) {
                    System.out.println("La fecha de regreso debe ser posterior a la de salida!");
                    returnDateFilter = askForDate("Ingrese fecha de regreso (formato dd/mm/yyyy): ");
                }
            }
        }
        Double minPrice = null, maxPrice = null;
        if (getYesNoInput("¿Desea establecer intervalo de precio? (si/no): ")) {
            minPrice = getPriceInput("Ingrese precio mínimo: ");
            maxPrice = getPriceInput("Ingrese precio máximo: ");
        }

        LocalDateTime now = LocalDateTime.now();

        List<Recommendation> allRecommendations = filterRecommendations(
                analysisService.getTravelPackages(destinationCity),
                originCity, departureDateFilter, minPrice, maxPrice,
                now, departureDateFilter, returnDateFilter);

        Map<String, Recommendation> latestRecommendations = getLatestHotelRecommendations(allRecommendations);

        displayResults(destinationCity, originCity, departureDateFilter,
                returnDateFilter, minPrice, maxPrice, now,
                latestRecommendations, allRecommendations);

        if (originCity != null && getYesNoInput("\n¿Desea buscar viajes de regreso (" +
                destinationCity + " → " + originCity + ")? (si/no): ")) {
            List<Trip> returnTrips = findReturnTrips(originCity, destinationCity,
                    now, departureDateFilter, returnDateFilter);
            displayReturnTrips(returnTrips);
        }
    }
}