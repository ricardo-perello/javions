package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AircraftDataTest {

    // we want this test to fail
    @Test
    void constructorWorkForCorrectValues(){
        AircraftRegistration registration = new AircraftRegistration("HB-JDC");
        AircraftTypeDesignator typeDesignator = new AircraftTypeDesignator("A20N");
        String model = "AIRBUS A-320neo";
        AircraftDescription description = new AircraftDescription("L2J");
        WakeTurbulenceCategory wakeTurbulenceCategory = WakeTurbulenceCategory.of("M");
        assertDoesNotThrow(() -> {
            new AircraftData(registration, typeDesignator, model, description, wakeTurbulenceCategory);
        });
    }

    @Test
    void constructorThrowsErrorIfRegistrationIsNull(){
        AircraftRegistration registration = null;
        AircraftTypeDesignator typeDesignator = new AircraftTypeDesignator("A20N");
        String model = "AIRBUS A-320neo";
        AircraftDescription description = new AircraftDescription("L2J");
        WakeTurbulenceCategory wakeTurbulenceCategory = WakeTurbulenceCategory.of("M");
        assertThrows(NullPointerException.class, () -> {
            new AircraftData(registration, typeDesignator, model, description, wakeTurbulenceCategory);
        });
    }

    @Test
    void constructorThrowsErrorIfTypeDesignatorIsNull(){
        AircraftRegistration registration = new AircraftRegistration("HB-JDC");
        AircraftTypeDesignator typeDesignator = null;;
        String model = "AIRBUS A-320neo";
        AircraftDescription description = new AircraftDescription("L2J");
        WakeTurbulenceCategory wakeTurbulenceCategory = WakeTurbulenceCategory.of("M");
        assertThrows(NullPointerException.class, () -> {
            new AircraftData(registration, typeDesignator, model, description, wakeTurbulenceCategory);
        });
    }

    @Test
    void constructorThrowsErrorIfModelIsNull(){
        AircraftRegistration registration = new AircraftRegistration("HB-JDC");
        AircraftTypeDesignator typeDesignator = new AircraftTypeDesignator("A20N");;
        String model = null;
        AircraftDescription description = new AircraftDescription("L2J");
        WakeTurbulenceCategory wakeTurbulenceCategory = WakeTurbulenceCategory.of("M");
        assertThrows(NullPointerException.class, () -> {
            new AircraftData(registration, typeDesignator, model, description, wakeTurbulenceCategory);
        });
    }

    @Test
    void constructorThrowsErrorIfDescriptionIsNull(){
        AircraftRegistration registration = new AircraftRegistration("HB-JDC");
        AircraftTypeDesignator typeDesignator = new AircraftTypeDesignator("A20N");;
        String model = "AIRBUS A-320neo";
        AircraftDescription description = null;
        WakeTurbulenceCategory wakeTurbulenceCategory = WakeTurbulenceCategory.of("M");
        assertThrows(NullPointerException.class, () -> {
            new AircraftData(registration, typeDesignator, model, description, wakeTurbulenceCategory);
        });
    }

    @Test
    void constructorThrowsErrorIfWakeTurbulenceCategoryIsNull(){
        AircraftRegistration registration = new AircraftRegistration("HB-JDC");
        AircraftTypeDesignator typeDesignator = new AircraftTypeDesignator("A20N");;
        String model = "AIRBUS A-320neo";
        AircraftDescription description = new AircraftDescription("L2J");
        WakeTurbulenceCategory wakeTurbulenceCategory = null;
        assertThrows(NullPointerException.class, () -> {
            new AircraftData(registration, typeDesignator, model, description, wakeTurbulenceCategory);
        });
    }

}
