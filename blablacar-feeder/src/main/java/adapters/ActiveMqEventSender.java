package adapters;
import ports.EventSender;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import com.google.gson.JsonObject;

public class ActiveMqEventSender implements EventSender {
    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String TOPIC_NAME = "Blablacar";

    @Override
    public void sendEvent(String origin, String destination,
                          String departureTime, double price, int available) {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
            Connection connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destinationTopic = session.createTopic(TOPIC_NAME);

            MessageProducer producer = session.createProducer(destinationTopic);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            JsonObject eventJson = new JsonObject();
            eventJson.addProperty("ts", System.currentTimeMillis());
            eventJson.addProperty("ss", "Blablacar");
            eventJson.addProperty("origin", origin);
            eventJson.addProperty("destination", destination);
            eventJson.addProperty("departureTime", departureTime);
            eventJson.addProperty("price", price);
            eventJson.addProperty("available", available);

            TextMessage message = session.createTextMessage(eventJson.toString());
            producer.send(message);

            session.close();
            connection.close();
        } catch (JMSException e) {
            throw new RuntimeException("Failed to send event", e);
        }
    }
}
