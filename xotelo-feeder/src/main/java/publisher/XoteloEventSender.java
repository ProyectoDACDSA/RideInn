package publisher;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import com.google.gson.JsonObject;

public class XoteloEventSender {

    private static final String BROKER_URL = "tcp://localhost:61616";  // URL del broker ActiveMQ
    private static final String TOPIC_NAME = "Xotelo";  // Nombre del topic para Xotelo

    public void sendEvent(String hotel, double price, String location) {
        // Crear una conexión a ActiveMQ
        try {
            // Crear la conexión con ActiveMQ
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
            Connection connection = connectionFactory.createConnection();
            connection.start();  // Iniciar la conexión

            // Crear una sesión
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Crear el topic
            Destination destinationTopic = session.createTopic(TOPIC_NAME);

            // Crear el productor
            MessageProducer producer = session.createProducer(destinationTopic);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            // Crear el evento en formato JSON
            JsonObject eventJson = new JsonObject();
            eventJson.addProperty("ts", System.currentTimeMillis());  // Timestamp en UTC
            eventJson.addProperty("ss", "Xotelo");  // Fuente
            eventJson.addProperty("hotel", hotel);
            eventJson.addProperty("price", price);
            eventJson.addProperty("location", location);

            // Convertir el evento a un mensaje de texto
            TextMessage message = session.createTextMessage(eventJson.toString());

            // Enviar el mensaje
            producer.send(message);
            System.out.println("Evento enviado: " + message.getText());

            // Cerrar la conexión
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
