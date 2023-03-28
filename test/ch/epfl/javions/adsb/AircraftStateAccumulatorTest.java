package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;
import ch.epfl.javions.demodulation.AdsbDemodulator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
//TODO Position does not work
public class AircraftStateAccumulatorTest {
    public static void main(String[] args) throws IOException {
        String f = "resources/samples_20230304_1442.bin";
        IcaoAddress expectedAddress = new IcaoAddress("4D2228");
        try (InputStream s = new FileInputStream(f)) {
            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;
            AircraftStateAccumulator<AircraftState> a =
                    new AircraftStateAccumulator<>(new AircraftState());
            while ((m = d.nextMessage()) != null) {
                if (!m.icaoAddress().equals(expectedAddress)) continue;

                Message pm = MessageParser.parse(m);
                if (pm != null) a.update(pm);
            }
        }
    }
}
class AircraftState implements AircraftStateSetter {
    @Override
    public void setLastMessageTimeStampNs(long timeStampsNs) {

    }

    @Override
    public void setCategory(int category) {

    }

    @Override
    public void setCallSign(CallSign callSign) {
        System.out.println("indicatif : " + callSign);
    }


    @Override
    public void setAltitude(double altitude) {

    }

    @Override
    public void setVelocity(double velocity) {

    }

    @Override
    public void setTrackOrHeading(double trackOrHeading) {

    }

    @Override
    public void setPosition(GeoPos position) {
        System.out.println("position : " + position);
    }

}
