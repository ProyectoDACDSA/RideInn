package service;

import com.google.gson.*;
import model.Hotel;
import model.Trip;
import repository.HotelRepository;
import repository.TripRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.stream.Stream;

public class EventStoreReader {
    private static final Logger logger = LoggerFactory.getLogger(EventStoreReader.class);

    private final String blablacarPath;
    private final String xoteloPath;
    private final TripRepository tripRepository;
    private final HotelRepository hotelRepository;
    private final Gson gson;

    public EventStoreReader(String blablacarPath, String xoteloPath) {
        this.blablacarPath = blablacarPath;
        this.xoteloPath = xoteloPath;
        this.tripRepository = new TripRepository();
        this.hotelRepository = new HotelRepository();
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, context) -> {
                    try {
                        return LocalDate.parse(json.getAsString());
                    } catch (DateTimeParseException e) {
                        String datePart = json.getAsString().split("T")[0];
                        return LocalDate.parse(datePart);
                    }
                })
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, context) -> {
                    try {
                        return LocalDateTime.parse(json.getAsString());
                    } catch (DateTimeParseException e) {
                        if (!json.getAsString().contains("T")) {
                            return LocalDate.parse(json.getAsString()).atStartOfDay();
                        }
                        throw e;
                    }
                })
                .registerTypeAdapter(LocalTime.class, (JsonDeserializer<LocalTime>) (json, type, context) -> {
                    try {
                        String timeStr = json.getAsString();
                        // Handle full datetime strings by extracting time part
                        if (timeStr.contains("T")) {
                            timeStr = timeStr.split("T")[1];
                            // Remove timezone if present
                            if (timeStr.contains("+")) {
                                timeStr = timeStr.split("\\+")[0];
                            }
                        }
                        return LocalTime.parse(timeStr);
                    } catch (DateTimeParseException e) {
                        throw new JsonParseException("Failed to parse LocalTime", e);
                    }
                })
                .create();
    }

    public void processAllHistoricalEvents() {
        logger.info("Processing all historical events...");
        processBlablacarEvents();
        processXoteloEvents();
        logger.info("Finished processing historical events");
    }

    private void processBlablacarEvents() {
        try {
            logger.info("Processing Blablacar events from: {}", blablacarPath);
            Path path = Paths.get(blablacarPath);

            if (!Files.exists(path)) {
                logger.warn("Blablacar directory does not exist: {}", blablacarPath);
                return;
            }

            try (Stream<Path> paths = Files.walk(path)) {
                paths.filter(Files::isRegularFile)
                        .filter(p -> p.toString().endsWith(".events"))
                        .forEach(this::processBlablacarFile);
            }
        } catch (IOException e) {
            logger.error("Error processing Blablacar events", e);
        }
    }

    private void processBlablacarFile(Path file) {
        try {
            logger.debug("Processing file: {}", file);
            long lineCount = Files.lines(file)
                    .peek(this::processTripLine)
                    .count();

            logger.info("File {} processed: {} lines", file, lineCount);
        } catch (IOException e) {
            logger.error("Error processing file: {}", file, e);
        }
    }

    private void processTripLine(String line) {
        try {
            Trip trip = gson.fromJson(line, Trip.class);
            System.out.println("Se identifica");
            System.out.println(trip.toString());
            if (trip != null) {
                tripRepository.save(trip);
                logger.debug("Saved trip: {}", trip);
            }
        } catch (Exception e) {
            logger.error("Error processing trip line: {}", line, e);
        }
    }

    private void processXoteloEvents() {
        try {
            logger.info("Processing Xotelo events from: {}", xoteloPath);
            Path path = Paths.get(xoteloPath);

            if (!Files.exists(path)) {
                logger.warn("Xotelo directory does not exist: {}", xoteloPath);
                return;
            }

            try (Stream<Path> paths = Files.walk(path)) {
                paths.filter(Files::isRegularFile)
                        .filter(p -> p.toString().endsWith(".events"))
                        .forEach(this::processXoteloFile);
            }
        } catch (IOException e) {
            logger.error("Error processing Xotelo events", e);
        }
    }

    private void processXoteloFile(Path file) {
        try {
            logger.debug("Processing file: {}", file);
            long lineCount = Files.lines(file)
                    .peek(this::processHotelLine)
                    .count();

            logger.info("File {} processed: {} lines", file, lineCount);
        } catch (IOException e) {
            logger.error("Error processing file: {}", file, e);
        }
    }

    private void processHotelLine(String line) {
        try {
            Hotel hotel = gson.fromJson(line, Hotel.class);
            if (hotel != null) {
                hotelRepository.save(hotel);
                logger.debug("Saved hotel: {}", hotel);
            }
        } catch (Exception e) {
            logger.error("Error processing hotel line: {}", line, e);
        }
    }
}