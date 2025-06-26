package adapters;

import domain.Trip;
import domain.TripEvent;
import org.junit.jupiter.api.Test;
import javax.jms.*;
import static org.mockito.Mockito.*;

class ActiveMqTripEventStorageTest {

    @Test
    void testStore() throws JMSException {
        ConnectionFactory factory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        Session session = mock(Session.class);
        MessageProducer producer = mock(MessageProducer.class);
        Topic topic = mock(Topic.class);
        TextMessage textMessage = mock(TextMessage.class);

        when(factory.createConnection()).thenReturn(connection);
        when(connection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(session);
        when(session.createTopic("Blablacar")).thenReturn(topic);
        when(session.createProducer(topic)).thenReturn(producer);
        when(session.createTextMessage(anyString())).thenReturn(textMessage);

        ActiveMqTripEventStorage storage = new ActiveMqTripEventStorage() {
            @Override
            protected ConnectionFactory createConnectionFactory() {
                return factory;
            }
        };

        Trip trip = new Trip(1L, null, null, null, true, 1000, "EUR", 1, "A", 2, "B", null);
        storage.store(new TripEvent(123L, "test", trip));

        verify(producer).setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        verify(producer).send(textMessage);
        verify(connection).close();
    }
}