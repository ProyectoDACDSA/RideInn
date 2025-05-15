package ui;

import model.Hotel;
import model.Recommendation;
import model.Trip;
import service.RecommendationAnalysisService;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.function.Function;

public class TravelAnalysisCLI {
    private final RecommendationAnalysisService analysisService;
    private final Scanner scanner;
    private final DateTimeFormatter dateFormatter;

    public TravelAnalysisCLI() {
        this.analysisService = new RecommendationAnalysisService();
        this.scanner = new Scanner(System.in);
        this.dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }

    public void start() {
        System.out.println("=== SISTEMA DE ANÁLISIS DE VIAJES ===");

        while (true) {
            printMenu();
            String option = scanner.nextLine().trim();

            try {
                switch (option) {
                    case "1":
                        searchCurrentRecommendations();
                        break;
                    case "2":
                        findBestValueTrips();
                        break;
                    case "3":
                        System.out.println("Saliendo del sistema...");
                        return;
                    default:
                        System.out.println("Opción no válida");
                        continue;
                }
            } catch (SQLException e) {
                System.err.println("Error al acceder a la base de datos: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.err.println("Error en formato numérico: " + e.getMessage());
            }

            System.out.print("Presione Enter para continuar...");
            scanner.nextLine();
        }
    }

    private void printMenu() {
        System.out.println("\nMENU PRINCIPAL");
        System.out.println("1. Buscar recomendaciones actuales (con filtros)");
        System.out.println("2. Encontrar viajes con mejor relación calidad-precio");
        System.out.println("3. Salir");
        System.out.print("Seleccione una opción: ");
    }

    private void searchCurrentRecommendations() throws SQLException {
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
        final LocalDate finalDepartureDateFilter = respuestaSalida.equals("si") ?
                askForDate(scanner, dateFormatter) : null;

        System.out.print("¿Desea insertar fecha de regreso? (si/no): ");
        final String respuestaRegreso = scanner.nextLine().trim().toLowerCase();
        final LocalDate finalReturnDateFilter = respuestaRegreso.equals("si") ?
                askForDate(scanner, dateFormatter) : null;

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
                        (existing, replacement) ->
                                existing.getHotel().getTimestamp().compareTo(replacement.getHotel().getTimestamp()) > 0
                                        ? existing : replacement
                ));

        displayResultsForBestFiltredTrips(destinationCity, originCity, finalDepartureDateFilter,
                finalReturnDateFilter, finalMinPrice, finalMaxPrice, now,
                latestHotelRecommendations, allRecommendations);
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

    private void findBestValueTrips() throws SQLException {
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
        final int stayDuration = specifyStay ? askForStayDuration(scanner) : 3; // Default 3 días

        System.out.print("¿Filtrar por rating mínimo del hotel? (si/no): ");
        final boolean filterByRating = scanner.nextLine().trim().equalsIgnoreCase("si");
        final Double minRating = filterByRating ? askForMinRating(scanner) : null;

        System.out.print("Número máximo de resultados a mostrar: ");
        final int maxResults = Integer.parseInt(scanner.nextLine());

        RecommendationAnalysisService analysisService = new RecommendationAnalysisService();
        List<Recommendation> allRecommendations = analysisService.getTravelPackages(city);

        List<Recommendation> processedRecommendations = allRecommendations.stream()
                .filter(r -> originCity == null || r.getTrip().getOrigin().equalsIgnoreCase(originCity))
                .filter(r -> !filterByRating || (r.getHotel().getRating() != null && r.getHotel().getRating() >= minRating))
                .peek(r -> {
                    if (specifyStay) {
                        LocalDate startDate = r.getTrip().getDepartureDateTime().toLocalDate();
                        r.getHotel().setStartDate(startDate);
                        r.getHotel().setEndDate(startDate.plusDays(stayDuration));
                        r.getHotel().calculateTotalPrice();
                        r.setTotalPrice();
                    } else {
                        r.setTotalPrice();
                    }
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

    private int askForStayDuration(Scanner scanner) {
        System.out.print("Ingrese duración de estancia (noches): ");
        return Integer.parseInt(scanner.nextLine());
    }

    private double askForMinRating(Scanner scanner) {
        System.out.print("Ingrese rating mínimo (1-5): ");
        return Double.parseDouble(scanner.nextLine());
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
                System.out.println("\n* Precio Noche muestra el costo promedio por noche del hotel");
            }
        }
    }

    private String getValueRating(double ratio) {
        if (ratio < 100) return "★ Excelente";
        if (ratio < 150) return "▲ Bueno";
        return "▼ Regular";
    }

    private String truncate(String text, int length) {
        return text.length() > length ? text.substring(0, length-3) + "..." : text;
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
}