package application;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

class CurrentRecommendationsTest {

    @Test
    void testExecuteWithSampleInput() throws Exception {
        String simulatedUserInput = String.join("\n",
                "Paris",
                "no",
                "no",
                "no",
                "no"
        );

        InputStream inputStream = new ByteArrayInputStream(simulatedUserInput.getBytes(StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(inputStream);

        CurrentRecommendations currentRecommendations = new CurrentRecommendations(scanner);

        currentRecommendations.execute();
    }
}