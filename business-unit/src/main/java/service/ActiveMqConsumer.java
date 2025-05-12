package service;

import com.google.gson.*;
import model.Hotel;
import model.Trip;
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
    private static final Logger logger = LoggerFactory.getLogger(ActiveMqConsumer.class);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Connection connection;

    private static final String ACTIVEMQ_URL = "tcp://localhost:61616";
    private static final String BLABLACAR_TOPIC = "BlablacarTopic";
    private static final String XOTELO_TOPIC = "XoteloTopic";

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, context) ->
                    LocalDate.parse(json.getAsJsonPrimitive().getAsString()))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, context) ->
                    LocalDateTime.parse(json.getAsJsonPrimitive().getAsString()))
            .create();

    public void start() {
        executor.submit(() -> {
            try {
                initializeActiveMQConnection();
                logger.info("Consumidor de ActiveMQ iniciado correctamente");
            } catch (JMSException e) {
                logger.error("Error al iniciar el consumidor de ActiveMQ", e);
            }
        });
    }

    private void initializeActiveMQConnection() throws JMSException {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        connection = factory.createConnection();
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        setupTopicConsumer(session, BLABLACAR_TOPIC, new TripMessageListener(this));
        setupTopicConsumer(session, XOTELO_TOPIC, new HotelMessageListener(this));
    }

    private void setupTopicConsumer(Session session, String topicName, MessageListener listener) throws JMSException {
        Topic topic = session.createTopic(topicName);
        MessageConsumer consumer = session.createConsumer(topic);
        consumer.setMessageListener(listener);
        logger.info("Suscrito al topic: {}", topicName);
    }

    public void stop() {
        try {
            if (connection != null) {
                connection.close();
            }
            executor.shutdown();
            logger.info("Consumidor de ActiveMQ detenido correctamente");
        } catch (JMSException e) {
            logger.error("Error al detener el consumidor", e);
        }
    }

    private Trip parseTrip(String json) {
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

        return new Trip(
                jsonObject.get("origin").getAsString(),
                jsonObject.get("destination").getAsString(),
                (jsonObject.get("departureTime").getAsString().substring(11,19)),
                (jsonObject.get("departureTime").getAsString().substring(0,10)),
                jsonObject.get("price").getAsDouble(),
                jsonObject.get("available").getAsInt());
    }


    private Hotel parseHotel(String json) {
        if (json == null || json.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON string cannot be null or empty");
        }

        try {
            return gson.fromJson(json, Hotel.class);
        } catch (JsonSyntaxException | DateTimeParseException e) {
            throw new IllegalArgumentException("Formato JSON inválido para Hotel", e);
        }
    }

    private static class TripMessageListener implements MessageListener {
        private final TripRepository tripRepository = new TripRepository();
        private final ActiveMqConsumer parent;

        public TripMessageListener(ActiveMqConsumer parent) {
            this.parent = parent;
        }

        @Override
        public void onMessage(Message message) {
            try {
                if (message instanceof TextMessage textMessage) {
                    String json = textMessage.getText();
                    Trip trip = parent.parseTrip(json);
                    tripRepository.save(trip);
                    logger.info("Nuevo viaje procesado - Origen: {}, Destino: {}, Horario: {}", trip.getOrigin(), trip.getDestination(), trip.getDepartureTime());
                } else {
                    logger.warn("Tipo de mensaje no soportado: {}", message.getClass().getSimpleName());
                }
            } catch (Exception e) {
                logger.error("Error procesando mensaje de viaje", e);
            }
        }
    }

    private static class HotelMessageListener implements MessageListener {
        private final HotelRepository hotelRepository = new HotelRepository();
        private final ActiveMqConsumer parent;

        public HotelMessageListener(ActiveMqConsumer parent) {
            this.parent = parent;
        }

        @Override
        public void onMessage(Message message) {
            try {
                if (message instanceof TextMessage textMessage) {
                    String json = textMessage.getText();
                    Hotel hotel = parent.parseHotel(json);
                    hotelRepository.save(hotel);
                    logger.info("Nuevo hotel procesado - ID: {}, Ciudad: {}", hotel.getId(), hotel.getCity());
                } else {
                    logger.warn("Tipo de mensaje no soportado: {}", message.getClass().getSimpleName());
                }
            } catch (Exception e) {
                logger.error("Error procesando mensaje de hotel", e);
            }
        }
    }
}