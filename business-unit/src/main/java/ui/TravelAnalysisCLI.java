package ui;

import model.Recommendation;
import service.RecommendationAnalysisService;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDateTime;

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
                        searchHistoricalRecommendations();
                        break;
                    case "3":
                        comparePriceTrends();
                        break;
                    case "4":
                        System.out.println("Saliendo del sistema...");
                        return;
                    default:
                        System.out.println("Opción no válida");
                }
            } catch (SQLException e) {
                System.err.println("Error al acceder a la base de datos: " + e.getMessage());
            }

            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
        }
    }

    private void printMenu() {
        System.out.println("\nMENU PRINCIPAL");
        System.out.println("1. Buscar recomendaciones actuales");
        System.out.println("2. Buscar recomendaciones históricas");
        System.out.println("3. Comparar evolución de precios");
        System.out.println("4. Salir");
        System.out.print("Seleccione una opción: ");
    }

    private void searchCurrentRecommendations() throws SQLException {
        System.out.println("\n══════════════════════════════════════════");
        System.out.println("      BUSCAR RECOMENDACIONES ACTUALES      ");
        System.out.println("══════════════════════════════════════════");

        System.out.print("\nIngrese ciudad destino: ");
        String destinationCity = scanner.nextLine();

        // Nueva pregunta: Ciudad de origen
        final String originCity;
        System.out.print("¿Desea insertar ciudad de origen? (si/no): ");
        String respuestaOrigen = scanner.nextLine().trim().toLowerCase();
        if (respuestaOrigen.equals("si")) {
            System.out.print("Ingrese ciudad de origen: ");
            originCity = scanner.nextLine();
        } else {
            originCity = null;
        }

        // Preguntar por fecha de salida
        final LocalDate finalDepartureDateFilter;
        System.out.print("¿Desea insertar fecha de salida? (si/no): ");
        String respuestaFecha = scanner.nextLine().trim().toLowerCase();
        if (respuestaFecha.equals("si")) {
            System.out.print("Ingrese fecha de salida (dd/MM/yyyy): ");
            String fechaStr = scanner.nextLine();
            finalDepartureDateFilter = LocalDate.parse(fechaStr, dateFormatter);
        } else {
            finalDepartureDateFilter = null;
        }

        final Double finalMinPrice;
        final Double finalMaxPrice;
        System.out.print("¿Desea establecer intervalo de precio? (si/no): ");
        String respuestaPrecio = scanner.nextLine().trim().toLowerCase();
        if (respuestaPrecio.equals("si")) {
            System.out.print("Ingrese precio mínimo: ");
            finalMinPrice = Double.parseDouble(scanner.nextLine());
            System.out.print("Ingrese precio máximo: ");
            finalMaxPrice = Double.parseDouble(scanner.nextLine());
        } else {
            finalMinPrice = null;
            finalMaxPrice = null;
        }

        final LocalDateTime now = LocalDateTime.now();

        List<Recommendation> recommendations = analysisService.getTravelPackages(destinationCity)
                .stream()
                .filter(recommendation -> {
                    boolean originValid = (originCity == null) ||
                            recommendation.getTrip().getOrigin().equalsIgnoreCase(originCity);

                    LocalDateTime departure = recommendation.getTrip().getDepartureDateTime();
                    boolean dateValid = departure.isAfter(now) ||
                            (departure.toLocalDate().equals(now.toLocalDate()) &&
                                    departure.toLocalTime().isAfter(now.toLocalTime()));

                    if (finalDepartureDateFilter != null) {
                        dateValid = dateValid && departure.toLocalDate().equals(finalDepartureDateFilter);
                    }

                    if (finalMinPrice != null && finalMaxPrice != null) {
                        dateValid = dateValid &&
                                recommendation.getTotalPrice() >= finalMinPrice &&
                                recommendation.getTotalPrice() <= finalMaxPrice;
                    }

                    return originValid && dateValid;
                })
                .toList();

        System.out.println("\n══════════════════════════════════════════");
        System.out.println("   RECOMENDACIONES ACTUALES PARA " + destinationCity.toUpperCase());
        if (originCity != null) {
            System.out.println("   Origen: " + originCity.toUpperCase());
        }
        if (finalDepartureDateFilter != null) {
            System.out.println("   Fecha de salida: " + finalDepartureDateFilter.format(dateFormatter));
        }
        if (finalMinPrice != null && finalMaxPrice != null) {
            System.out.println("   Rango de precios: " + finalMinPrice + "€ - " + finalMaxPrice + "€");
        }
        System.out.println("   Fecha actual: " + now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        System.out.println("══════════════════════════════════════════");

        if (recommendations.isEmpty()) {
            System.out.println("\nNo se encontraron recomendaciones disponibles para " + destinationCity);
            if (originCity != null || finalDepartureDateFilter != null || finalMinPrice != null) {
                System.out.println("   con los filtros especificados");
            } else {
                System.out.println("   (No hay viajes futuros a esta ciudad)");
            }
        } else {
            System.out.println("\nSe encontraron " + recommendations.size() + " recomendaciones:");
            System.out.println("--------------------------------------------------");

            recommendations.forEach(recommendation -> {
                System.out.println("Viaje ID: " + recommendation.getTrip().getId());
                System.out.println("Origen: " + recommendation.getTrip().getOrigin());
                System.out.println("Fecha salida: " + recommendation.getTrip().getDepartureDateTime()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                System.out.println("CheckIn: " + recommendation.getTrip().getDepartureDate());
                System.out.println("CheckOut: " + recommendation.getTrip().getDepartureDate().plusDays(3));
                System.out.println("Hotel: " + recommendation.getHotel().getHotelName() +
                        " (" + "Valoración: " + recommendation.getHotel().getRating() + "/5)");
                System.out.println("Precio total: " + String.format("%.2f", recommendation.getTotalPrice()) + "€");
                System.out.println("Para reservar el hotel consulte en: " + recommendation.getHotel().getUrl());
                System.out.println("--------------------------------------------------");
            });

            System.out.println("\nMostrando " + recommendations.size() + " resultados");
        }

        System.out.println("\n══════════════════════════════════════════");
    }

    private void searchHistoricalRecommendations() throws SQLException {
        System.out.print("\nIngrese ciudad destino: ");
        String city = scanner.nextLine();

        System.out.print("Fecha inicio (dd/MM/yyyy): ");
        LocalDate startDate = LocalDate.parse(scanner.nextLine(), dateFormatter);

        System.out.print("Fecha fin (dd/MM/yyyy): ");
        LocalDate endDate = LocalDate.parse(scanner.nextLine(), dateFormatter);

        List<Recommendation> recommendations = analysisService.getHistoricalTrends(city, startDate, endDate);

        System.out.printf("\nRECOMENDACIONES HISTÓRICAS PARA %s (%s a %s)%n",
                city.toUpperCase(), startDate.format(dateFormatter), endDate.format(dateFormatter));

        if (recommendations.isEmpty()) {
            System.out.println("No se encontraron recomendaciones en este período");
        } else {
            recommendations.forEach(System.out::println);
        }
    }

    private void comparePriceTrends() throws SQLException {
        System.out.print("\nIngrese ciudad destino: ");
        String city = scanner.nextLine();

        System.out.print("Número de meses a analizar: ");
        int months = Integer.parseInt(scanner.nextLine());

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(months);

        System.out.printf("\nEVOLUCIÓN DE PRECIOS EN %s (%s a %s)%n",
                city.toUpperCase(), startDate.format(dateFormatter), endDate.format(dateFormatter));

        analysisService.getPriceEvolution(city, months).forEach((month, avgPrice) -> {
            System.out.printf("%s: %.2f €%n", month, avgPrice);
        });
    }
}