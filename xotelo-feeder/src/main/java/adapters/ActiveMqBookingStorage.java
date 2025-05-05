package adapters;

import domain.Booking;
import ports.BookingStorage;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class ActiveMqBookingStorage implements BookingStorage {
    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String TOPIC_NAME = "Xotelo";

    @Override
    public void store(Booking booking) {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
            Connection connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destinationTopic = session.createTopic(TOPIC_NAME);

            MessageProducer producer = session.createProducer(destinationTopic);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            TextMessage message = session.createTextMessage(booking.toJson());
            producer.send(message);

            session.close();
            connection.close();
        } catch (JMSException e) {
            throw new RuntimeException("Failed to store booking", e);
        }
    }
}