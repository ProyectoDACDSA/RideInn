package adapters;

import application.BlablacarTripProvider;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

public class ControllerTest {

    @Test
    public void testStartAndStop() throws InterruptedException {
        BlablacarTripProvider mockProvider = mock(BlablacarTripProvider.class);
        Controller controller = new Controller(mockProvider);

        controller.start();
        Thread.sleep(100);
        controller.stop();

        verify(mockProvider, atLeastOnce()).fetchAndProcessAllTrips();
    }
}