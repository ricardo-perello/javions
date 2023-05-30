package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.io.IOException;
import java.util.Map;

import static ch.epfl.javions.Units.Time.MINUTE;
import static java.util.Objects.requireNonNull;

/**
 * AircraftStateManager is responsible for managing the state of aircraft based on the messages received from them.
 *
 * @author Ricardo Perello Mas (357241)
 * @author Alejandro Meredith Romero (360864)
 */

public final class AircraftStateManager {

    private final Map<IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> aircraftStateAccumulatorMap;
    private final ObservableSet<ObservableAircraftState> statePlaneSet;
    private final AircraftDatabase aircraftDatabase;
    private long lastTimeStampNs;
    private IcaoAddress lastIcaoAddress;
    private final long MAX_DIFFERENCE_TIME = (long) (MINUTE * Math.pow(10, 9));

    /**
     * constructor for AircraftStateManager
     *
     * @param aircraftDatabase AircraftDatabase, the database of a given plane
     */
    public AircraftStateManager(AircraftDatabase aircraftDatabase) {
        requireNonNull(aircraftDatabase);
        this.aircraftDatabase = aircraftDatabase;
        aircraftStateAccumulatorMap = FXCollections.observableHashMap();
        statePlaneSet = FXCollections.observableSet();
        lastTimeStampNs = 0;
    }

    /**
     * public method that allows to update the state of the plane that sent the message
     *
     * @param message Message, message sent by the given plane
     * @throws IOException if the Map aircraftStateAccumulatorMap does not have a value connected to the key icaoAddress
     */
    public void updateWithMessage(Message message) throws IOException {
        requireNonNull(message);

        lastIcaoAddress = message.icaoAddress();
        lastTimeStampNs = message.timeStampNs();
        if (!aircraftStateAccumulatorMap.containsKey(lastIcaoAddress)) {
            aircraftStateAccumulatorMap.put(lastIcaoAddress, new AircraftStateAccumulator<>(
                    new ObservableAircraftState(lastIcaoAddress, aircraftDatabase.get(lastIcaoAddress))));
        }
        aircraftStateAccumulatorMap.get(lastIcaoAddress).update(message);

        if (aircraftStateAccumulatorMap.get(lastIcaoAddress).stateSetter().getPosition() != null) {
            statePlaneSet.add(aircraftStateAccumulatorMap.get(lastIcaoAddress).stateSetter());

        }
    }

    /**
     * public method that allows to eliminate a plane from our collections if the last message heard from it is over the
     * maximum we have determined (in this case a minute)
     */

    public void purge() {
        statePlaneSet.removeIf(observableAircraftState ->
                lastTimeStampNs - observableAircraftState.getLastMessageTimeStampNs()
                        >= MAX_DIFFERENCE_TIME);
        IcaoAddress removeIcaoAddress = lastIcaoAddress;
        if (aircraftStateAccumulatorMap.get(removeIcaoAddress) != null) {
            if (lastTimeStampNs -
                    aircraftStateAccumulatorMap.get(removeIcaoAddress).stateSetter().getLastMessageTimeStampNs()
                    >= MAX_DIFFERENCE_TIME) {
                aircraftStateAccumulatorMap.remove(removeIcaoAddress);
            }
        }
    }

    /**
     * public method that allows us to see the set of states
     *
     * @return an ObservableSet of Observable Aircraft State using an unmodifiable view
     */
    public ObservableSet<ObservableAircraftState> states() {
        return FXCollections.unmodifiableObservableSet(statePlaneSet);
    }
}





