package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

import static java.util.Objects.requireNonNull;

public class AircraftStateAccumulator<T extends AircraftStateSetter> {
    private T state;
    private double xEven = Double.NaN;
    private double xOdd = Double.NaN;
    private double yEven = Double.NaN;
    private double yOdd= Double.NaN;
    private long lastMessageTimeStampNsEven = 0;
    private long lastMessageTimeStampNsOdd = 0;


    public AircraftStateAccumulator(T stateSetter) {
        requireNonNull(stateSetter);
        state = stateSetter;
    }

    public T stateSetter() {
        return state;
    }

    public void update(Message message) {
        switch (message) {
            case AircraftIdentificationMessage aim ->{
                state.setLastMessageTimeStampNs(aim.timeStampNs());
                state.setCategory(aim.category());
                state.setCallSign(aim.callSign());
            }

            case AirbornePositionMessage aim -> {
                state.setLastMessageTimeStampNs(aim.timeStampNs());
                state.setAltitude(aim.altitude());
                int parity = aim.parity();
                switch (parity){
                    case 0 -> {
                        xEven = aim.x();
                        yEven = aim.y();
                        if(!Double.isNaN(xOdd) && !Double.isNaN(yOdd)){
                            if(aim.timeStampNs() - lastMessageTimeStampNsOdd <= Math.pow(10,10)){
                                state.setPosition(CprDecoder.decodePosition(xEven, yEven, xOdd, yOdd, parity));
                                lastMessageTimeStampNsEven = aim.timeStampNs();
                            }
                        }
                    }
                    case 1 ->{
                        xOdd = aim.x();
                        yOdd = aim.y();
                        if(!Double.isNaN(xEven) && !Double.isNaN(yEven)) {
                            if (aim.timeStampNs() - lastMessageTimeStampNsEven <= Math.pow(10, 10)) {
                                state.setPosition(CprDecoder.decodePosition(xEven, yEven, xOdd, yOdd, parity));
                                lastMessageTimeStampNsOdd = aim.timeStampNs();
                            }
                        }
                    }
                }
            }

            case AirborneVelocityMessage aim -> {
                state.setLastMessageTimeStampNs(aim.timeStampNs());
                state.setVelocity(aim.speed());
                state.setTrackOrHeading(aim.trackOrHeading());
            }

            default -> {}
        }
    }

}