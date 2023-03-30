package ch.epfl.javions.adsb;


import static java.util.Objects.requireNonNull;

public class AircraftStateAccumulator<T extends AircraftStateSetter> {
    private final T state;
    private final double MAXIMUM_DISTANCE_BETWEEN_MESSAGES = Math.pow(10,10);
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
        state.setLastMessageTimeStampNs(message.timeStampNs());
        switch (message) {
            case AircraftIdentificationMessage aim ->{
                state.setCategory(aim.category());
                state.setCallSign(aim.callSign());
            }

            case AirbornePositionMessage aim -> {
                state.setAltitude(aim.altitude());
                setPosition(aim);
            }

            case AirborneVelocityMessage aim -> {
                state.setVelocity(aim.speed());
                state.setTrackOrHeading(aim.trackOrHeading());
            }

            default -> {}
        }
    }

    private void setPosition(AirbornePositionMessage aim){
        int parity = aim.parity();
        switch (parity){
            case 0 -> {
                //TODO check what happens when decodePosition return null
                xEven = aim.x();
                yEven = aim.y();
                lastMessageTimeStampNsEven = aim.timeStampNs();
            }
            case 1 ->{
                xOdd = aim.x();
                yOdd = aim.y();
                lastMessageTimeStampNsOdd = aim.timeStampNs();
            }
        }
        if(!Double.isNaN(xEven) && !Double.isNaN(yEven)) {
            if (aim.timeStampNs() - lastMessageTimeStampNsEven <= Math.pow(10, 10)) {
                state.setPosition(CprDecoder.decodePosition(xEven, yEven, xOdd, yOdd, parity));
            }
        }
    }

}