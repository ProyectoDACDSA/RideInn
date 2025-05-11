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
import java.sql.Time;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ActiveMqConsumer {
    private static final Logger logger = LoggerFactory.getLogger(ActiveMqConsumer.class);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Connection connection;

    // Configuración de ActiveMQ (podría moverse a properties)
    private static final String ACTIVEMQ_URL = "tcp://localhost:61616";
    private static final String BLABLACAR_TOPIC = "BlablacarTopic";
    private static final String XOTELO_TOPIC = "XoteloTopic";

    // Instancia de Gson con adaptadores para tipos especiales
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, context) ->
                    LocalDate.parse(json.getAsJsonPrimitive().getAsString()))
            .registerTypeAdapter(Time.class, (JsonDeserializer<Time>) (json, type, context) ->
                    Time.valueOf(json.getAsJsonPrimitive().getAsString()))
            .create();

    /**
     * Inicia el consumidor de ActiveMQ en un hilo separado
     */
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

        // Configuración de consumidores
        setupTopicConsumer(session, BLABLACAR_TOPIC, new TripMessageListener(this));
        setupTopicConsumer(session, XOTELO_TOPIC, new HotelMessageListener(this));
    }

    private void setupTopicConsumer(Session session, String topicName, MessageListener listener) throws JMSException {
        Topic topic = session.createTopic(topicName);
        MessageConsumer consumer = session.createConsumer(topic);
        consumer.setMessageListener(listener);
        logger.info("Suscrito al topic: {}", topicName);
    }

    /**
     * Detiene el consumidor y libera recursos
     */
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

    /**
     * Parsea un JSON a objeto Trip con validaciones
     */
    private Trip parseTrip(String json) {
        if (json == null || json.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON string cannot be null or empty");
        }

        try {
            Trip trip = gson.fromJson(json, Trip.class);

            // Validaciones de campos obligatorios
            validateTripFields(trip);
            return trip;
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Formato JSON inválido para Trip", e);
        }
    }

    private void validateTripFields(Trip trip) {
        if (trip.getDestination() == null || trip.getDestination().isEmpty()) {
            throw new IllegalArgumentException("Trip destination cannot be empty");
        }
        if (trip.getDepartureTime() == null) {
            throw new IllegalArgumentException("Departure time cannot be null");
        }
        if (trip.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
    }

    /**
     * Parsea un JSON a objeto Hotel con validaciones
     */
    private Hotel parseHotel(String json) {
        if (json == null || json.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON string cannot be null or empty");
        }

        try {
            Hotel hotel = gson.fromJson(json, Hotel.class);

            // Validaciones de campos obligatorios
            validateHotelFields(hotel);
            return hotel;
        } catch (JsonSyntaxException | DateTimeParseException e) {
            throw new IllegalArgumentException("Formato JSON inválido para Hotel", e);
        }
    }

    private void validateHotelFields(Hotel hotel) {
        if (hotel.getCity() == null || hotel.getCity().isEmpty()) {
            throw new IllegalArgumentException("Hotel city cannot be empty");
        }
        if (hotel.getStartDate() == null || hotel.getEndDate() == null) {
            throw new IllegalArgumentException("Hotel dates cannot be null");
        }
        if (hotel.getStartDate().isAfter(hotel.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
    }

    /**
     * MessageListener para mensajes de viajes (Blablacar)
     */
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
                    logger.info("Nuevo viaje procesado - ID: {}, Destino: {}", trip.getId(), trip.getDestination());
                } else {
                    logger.warn("Tipo de mensaje no soportado: {}", message.getClass().getSimpleName());
                }
            } catch (Exception e) {
                logger.error("Error procesando mensaje de viaje", e);
            }
        }
    }

    /**
     * MessageListener para mensajes de hoteles (Xotelo)
     */
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