package service;

import config.DatabaseConfig;
import model.Hotel;
import model.Trip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.HotelRepository;
import repository.TripRepository;
import util.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;


public class EventStoreReader {
    private static final Logger logger = LoggerFactory.getLogger(EventStoreReader.class);

    public void processHistoricalEvents(String directoryPath) {
        Path path = Paths.get(directoryPath);

        if (!Files.exists(path)) {
            logger.warn("El directorio {} no existe", directoryPath);
            return;
        }

        try (Stream<Path> paths = Files.walk(path)) {
            paths.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".events"))
                    .forEach(this::processEventFile);
        } catch (IOException e) {
            logger.error("Error leyendo archivos históricos", e);
        }
    }

    public void processEventFile(Path filePath) {
        String sourceSystem = filePath.getParent().getFileName().toString();
        int lineCount = 0;
        int errorCount = 0;

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                try {
                    if ("blablacar".equalsIgnoreCase(sourceSystem)) {
                        Trip trip = JsonParser.parseTrip(line);
                        TripRepository repo = new TripRepository();
                        repo.save(trip);
                        DatabaseConfig.commit();
                    } else if ("xotelo".equalsIgnoreCase(sourceSystem)) {
                        Hotel hotel = JsonParser.parseHotel(line);
                        HotelRepository repo = new HotelRepository();
                        repo.save(hotel);
                        DatabaseConfig.commit();
                    }
                    lineCount++;
                } catch (Exception e) {
                    errorCount++;
                    DatabaseConfig.rollback();
                    logger.error("Error processing line {} in file {}", lineCount + 1, filePath, e);
                }
            }
            logger.info("Processed {} events from {}, {} errors", lineCount, filePath, errorCount);
        } catch (IOException e) {
            logger.error("Error reading file {}", filePath, e);
        }
    }
}
