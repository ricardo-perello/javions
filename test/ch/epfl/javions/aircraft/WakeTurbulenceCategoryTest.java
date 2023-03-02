package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WakeTurbulenceCategoryTest {

    @Test
    void wakeTurbulenceCategoryWorksForLight(){
        String category = "L";
        assertEquals(WakeTurbulenceCategory.LIGHT, WakeTurbulenceCategory.of(category));
    }

    @Test
    void wakeTurbulenceCategoryWorksForMedium(){
        String category = "M";
        assertEquals(WakeTurbulenceCategory.MEDIUM, WakeTurbulenceCategory.of(category));
    }

    @Test
    void wakeTurbulenceCategoryWorksForHeavy(){
        String category = "H";
        assertEquals(WakeTurbulenceCategory.HEAVY, WakeTurbulenceCategory.of(category));
    }

    @Test
    void wakeTurbulenceCategoryWorksForUnknown(){
        String category = "K";
        assertEquals(WakeTurbulenceCategory.UNKNOWN, WakeTurbulenceCategory.of(category));
    }


}
