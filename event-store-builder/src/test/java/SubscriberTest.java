import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class SubscriberTest {

    @Test
    public void testStartMethodRunsWithoutException() {
        Subscriber subscriber = new Subscriber();
        assertDoesNotThrow(subscriber::start);
    }
}