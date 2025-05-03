import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Subscriber {

    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final List<String> TOPICS = List.of("Blablacar");
    private static final String CLIENT_ID = "event-store-builder";
    private static final String BASE_DIR = "eventstore";

    public static void main(String[] args) {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
            Connection connection = factory.createConnection();
            connection.setClientID(CLIENT_ID);
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            for (String topicName : TOPICS) {
                Topic topic = session.createTopic(topicName);
                MessageConsumer consumer = session.createDurableSubscriber(topic, topicName + "-subscriber");

                consumer.setMessageListener(message -> {
                    if (message instanceof TextMessage) {
                        try {
                            String json = ((TextMessage) message).getText();

                            JsonObject event = JsonParser.parseString(json).getAsJsonObject();

                            saveEventToFile(topicName, event);

                            System.out.println("Evento recibido de '" + topicName + "': " + event);
                        } catch (JMSException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            System.out.println("Suscripciones duraderas activas. Esperando eventos...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveEventToFile(String topicName, JsonObject event) {
        try {
            LocalDateTime now = LocalDateTime.now();
            String date = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String second = now.format(DateTimeFormatter.ofPattern("ss")); // segundos actuales

            Path dir = Paths.get(BASE_DIR, topicName, second);
            Files.createDirectories(dir);

            Path filePath = dir.resolve(date + ".events");

            try (FileWriter writer = new FileWriter(filePath.toFile(), true)) {
                writer.write(event.toString());
                writer.write(System.lineSeparator());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
