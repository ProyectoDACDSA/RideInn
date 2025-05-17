package adapters;

import static org.mockito.Mockito.*;

import domain.HotelEvent;
import javax.jms.*;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ActiveMqHotelEventStorageTest {

    @Test
    public void testStoreBooking_Success() throws JMSException {
        HotelEvent mockHotelEvent = mock(HotelEvent.class);
        when(mockHotelEvent.toJson()).thenReturn("{\"key\":\"value\"}");

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
            ActiveMqHotelEventStorage storage = new ActiveMqHotelEventStorage();
            storage.store(mockHotelEvent);

            verify(mockConnection).start();
            verify(mockSession).createTextMessage("{\"key\":\"value\"}");
            verify(mockProducer).send(mockMessage);
            verify(mockConnection).close();
            verify(mockSession).close();
        }
    }

    @Test
    public void testStoreBooking_JMSException() throws JMSException {
        try (MockedConstruction<ActiveMQConnectionFactory> ignored = mockConstruction(
                ActiveMQConnectionFactory.class,
                (mock, context) -> when(mock.createConnection()).thenThrow(new JMSException("Connection error"))
        )) {
            ActiveMqHotelEventStorage storage = new ActiveMqHotelEventStorage();
            HotelEvent mockHotelEvent = mock(HotelEvent.class);
            assertThrows(RuntimeException.class, () -> storage.store(mockHotelEvent));
        }
    }
}
