package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AircraftDatabaseTest {
    @Test
    void findIcaoAddressInZipFileTest(){
        AircraftData a=null;
        AircraftDatabase d = new AircraftDatabase("resources/aircraft.zip");
        try {
             a = d.get(new IcaoAddress("0AB014"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert a != null;
        assertEquals("CESSNA 208 Caravan",a.model());
    }



}

