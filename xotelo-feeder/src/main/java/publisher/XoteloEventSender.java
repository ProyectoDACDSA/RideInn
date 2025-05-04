package publisher;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import com.google.gson.JsonObject;

public class XoteloEventSender {

    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String TOPIC_NAME = "Xotelo";

    public void sendEvent(String hotel, double price, String location) {
        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destinationTopic = session.createTopic(TOPIC_NAME);

            MessageProducer producer = session.createProducer(destinationTopic);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            JsonObject eventJson = new JsonObject();
            eventJson.addProperty("ts", System.currentTimeMillis());
            eventJson.addProperty("ss", "Xotelo");
            eventJson.addProperty("hotel", hotel);
            eventJson.addProperty("price", price);
            eventJson.addProperty("location", location);

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
