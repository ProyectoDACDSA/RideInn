package adapters;

import static org.mockito.Mockito.*;
import domain.Booking;
import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ActiveMqBookingStorageTest {
    @Test
    public void testStoreBooking_Success() throws JMSException {
        Booking mockBooking = mock(Booking.class);
        when(mockBooking.toJson()).thenReturn("{\"key\":\"value\"}");

        Connection mockConnection = mock(Connection.class);
        Session mockSession = mock(Session.class);
        MessageProducer mockProducer = mock(MessageProducer.class);
        Topic mockTopic = mock(Topic.class);
        TextMessage mockMessage = mock(TextMessage.class);

        when(mockConnection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(mockSession);
        when(mockSession.createTopic("Xotelo")).thenReturn(mockTopic);
        when(mockSession.createTextMessage("{\"key\":\"value\"}")).thenReturn(mockMessage);
        when(mockSession.createProducer(mockTopic)).thenReturn(mockProducer);

        try (MockedConstruction<ActiveMQConnectionFactory> ignored = mockConstruction(
                ActiveMQConnectionFactory.class,
                (mock, context) -> when(mock.createConnection()).thenReturn(mockConnection)
        )) {
            ActiveMqBookingStorage storage = new ActiveMqBookingStorage();
            storage.store(mockBooking);

            verify(mockConnection).start();
            verify(mockSession).createTextMessage(anyString());
            verify(mockProducer).send(mockMessage);
            verify(mockConnection).close();
        }
    }

    @Test
    public void testStoreBooking_JMSException() throws JMSException {
        try (MockedConstruction<ActiveMQConnectionFactory> ignored = mockConstruction(
                ActiveMQConnectionFactory.class,
                (mock, context) -> {
                    when(mock.createConnection()).thenThrow(new JMSException("Error de conexión"));
                }
        )) {
            ActiveMqBookingStorage storage = new ActiveMqBookingStorage();
            assertThrows(RuntimeException.class, () -> storage.store(mock(Booking.class)));
        }
    }
}