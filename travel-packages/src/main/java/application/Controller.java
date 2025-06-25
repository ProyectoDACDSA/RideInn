package application;

import domain.ports.RecommendationInputPort;
import domain.service.RecommendationAnalysisService;
import java.sql.SQLException;
import java.util.Scanner;

public class Controller {
    private final Scanner scanner;
    private final RecommendationInputPort recommendationService;

    public Controller() {
        this.scanner = new Scanner(System.in);
        this.recommendationService = new RecommendationAnalysisService();
    }

    public void start() {
        boolean exit = false;

        while (!exit) {
            printMainMenu();
            String input = scanner.nextLine().trim();

            try {
                switch (input) {
                    case "1":
                        showCurrentRecommendations();
                        break;
                    case "2":
                        showBestValueTrips();
                        break;
                    case "3":
                        exit = true;
                        System.out.println("Saliendo del programa...");
                        break;
                    default:
                        System.out.println("Opción inválida. Intente de nuevo.");
                        break;
                }
            } catch (SQLException e) {
                System.err.println("Error en la base de datos: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Error inesperado: " + e.getMessage());
                e.printStackTrace();
            }
        }
        scanner.close();
    }

    private void showCurrentRecommendations() throws SQLException {
        new CurrentRecommendations(scanner, recommendationService).execute();
    }

    private void showBestValueTrips() throws SQLException {
        new BestValueTrips(scanner, recommendationService).execute();
    }

    private void printMainMenu() {
        System.out.println("\n=== MENÚ DE ANÁLISIS DE VIAJES ===");
        System.out.println("1. Recomendaciones Actuales");
        System.out.println("2. Viajes Mejor Valorados");
        System.out.println("3. Salir");
        System.out.print("Seleccione una opción: ");
    }

    public static void main(String[] args) {
        new Controller().start();
    }
}