package ch.epfl.javions.adsb;


import ch.epfl.javions.GeoPos;

import static java.util.Objects.requireNonNull;

public class AircraftStateAccumulator<T extends AircraftStateSetter> {
    private final T state;
    private final double MAXIMUM_DISTANCE_BETWEEN_MESSAGES = Math.pow(10, 10);
    private double xEven = Double.NaN;
    private double xOdd = Double.NaN;
    private double yEven = Double.NaN;
    private double yOdd = Double.NaN;
    private long lastMessageTimeStampNsEven = 0;
    private long lastMessageTimeStampNsOdd = 0;

    /**
     * constructor of the AircraftStateAccumulator that allows us to store to state of the parameter
     *
     * @param stateSetter aircraft we are interested to know the state
     */
    public AircraftStateAccumulator(T stateSetter) {
        requireNonNull(stateSetter);
        state = stateSetter;
    }

    /**
     * method that allows us to see what the state is
     *
     * @return state of played stored using the constructor
     */
    public T stateSetter() {
        return state;
    }

    /**
     * method that allows to update the mutable state based on the given message
     *
     * @param message Message, message emitted by plane
     */
    public void update(Message message) {
        // store the lastMessageTimeStampNs of the message
        state.setLastMessageTimeStampNs(message.timeStampNs());
        switch (message) {
            // case where the message is an AircraftIdentificationMessage, we store the category and callSign of the plane
            case AircraftIdentificationMessage aim -> {
                state.setCategory(aim.category());
                state.setCallSign(aim.callSign());
            }

            // case where the message is an AirbornePositionMessage, we store the altitude and if possible the position
            // we use the method setPosition to store the position
            case AirbornePositionMessage aim -> {
                state.setAltitude(aim.altitude());
                setPosition(aim);
            }

            //case where the message is an AirborneVelocityMessage, we store the velocity and trackOrHeading of the plan
            case AirborneVelocityMessage aim -> {
                state.setVelocity(aim.speed());
                state.setTrackOrHeading(aim.trackOrHeading());
            }

            default -> {
            }
        }
    }

    /**
     * private method that allows to determine if we can store the position and to store it
     * @param aim AirbornePositionMessage from where we are going to determine the coordinates
     */
    private void setPosition(AirbornePositionMessage aim) {
        int parity = aim.parity();
        switch (parity) {
            //case where the parity is 0
            case 0 -> {
                xEven = aim.x();
                yEven = aim.y();
                calculateGeopos(aim,parity,lastMessageTimeStampNsEven);
                //TODO ver que preferimos
                // verify that the coordinates are not NaN (aka we cannot calculate for the first position message)
                /*if (!Double.isNaN(xEven) && !Double.isNaN(yEven)) {
                    // verify that the difference since last opposite parity message is not over the maximum
                    if (aim.timeStampNs() - lastMessageTimeStampNsOdd <= MAXIMUM_DISTANCE_BETWEEN_MESSAGES) {
                        GeoPos geoPos = CprDecoder.decodePosition(xEven, yEven, xOdd, yOdd, parity);
                        //verify that the decodePosition is not null (aka that the plane is in a different latitude band)
                        if (geoPos != null) {
                            state.setPosition(geoPos);
                            // keep the timeStampNs, used next time we have an Odd message
                            lastMessageTimeStampNsEven = aim.timeStampNs();
                        }
                    }
                }*/
            }
            //case where the parity is 1
            case 1 -> {
                xOdd = aim.x();
                yOdd = aim.y();
                calculateGeopos(aim,parity,lastMessageTimeStampNsOdd);
                // verify that the coordinates are not NaN (aka we cannot calculate for the first position message)
                /*if (!Double.isNaN(xEven) && !Double.isNaN(yEven)) {
                    // verify that the difference since last oppposite parity message is not over the maximum
                    if (aim.timeStampNs() - lastMessageTimeStampNsEven <= MAXIMUM_DISTANCE_BETWEEN_MESSAGES) {
                        GeoPos geoPos = CprDecoder.decodePosition(xEven, yEven, xOdd, yOdd, parity);
                        //verify that the decodePosition is not null (aka that the plane is in a different latitude band)
                        if (geoPos != null) {
                            state.setPosition(geoPos);
                            // keep the timeStampNs, used next time we have an Even message
                            lastMessageTimeStampNsOdd = aim.timeStampNs();
                        }
                    }
                }*/
            }
        }
    }
    //TODO comentarios
    private void calculateGeopos(AirbornePositionMessage aim, int parity, long lastMessageTimeStampsNsParity){
        if (!Double.isNaN(xEven) && !Double.isNaN(yEven)) {
            // verify that the difference since last opposite parity message is not over the maximum
            if (aim.timeStampNs() - lastMessageTimeStampNsOdd <= MAXIMUM_DISTANCE_BETWEEN_MESSAGES) {
                GeoPos geoPos = CprDecoder.decodePosition(xEven, yEven, xOdd, yOdd, parity);                        //verify that the decodePosition is not null (aka that the plane is in a different latitude band)
                if (geoPos != null) {
                    state.setPosition(geoPos);
                    // keep the timeStampNs, used next time we have an Odd message
                    lastMessageTimeStampsNsParity = aim.timeStampNs();
                }
            }
        }
    }
}