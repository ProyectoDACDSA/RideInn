import ui.BestValueTrips;
import ui.CurrentRecommendations;
import java.sql.SQLException;
import java.util.Scanner;

public class CLI {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            boolean exit = false;
            while (!exit) {
                System.out.println("\n=== MENÚ DE ANÁLISIS DE VIAJES ===");
                System.out.println("1. Recomendaciones Actuales");
                System.out.println("2. Viajes Mejor Valorados");
                System.out.println("3. Salir");
                System.out.print("Seleccione una opción: ");
                String input = scanner.nextLine();
                switch (input) {
                    case "1":
                        try {
                            CurrentRecommendations currentRecommendations = new CurrentRecommendations(scanner);
                            currentRecommendations.execute();
                        } catch (SQLException e) {
                            System.err.println("Error en la base de datos: " + e.getMessage());
                        } catch (Exception e) {
                            System.err.println("Error inesperado: " + e.getMessage());
                            e.printStackTrace();
                        }
                        break;
                    case "2":
                        try {
                            BestValueTrips bestValueTrips = new BestValueTrips(scanner);
                            bestValueTrips.execute();
                        } catch (SQLException e) {
                            System.err.println("Error en la base de datos: " + e.getMessage());
                        } catch (Exception e) {
                            System.err.println("Error inesperado: " + e.getMessage());
                            e.printStackTrace();
                        }
                        break;
                    case "3":
                        exit = true;
                        System.out.println("Saliendo del programa...");
                        break;
                    default:
                        System.out.println("Opción inválida. Intente de nuevo.");
                        break;
                }
            }
        }
    }
}
