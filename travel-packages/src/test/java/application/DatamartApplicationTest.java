package application;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DatamartApplicationTest {
    @Test
    void testMainMethod() {
        assertDoesNotThrow(() -> DatamartApplication.main(new String[]{}));
    }
}