package ui;

import model.Recommendation;
import repository.TravelPackageRepository;
import service.AnalysisService;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class TravelAnalysisCLI {
    private final AnalysisService analysisService;
    private final Scanner scanner;
    private final DateTimeFormatter dateFormatter;

    public TravelAnalysisCLI() {
        this.analysisService = new AnalysisService();
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
                        showCityPackages();
                        break;
                    case "5":
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

    private void showCityPackages() throws SQLException {
        System.out.print("\nIngrese ciudad para ver paquetes: ");
        String city = scanner.nextLine();
        new TravelPackageRepository().printPackagesForCity(city);
    }

    private void printMenu() {
        System.out.println("\nMENU PRINCIPAL");
        System.out.println("1. Buscar recomendaciones actuales");
        System.out.println("2. Buscar recomendaciones históricas");
        System.out.println("3. Comparar evolución de precios");
        System.out.println("4. Ver paquetes combinados por ciudad");
        System.out.println("5. Salir");
        System.out.print("Seleccione una opción: ");
    }

    private void searchCurrentRecommendations() throws SQLException {
        System.out.print("\nIngrese ciudad destino: ");
        String city = scanner.nextLine();

        List<Recommendation> recommendations = analysisService.getTravelPackages(city);

        System.out.println("\nRECOMENDACIONES ACTUALES PARA " + city.toUpperCase());
        if (recommendations.isEmpty()) {
            System.out.println("No se encontraron recomendaciones");
        } else {
            recommendations.forEach(System.out::println);
        }
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
