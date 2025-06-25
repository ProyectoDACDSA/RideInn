package application;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BestValueTripsTest {

    @Test
    void testGetValueRatingUsingReflection() throws Exception {
        BestValueTrips bvt = new BestValueTrips(new Scanner(System.in));

        Method method = BestValueTrips.class.getDeclaredMethod("getValueRating", double.class);
        method.setAccessible(true);

        String result1 = (String) method.invoke(bvt, 99.0);
        String result2 = (String) method.invoke(bvt, 120.0);
        String result3 = (String) method.invoke(bvt, 180.0);

        assertEquals("★ Excelente", result1);
        assertEquals("▲ Bueno", result2);
        assertEquals("▼ Regular", result3);
    }
}
