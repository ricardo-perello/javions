package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

import static java.util.Objects.requireNonNull;

public class AircraftStateAccumulator<T extends AircraftStateSetter> {
    private T state;
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
                            state.setPosition(CprDecoder.decodePosition(aim.x(),
                                    aim.y(),xOdd, yOdd, 0));
                            xEven = aim.x();
                            yEven = aim.y();
                            lastMessageTimeStampNsEven = aim.timeStampNs();
                        }
                    }
                    case 1 ->{
                        if(aim.timeStampNs() - lastMessageTimeStampNsEven <= Math.pow(10,10)){
                            state.setPosition(CprDecoder.decodePosition(xEven,yEven,aim.x(),
                                    aim.y(), 1));
                            xOdd = aim.x();
                            yOdd = aim.y();
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

}