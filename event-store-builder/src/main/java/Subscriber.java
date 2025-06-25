import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.List;

public class Subscriber {

    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final List<String> TOPICS = List.of("Blablacar", "Xotelo");
    private static final String CLIENT_ID = "event-store-builder";

    public void start() {
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

    private void saveEventToFile(String topicName, JsonObject event) {
        try {
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            String date = now.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
            String hourMinute = now.format(java.time.format.DateTimeFormatter.ofPattern("HHmm"));

            java.nio.file.Path dir = java.nio.file.Paths.get("eventstore", topicName, hourMinute);
            java.nio.file.Files.createDirectories(dir);

            java.nio.file.Path filePath = dir.resolve(date + ".events");

            try (java.io.FileWriter writer = new java.io.FileWriter(filePath.toFile(), true)) {
                writer.write(event.toString());
                writer.write(System.lineSeparator());
            }

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}