import ui.CurrentRecommendations;
import ui.BestValueTrips;
import java.sql.SQLException;
import java.util.Scanner;

public class CLI {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            printMainMenu();
            String input = scanner.nextLine().trim();

            try {
                switch (input) {
                    case "1":
                        new CurrentRecommendations(scanner).execute();
                        break;
                    case "2":
                        new BestValueTrips(scanner).execute();
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

    private static void printMainMenu() {
        System.out.println("\n=== MENÚ DE ANÁLISIS DE VIAJES ===");
        System.out.println("1. Recomendaciones Actuales");
        System.out.println("2. Viajes Mejor Valorados");
        System.out.println("3. Salir");
        System.out.print("Seleccione una opción: ");
    }
}