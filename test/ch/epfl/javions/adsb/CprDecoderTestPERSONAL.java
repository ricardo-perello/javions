package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CprDecoderTestPERSONAL {

    @Test
    public void test112() {
        GeoPos pos = CprDecoder.decodePosition(0.747222900390625, 0.7342300415039062, 0.6243515014648438, 0.4921417236328125, 0);
        System.out.println(pos);
    }

    @Test
    public void testEd() {
        GeoPos pos = CprDecoder.decodePosition(0.62, 0.42, 0.6200000000000000001, 0.4200000000000000001, 0);
        assertEquals(-2.3186440486460924, Units.convert((pos.longitudeT32()), Units.Angle.T32, Units.Angle.DEGREE),0.000001);
        assertEquals(2.5199999939650297, Units.convert((pos.latitudeT32()), Units.Angle.T32, Units.Angle.DEGREE),0.000001);
    }

    @Test
    public void testEdd() {
        GeoPos a = CprDecoder.decodePosition(0.3, 0.3, 0.3, 0.3, 0);
        GeoPos b = CprDecoder.decodePosition(0.3, 0.3, 0.3, 0.3, 1);
        GeoPos decodedPosition = CprDecoder.decodePosition(0.3, 0.3, 0.3, 0.3, 1);
        assertEquals(1.862068958580494, Units.convert((decodedPosition.longitudeT32()), Units.Angle.T32, Units.Angle.DEGREE),0.0000001);
        assertEquals(1.8305084947496653, Units.convert((decodedPosition.latitudeT32()), Units.Angle.T32, Units.Angle.DEGREE),0.0000001);
        GeoPos decodedPosition0 = CprDecoder.decodePosition(0.3, 0.3, 0.3, 0.3, 0);
        assertEquals(1.8305084947496653, Units.convert((decodedPosition0.longitudeT32()), Units.Angle.T32,
                Units.Angle.DEGREE),0.0000001);
        assertEquals(1.7999999597668648, Units.convert((decodedPosition0.latitudeT32()), Units.Angle.T32,
                Units.Angle.DEGREE),0.0000001);
        assertNull(CprDecoder.decodePosition(0, 0.3, 0, 0, 0));
    }

    @Test
    public void testChangeOfInvalidLatitude() {
        double x0 = Math.scalb(111600, -17);
        double x1 = Math.scalb(108865, -17);
        double y0 = 0.999999;
        double y1 = 0.439203;
        int mostRecent = 0;
        GeoPos decodedPosition = CprDecoder.decodePosition(x0, y0, x1, y1, mostRecent);
        assertEquals(null, decodedPosition);
    }

    @Test
    public void testDecodePosition() {
        double x0 = Math.scalb(111600, -17);
        double x1 = Math.scalb(108865, -17);
        double y0 = Math.scalb(94445, -17);
        double y1 = Math.scalb(77558, -17);
        int mostRecent = 0;
        GeoPos decodedPosition = CprDecoder.decodePosition(x0, y0, x1, y1, mostRecent);
        assertEquals(7.476062346249819, Units.convert((decodedPosition.longitudeT32()), Units.Angle.T32, Units.Angle.DEGREE),0.0000001);
        assertEquals(46.323349038138986, Units.convert((decodedPosition.latitudeT32()), Units.Angle.T32, Units.Angle.DEGREE),0.0000001);
    }

    private final static double DIVIDER = Math.scalb(1.0, 17);

    @Test
    void testDecodePositionp() {
        double x0 = 111600 / DIVIDER;
        double y0 = 94445 / DIVIDER;
        double x1 = 108865 / DIVIDER;
        double y1 = 77558 / DIVIDER;
        double expectedLat0 = Units.convertFrom((1.0 / 60) * (7 + y0), Units.Angle.TURN);
        double expectedLat1 = Units.convertFrom((1.0 / 59) * (7 + y1), Units.Angle.TURN);
        double expectedLon0 = Units.convertFrom((1.0 / 41) * x0, Units.Angle.TURN);
        double expectedLon1 = Units.convertFrom((1.0 / 40) * x1, Units.Angle.TURN);
        int mostRecent = 0;
        GeoPos pos = CprDecoder.decodePosition(x0, y0, x1, y1, mostRecent);
        assertNotNull(pos);
        assertEquals(expectedLat0, pos.latitude(), 1e-9);
        assertEquals(expectedLon0, pos.longitude(), 1e-9);
        mostRecent = 1;
        pos = CprDecoder.decodePosition(x0, y0, x1, y1, mostRecent);
        assertNotNull(pos);
        assertEquals(expectedLat1, pos.latitude(), 1e-9);
        assertEquals(expectedLon1, pos.longitude(), 1e-9);
        mostRecent = 2;
        int finalMostRecent = mostRecent;

        assertThrows(IllegalArgumentException.class, () ->

        {
            CprDecoder.decodePosition(x0, y0, x1, y1, finalMostRecent);
        });
    }

    @Test
    public void testImpairLongitudeZones() {
        double lat = 37.0;
        double lon = -120.0;
        GeoPos cprResult = CprDecoder.decodePosition(lat, lon, lat, lat, 1);
        assertNull(cprResult);
    }

    @Test
    public void testDecodePositions() {
        double x0 = Math.scalb(111600, -17);
        ;
        double x1 = Math.scalb(108865, -17);
        ;
        double y0 = Math.scalb(94445, -17);
        double y1 = Math.scalb(77558, -17);
        ;
        int mostRecent = 0;
        GeoPos decodedPosition = CprDecoder.decodePosition(x0, y0, x1, y1, mostRecent);
        assertEquals(7.476062, Units.convert((decodedPosition.longitudeT32()), Units.Angle.T32,
                Units.Angle.DEGREE), 1e-6);
        assertEquals(46.323349, Units.convert((decodedPosition.latitudeT32()), Units.Angle.T32,
                Units.Angle.DEGREE), 1e-6);
    }

    @Test
    void testDecodePosition2() {
        double x0 = 0.851440;
        double x1 = 0.830574;
        double y0 = 0.720558;
        double y1 = 0.591721;
        int mostRecent = 0;
        GeoPos pos = CprDecoder.decodePosition(x0, y0, x1, y1, mostRecent);
        assertNotNull(pos);
        assertEquals(Units.convertFrom(46.323349, Units.Angle.DEGREE), pos.latitude(), 1e-7);
        assertEquals(Units.convertFrom(7.476062, Units.Angle.DEGREE), pos.longitude(), 1e-7);
        mostRecent = 1;
        pos = CprDecoder.decodePosition(x0, y0, x1, y1, mostRecent);
        assertNotNull(pos);
        assertEquals(Units.convertFrom(46.322363, Units.Angle.DEGREE), pos.latitude(), 1e-7);
        assertEquals(Units.convertFrom(7.475166, Units.Angle.DEGREE), pos.longitude(), 1e-7);
        mostRecent = 2;
        int finalMostRecent = mostRecent;
        assertThrows(IllegalArgumentException.class, () -> {
            CprDecoder.decodePosition(x0, y0, x1, y1, finalMostRecent);
        });
    }

    @Test
    public void ArgumentTest() {
        assertThrows(IllegalArgumentException.class, () -> CprDecoder.decodePosition(0, 0, 0, 0, 5));
    }

    @Test
    public void ArgumentTest2() {
        assertEquals(null, CprDecoder.decodePosition(-5, -5, 0, 0, 0));
        assertEquals(null, CprDecoder.decodePosition(0, 0, -5, -5, 0));
        assertEquals(null, CprDecoder.decodePosition(0, 0, 0, -5, 0));
    }

    @Test
    public void ArgumentTest4() {
        GeoPos pos = CprDecoder.decodePosition(-5, 1, 0, 0, 0);
        assertEquals(-180, Units.convert((pos.longitudeT32()), Units.Angle.T32,
                Units.Angle.DEGREE));
        assertEquals(0, Units.convert((pos.latitudeT32()), Units.Angle.T32,
                Units.Angle.DEGREE));
        GeoPos pos1 = CprDecoder.decodePosition(0, 0, 0, 0, 0);
        assertEquals(0, Units.convert((pos1.longitudeT32()), Units.Angle.T32, Units.Angle.DEGREE),0.00001);
        assertEquals(0, Units.convert((pos1.latitudeT32()), Units.Angle.T32, Units.Angle.DEGREE),0.00001);
    }

    @Test
    public void ArgumentTest3() {
        System.out.println(CprDecoder.decodePosition(0.26, 1.51, 1.25, 2.36, 0));
    }

    @Test
    public void testWithQEqualToZero() {
        ByteString bs = ByteString.ofHexadecimalString("8D39203559B225F07550ADBE328F");
        ByteString bs2 = ByteString.ofHexadecimalString("8DAE02C85864A5F5DD4975A1A3F5");
        RawMessage mess1 = new RawMessage(0, bs);
        RawMessage mess2 = new RawMessage(0, bs2);

        AirbornePositionMessage m1 = AirbornePositionMessage.of(mess1);
        AirbornePositionMessage m2 = AirbornePositionMessage.of(mess2);

        assertEquals(3474.72, m1.altitude(), 0.00001);
        assertEquals(7315.20, m2.altitude(), 0.00001);
    }

    @Test
    void airbornePositionMessageReturnsNull() {
        ByteString m0 = ByteString.ofHexadecimalString("8D3920351F54245F0E550ADBE28F");
        ByteString m5 = ByteString.ofHexadecimalString("8D3920351FBA245F0E550ADBE28F");
        ByteString m6 = ByteString.ofHexadecimalString("8D3920351F9A245F0E550ADBE28F");

        RawMessage mess0 = new RawMessage(0, m0);
        RawMessage mess5 = new RawMessage(0, m5);
        RawMessage mess6 = new RawMessage(0, m6);

        assertNull(AirbornePositionMessage.of(mess0));
        assertNull(AirbornePositionMessage.of(mess5));
        assertNull(AirbornePositionMessage.of(mess6));
    }
}
