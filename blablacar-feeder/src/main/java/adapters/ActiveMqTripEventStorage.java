package adapters;

import domain.TripEvent;
import ports.TripEventStorage;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class ActiveMqTripEventStorage implements TripEventStorage {
    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String TOPIC_NAME = "Blablacar";

    protected ConnectionFactory createConnectionFactory() {
        return new ActiveMQConnectionFactory(BROKER_URL);
    }

    @Override
    public void store(TripEvent tripEvent) {
        try {
            ConnectionFactory factory = createConnectionFactory();
            Connection connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destinationTopic = session.createTopic(TOPIC_NAME);

            MessageProducer producer = session.createProducer(destinationTopic);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            TextMessage message = session.createTextMessage(tripEvent.toJson());
            producer.send(message);

            session.close();
            connection.close();
        } catch (JMSException e) {
            throw new RuntimeException("Failed to store event", e);
        }
    }
}