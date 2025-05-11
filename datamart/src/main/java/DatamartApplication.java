import config.DatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.ActiveMqConsumer;
import service.EventStoreReader;

public class DatamartApplication {
    private static final Logger logger = LoggerFactory.getLogger(DatamartApplication.class);

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Cerrando aplicación...");
            DatabaseConfig.closeConnection();
        }));
    }

    public static void main(String[] args) {
        try {
            logger.info("Iniciando Datamart...");
            DatabaseConfig.initializeDatabase();

            ActiveMqConsumer activeMqConsumer = new ActiveMqConsumer();
            EventStoreReader eventStoreReader = new EventStoreReader();

            eventStoreReader.processHistoricalEvents("eventstore/blablacar");
            eventStoreReader.processHistoricalEvents("eventstore/xotelo");

            activeMqConsumer.start();
            logger.info("Datamart listo para recibir eventos");

        } finally {
            DatabaseConfig.closeConnection();
        }
    }
}