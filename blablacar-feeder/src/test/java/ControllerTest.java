import adapters.BlablacarTripProvider;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

public class ControllerTest {
    @Test
    public void testStart() {
        BlablacarTripProvider mockProvider = mock(BlablacarTripProvider.class);
        Controller controller = new Controller(mockProvider);

        controller.start();

        verify(mockProvider, timeout(1000)).fetchAndProcessAllTrips();
    }

    @Test
    public void testStop() {
        BlablacarTripProvider mockProvider = mock(BlablacarTripProvider.class);
        Controller controller = new Controller(mockProvider);

        controller.start();
        controller.stop();

        verifyNoMoreInteractions(mockProvider);
    }
}
