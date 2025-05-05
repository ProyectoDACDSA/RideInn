package publisher;

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import com.google.gson.JsonObject;

public class XoteloEventSender {
    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String TOPIC_NAME = "Xotelo";

    public void sendBookingEvent(Booking booking) {
        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destinationTopic = session.createTopic(TOPIC_NAME);

            MessageProducer producer = session.createProducer(destinationTopic);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            JsonObject eventJson = createEventJsonFromBooking(booking);

            TextMessage message = session.createTextMessage(eventJson.toString());
            producer.send(message);
            System.out.println("Booking event sent: " + message.getText());

            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private JsonObject createEventJsonFromBooking(Booking booking) {
        JsonObject eventJson = new JsonObject();
        eventJson.addProperty("ts", booking.getTs());
        eventJson.addProperty("ss", booking.getSs());
        eventJson.addProperty("hotel", booking.getHotelName());
        eventJson.addProperty("key", booking.getKey());
        eventJson.addProperty("accommodationType", booking.getAccommodationType());
        eventJson.addProperty("url", booking.getUrl());
        eventJson.addProperty("rating", booking.getRating());
        eventJson.addProperty("averagePricePerNight", booking.getAveragePricePerNight());
        eventJson.addProperty("startDate", booking.getStartDate().toString());
        eventJson.addProperty("endDate", booking.getEndDate().toString());
        eventJson.addProperty("totalPrice", booking.getTotalPrice());
        eventJson.addProperty("location", booking.getCity());

        return eventJson;
    }
}