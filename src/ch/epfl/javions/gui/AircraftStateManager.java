package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import static ch.epfl.javions.Units.Time.MINUTE;
import static java.util.Objects.requireNonNull;

public final class AircraftStateManager {

    private final Map<IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> aircraftStateAccumulatorMap;
    private final ObservableSet<ObservableAircraftState> statePlaneSet;
    private final AircraftDatabase aircraftDatabase;
    private long lastTimeStampNs;
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
        IcaoAddress icaoAddress = message.icaoAddress();
        if ((icaoAddress == null) || (aircraftDatabase.get(icaoAddress) == null)) return; // TODO: 3/5/23 tota dice que le han dicho que esta condicion no va aqui, va despues en otra clase

        lastTimeStampNs = message.timeStampNs();
        if (!aircraftStateAccumulatorMap.containsKey(icaoAddress)) {
            aircraftStateAccumulatorMap.put(icaoAddress, new AircraftStateAccumulator<>(
                    new ObservableAircraftState(icaoAddress, aircraftDatabase.get(icaoAddress))));
        }
        aircraftStateAccumulatorMap.get(icaoAddress).update(message);

        if (aircraftStateAccumulatorMap.get(icaoAddress).stateSetter().getPosition() != null) {
            statePlaneSet.add(aircraftStateAccumulatorMap.get(icaoAddress).stateSetter());

        }
    }

    /**
     * public method that allows to eliminate a plane from our collections if the last message heard from it is over the
     * maximum we have determined (in this case a minute)
     */

    public void purge() {
        Iterator<AircraftStateAccumulator<ObservableAircraftState>> iterator = aircraftStateAccumulatorMap.values().iterator();
        while (iterator.hasNext()) {
            AircraftStateAccumulator<ObservableAircraftState> aircraftStateAircraftStateAccumulator = iterator.next();
            if (lastTimeStampNs - aircraftStateAircraftStateAccumulator.stateSetter().getLastMessageTimeStampNs()
                    >= MAX_DIFFERENCE_TIME){
                statePlaneSet.remove(aircraftStateAircraftStateAccumulator.stateSetter());
                iterator.remove();
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





