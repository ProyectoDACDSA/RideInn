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
            logger.info("Inicializando base de datos...");
            DatabaseConfig.initializeDatabase();

            logger.info("Procesando eventos históricos...");
            EventStoreReader eventReader = new EventStoreReader(
                    "eventstore/blablacar",
                    "eventstore/xotelo"
            );
            eventReader.processAllHistoricalEvents();

            logger.info("Iniciando consumidor de ActiveMQ...");
            ActiveMqConsumer mqConsumer = new ActiveMqConsumer();
            mqConsumer.start();

            logger.info("Iniciando API REST...");
            Javalin app = Javalin.create(config -> {
                config.plugins.enableDevLogging();
            });
            //TODO
            new ApiController(app, new AnalysisService());
            app.start(61616);

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