package infrastructure.adapters;

import com.google.gson.*;
import domain.model.Hotel;
import domain.model.Trip;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.HotelRepository;
import repository.TripRepository;
import javax.jms.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ActiveMqConsumer {
    private static final Logger log = LoggerFactory.getLogger(ActiveMqConsumer.class);
    private static final String URL = "tcp://localhost:61616", TOPIC_BLAB = "Blablacar", TOPIC_XOT = "Xotelo";
    private final ExecutorService exec = Executors.newSingleThreadExecutor();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>)
                    (j, t, c) -> LocalDate.parse(j.getAsString()))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>)
                    (j, t, c) -> LocalDateTime.parse(j.getAsString())).create();

    public void start() {
        exec.submit(() -> {
            try {
                var conn = new ActiveMQConnectionFactory(URL).createConnection();
                conn.start();
                var sess = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
                setupConsumer(sess, TOPIC_BLAB, new TripListener());
                setupConsumer(sess, TOPIC_XOT, new HotelListener());
                log.info("ActiveMQ consumer started");
            } catch (JMSException e) {
                log.error("Failed to start ActiveMQ consumer", e);
            }
        });
    }

    private void setupConsumer(Session sess, String topic, MessageListener listener) throws JMSException {
        var consumer = sess.createConsumer(sess.createTopic(topic));
        consumer.setMessageListener(listener);
        log.info("Subscribed to topic: {}", topic);
    }

    private Trip parseTrip(String json) {
        var o = JsonParser.parseString(json).getAsJsonObject();
        var dt = o.get("departureTime").getAsString();
        JsonElement availableElem = o.get("available");
        if (availableElem == null) {
            availableElem = o.get("avalable");
        }
        boolean available = availableElem != null && availableElem.getAsBoolean();
        return new Trip(o.get("origin").getAsString(), o.get("destination").getAsString(),
                dt.substring(11,19), dt.substring(0,10),
                o.get("price").getAsDouble(), available);
    }

    private Hotel parseHotel(String json) {
        if (json == null || json.isBlank()) throw new IllegalArgumentException("Empty JSON");
        try { return gson.fromJson(json, Hotel.class); }
        catch (JsonSyntaxException | DateTimeParseException e) { throw new IllegalArgumentException("Invalid Hotel JSON", e); }
    }

    private class TripListener implements MessageListener {
        private final TripRepository repo = new TripRepository();
        public void onMessage(Message m) {
            try {
                if (m instanceof TextMessage tm) repo.save(parseTrip(tm.getText()));
                else log.warn("Unsupported message: {}", m.getClass().getSimpleName());
            } catch (Exception e) { log.error("Trip processing error", e); }
        }
    }

    private class HotelListener implements MessageListener {
        private final HotelRepository repo = new HotelRepository();
        public void onMessage(Message m) {
            try {
                if (m instanceof TextMessage tm) repo.save(parseHotel(tm.getText()));
                else log.warn("Unsupported message: {}", m.getClass().getSimpleName());
            } catch (Exception e) { log.error("Hotel processing error", e); }
        }
    }
}