package adapters;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ActiveMqHotelEventStorageTest {

    @Test
    void createConnectionFactory_check() {
        ActiveMqHotelEventStorage storage = new ActiveMqHotelEventStorage();
        Object factory = storage.createConnectionFactory();

        assertNotNull(factory);
        assertEquals("org.apache.activemq.ActiveMQConnectionFactory",
                factory.getClass().getName());
    }

}