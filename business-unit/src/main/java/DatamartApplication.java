import config.DatabaseConfig;
import controller.ApiController;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.ActiveMqConsumer;
import service.AnalysisService;
import service.EventStoreReader;

public class DatamartApplication {
    private static final Logger logger = LoggerFactory.getLogger(DatamartApplication.class);

    public static void main(String[] args) {
        try {
            // 1. Inicialización de la base de datos
            logger.info("Inicializando base de datos...");
            DatabaseConfig.initializeDatabase();

            // 2. Procesamiento inicial de eventos históricos
            logger.info("Procesando eventos históricos...");
            EventStoreReader eventReader = new EventStoreReader(
                    "eventstore/blablacar",
                    "eventstore/xotelo"
            );
            eventReader.processAllHistoricalEvents();

            // 3. Iniciar consumidor de ActiveMQ (eventos en tiempo real)
            logger.info("Iniciando consumidor de ActiveMQ...");
            ActiveMqConsumer mqConsumer = new ActiveMqConsumer();
            mqConsumer.start();

            // 4. Iniciar interfaz de usuario (API REST)
            logger.info("Iniciando API REST...");
            Javalin app = Javalin.create(config -> {
                config.plugins.enableDevLogging();
            });
            new ApiController(app, new AnalysisService());
            app.start(7070);

            logger.info("Sistema completamente operativo");

            // Mantener el hilo principal activo
            Thread.currentThread().join();

        } catch (InterruptedException e) {
            logger.info("Aplicación interrumpida");
        } catch (Exception e) {
            logger.error("Error crítico en la aplicación", e);
        } finally {
            DatabaseConfig.closeConnection();
        }
    }
}