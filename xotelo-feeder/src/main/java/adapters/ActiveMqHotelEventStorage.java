package adapters;

import domain.HotelEvent;
import ports.HotelEventStorage;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class ActiveMqHotelEventStorage implements HotelEventStorage {
    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String TOPIC_NAME = "Xotelo";

    protected ConnectionFactory createConnectionFactory() {
        return new ActiveMQConnectionFactory(BROKER_URL);
    }

    @Override
    public void store(HotelEvent hotelEvent) {
        try {
            ConnectionFactory factory = createConnectionFactory();
            Connection connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destinationTopic = session.createTopic(TOPIC_NAME);

            MessageProducer producer = session.createProducer(destinationTopic);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            TextMessage message = session.createTextMessage(hotelEvent.toJson());
            producer.send(message);

            session.close();
            connection.close();
        } catch (JMSException e) {
            throw new RuntimeException("Failed to store hotelEvent", e);
        }
    }
}