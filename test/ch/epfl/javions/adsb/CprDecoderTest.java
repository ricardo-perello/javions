package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CprDecoderTest {

    @Test
    void decodePositionWorkOnTrivialNumbers(){
        GeoPos expected = new GeoPos((int) Units.convert(0.020767, Units.Angle.TURN, Units.Angle.T32),
                (int) Units.convert(0.020764, Units.Angle.TURN, Units.Angle.T32));
        assertEquals(expected, CprDecoder.decodePosition(111600,94445,108865,77558,0));
    }
}
