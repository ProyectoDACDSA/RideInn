import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class SuscriberTest {

    @Test
    void testSaveEventToFile(@TempDir Path tempDir) throws Exception {
        JsonObject testEvent = new JsonObject();
        testEvent.addProperty("id", "123");
        testEvent.addProperty("action", "test");

        System.setProperty("event.store.dir", tempDir.toString());

        Method method = Subscriber.class.getDeclaredMethod(
                "saveEventToFile", String.class, JsonObject.class);
        method.setAccessible(true);
        method.invoke(null, "TestTopic", testEvent);

        boolean fileCreated = Files.walk(tempDir)
                .anyMatch(p -> p.toString().endsWith(".events"));

        assertTrue(fileCreated, "El archivo de eventos no fue creado");

        String fileContent = Files.readString(
                Files.walk(tempDir)
                        .filter(p -> p.toString().endsWith(".events"))
                        .findFirst()
                        .orElseThrow());

        assertTrue(fileContent.contains("\"id\":\"123\""),
                "El contenido del evento no coincide");
    }
}