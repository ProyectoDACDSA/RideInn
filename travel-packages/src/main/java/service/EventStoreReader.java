package service;

import com.google.gson.*;
import model.Hotel;
import model.Trip;
import repository.HotelRepository;
import repository.TripRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.stream.Stream;

public class EventStoreReader {
    private static final Logger logger = LoggerFactory.getLogger(EventStoreReader.class);
    private final String blablacarPath, xoteloPath;
    private final TripRepository tripRepository = new TripRepository();
    private final HotelRepository hotelRepository = new HotelRepository();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, ctx) -> {
                try {
                    return LocalDate.parse(json.getAsString());
                } catch (DateTimeParseException e) {
                    return LocalDate.parse(json.getAsString().split("T")[0]);
                }
            })
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, ctx) -> {
                try {
                    return LocalDateTime.parse(json.getAsString());
                } catch (DateTimeParseException e) {
                    if (!json.getAsString().contains("T"))
                        return LocalDate.parse(json.getAsString()).atStartOfDay();
                    throw e;
                }
            })
            .create();

    public EventStoreReader(String blablacarPath, String xoteloPath) {
        this.blablacarPath = blablacarPath;
        this.xoteloPath = xoteloPath;
    }

    public void processAllHistoricalEvents() {
        logger.info("Processing all historical events...");
        processEvents(blablacarPath, this::processTripLine, "Blablacar");
        processEvents(xoteloPath, this::processHotelLine, "Xotelo");
        logger.info("Finished processing historical events");
    }

    private void processEvents(String dir, java.util.function.Consumer<String> lineProcessor, String source) {
        Path path = Paths.get(dir);
        if (!Files.exists(path)) {
            logger.warn("{} directory does not exist: {}", source, dir);
            return;
        }
        try (Stream<Path> files = Files.walk(path)) {
            files.filter(Files::isRegularFile)
                    .filter(f -> f.toString().endsWith(".events"))
                    .forEach(f -> {
                        logger.debug("Processing {} file: {}", source, f);
                        try {
                            long count = Files.lines(f).peek(lineProcessor).count();
                            logger.info("File for {} {} processed: {} lines", source.toLowerCase(), f, count);
                        } catch (IOException e) {
                            logger.error("Error processing file: {}", f, e);
                        }
                    });
        } catch (IOException e) {
            logger.error("Error processing {} events", source, e);
        }
    }

    private void processTripLine(String line) {
        try {
            JsonObject obj = JsonParser.parseString(line).getAsJsonObject();
            String dep = obj.get("departureTime").getAsString();
            Trip trip = new Trip(
                    obj.get("origin").getAsString(),
                    obj.get("destination").getAsString(),
                    dep.substring(11, 19),
                    dep.substring(0, 10),
                    obj.get("price").getAsDouble(),
                    obj.get("available").getAsInt()
            );
            tripRepository.save(trip);
        } catch (Exception e) {
            logger.error("Error processing trip line: {}", line, e);
        }
    }

    private void processHotelLine(String line) {
        try {
            Hotel hotel = gson.fromJson(line, Hotel.class);
            if (hotel != null) hotelRepository.save(hotel);
        } catch (Exception e) {
            logger.error("Error processing hotel line: {}", line, e);
        }
    }
}
