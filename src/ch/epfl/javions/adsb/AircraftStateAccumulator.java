package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

import static java.util.Objects.requireNonNull;

public class AircraftStateAccumulator<T extends AircraftStateSetter> {
    private T state;
    private  final double NORMALIZER = Math.pow(2,-17);
    private double xEven = 0.0;
    private double xOdd = 0.0;
    private double yEven = 0.0;
    private double yOdd= 0.0;
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
                        if(aim.timeStampNs() - lastMessageTimeStampNsOdd <= Math.pow(10,10)){
                            state.setPosition(CprDecoder.decodePosition(normalizeCoordinates(aim.x()), normalizeCoordinates(aim.y()),xOdd, yOdd, 0));
                            xEven = normalizeCoordinates(aim.x());
                            yEven = normalizeCoordinates(aim.y());
                            lastMessageTimeStampNsEven = aim.timeStampNs();
                        }
                    }
                    case 1 ->{
                        if(aim.timeStampNs() - lastMessageTimeStampNsEven <= Math.pow(10,10)){
                            state.setPosition(CprDecoder.decodePosition(xEven,yEven, normalizeCoordinates(aim.x()), normalizeCoordinates(aim.y()), 1));
                            xOdd = normalizeCoordinates(aim.x());
                            yOdd = normalizeCoordinates(aim.y());
                            lastMessageTimeStampNsOdd = aim.timeStampNs();
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

    private double normalizeCoordinates(double coordinate){
        return coordinate * NORMALIZER;
    }
}