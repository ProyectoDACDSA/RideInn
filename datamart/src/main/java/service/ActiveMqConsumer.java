package service;

import model.Hotel;
import model.Trip;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.HotelRepository;
import repository.TripRepository;
import util.JsonParser;

import javax.jms.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ActiveMqConsumer {
    private static final Logger logger = LoggerFactory.getLogger(ActiveMqConsumer.class);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Connection connection;

    public void start() {
        executor.submit(() -> {
            try {
                ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
                connection = factory.createConnection();
                connection.start();

                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                Topic blablacarTopic = session.createTopic("BlablacarTopic");
                MessageConsumer blablacarConsumer = session.createConsumer(blablacarTopic);
                blablacarConsumer.setMessageListener(new TripMessageListener());

                Topic xoteloTopic = session.createTopic("XoteloTopic");
                MessageConsumer xoteloConsumer = session.createConsumer(xoteloTopic);
                xoteloConsumer.setMessageListener(new HotelMessageListener());

                logger.info("Escuchando mensajes de ActiveMQ...");

            } catch (JMSException e) {
                logger.error("Error en el consumidor de ActiveMQ", e);
            }
        });
    }

    public void stop() {
        try {
            if (connection != null) {
                connection.close();
            }
            executor.shutdown();
        } catch (JMSException e) {
            logger.error("Error al detener el consumidor", e);
        }
    }

    private static class TripMessageListener implements MessageListener {
        private final TripRepository tripRepository = new TripRepository();

        @Override
        public void onMessage(Message message) {
            try {
                if (message instanceof TextMessage) {
                    String json = ((TextMessage) message).getText();
                    Trip trip = JsonParser.parseTrip(json);
                    tripRepository.save(trip);
                    logger.info("Nuevo viaje procesado: {}", trip);
                }
            } catch (Exception e) {
                logger.error("Error procesando mensaje de viaje", e);
            }
        }
    }

    private static class HotelMessageListener implements MessageListener {
        private final HotelRepository hotelRepository = new HotelRepository();

        @Override
        public void onMessage(Message message) {
            try {
                if (message instanceof TextMessage) {
                    String json = ((TextMessage) message).getText();
                    Hotel hotel = JsonParser.parseHotel(json);
                    hotelRepository.save(hotel);
                    logger.info("Nuevo hotel procesado: {}", hotel);
                }
            } catch (Exception e) {
                logger.error("Error procesando mensaje de hotel", e);
            }
        }
    }
}
