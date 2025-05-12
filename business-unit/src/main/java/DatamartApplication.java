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
    private static final int JAVALIN_PORT = 7070;

    public static void main(String[] args) {
        try {
            logger.info("Inicializando base de datos...");
            DatabaseConfig.initializeDatabase();

            logger.info("Procesando eventos históricos...");
            EventStoreReader eventReader = new EventStoreReader(
                    "eventstore/Blablacar",
                    "eventstore/Xotelo"
            );
            eventReader.processAllHistoricalEvents();

            logger.info("Iniciando consumidor de ActiveMQ...");
            ActiveMqConsumer mqConsumer = new ActiveMqConsumer();
            mqConsumer.start();

            logger.info("Iniciando API REST...");
            Javalin app = Javalin.create(config -> {
                config.plugins.enableDevLogging();
            });
            new ApiController(app, new AnalysisService());
            app.start(JAVALIN_PORT);

            logger.info("Sistema completamente operativo");

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