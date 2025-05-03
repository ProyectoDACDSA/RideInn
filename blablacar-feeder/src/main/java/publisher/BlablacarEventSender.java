package publisher;

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import com.google.gson.JsonObject;

public class BlablacarEventSender {

    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String TOPIC_NAME = "Blablacar";

    public void sendEvent(String origin, String destination, String departureTime, double price, int available) {
        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination destinationTopic = session.createTopic(TOPIC_NAME);

            MessageProducer producer = session.createProducer(destinationTopic);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            JsonObject eventJson = new JsonObject();
            eventJson.addProperty("ts", System.currentTimeMillis());  // Timestamp en UTC
            eventJson.addProperty("ss", "Blablacar");  // Fuente
            eventJson.addProperty("origin", origin);
            eventJson.addProperty("destination", destination);
            eventJson.addProperty("departureTime", departureTime);
            eventJson.addProperty("price", price);
            eventJson.addProperty("available", available);

            TextMessage message = session.createTextMessage(eventJson.toString());

            producer.send(message);
            System.out.println("Evento enviado: " + message.getText());

            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
