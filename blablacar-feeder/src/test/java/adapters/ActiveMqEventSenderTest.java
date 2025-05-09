package adapters;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

public class ActiveMqEventSenderTest {
    @Test
    public void testSendEvent_Success() throws JMSException {
        Connection mockConnection = mock(Connection.class);
        Session mockSession = mock(Session.class);
        MessageProducer mockProducer = mock(MessageProducer.class);
        Topic mockTopic = mock(Topic.class);
        TextMessage mockMessage = mock(TextMessage.class);

        when(mockConnection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(mockSession);
        when(mockSession.createTopic("Blablacar")).thenReturn(mockTopic);
        when(mockSession.createTextMessage(anyString())).thenReturn(mockMessage);
        when(mockSession.createProducer(mockTopic)).thenReturn(mockProducer);

        try (MockedConstruction<ActiveMQConnectionFactory> ignored = mockConstruction(
                ActiveMQConnectionFactory.class,
                (mock, context) -> when(mock.createConnection()).thenReturn(mockConnection)
        )) {
            ActiveMqEventSender sender = new ActiveMqEventSender();
            sender.sendEvent("Paris", "Lyon", "2023-12-01T10:00:00Z", 25.50, 1);

            verify(mockConnection).start();
            verify(mockSession).createTextMessage(contains("\"origin\":\"Paris\""));
            verify(mockProducer).send(mockMessage);
            verify(mockConnection).close();
        }
    }

    @Test
    public void testSendEvent_JMSException() throws JMSException {
        try (MockedConstruction<ActiveMQConnectionFactory> ignored = mockConstruction(
                ActiveMQConnectionFactory.class,
                (mock, context) -> {
                    when(mock.createConnection()).thenThrow(new JMSException("Connection error"));
                }
        )) {
            ActiveMqEventSender sender = new ActiveMqEventSender();
            assertThrows(RuntimeException.class, () ->
                    sender.sendEvent("Paris", "Lyon", "2023-12-01T10:00:00Z", 25.50, 1));
        }
    }
}
