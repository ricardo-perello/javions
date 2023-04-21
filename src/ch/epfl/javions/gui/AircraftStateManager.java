package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import ch.epfl.javions.adsb.AirbornePositionMessage;
import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.AircraftRegistration;
import ch.epfl.javions.aircraft.IcaoAddress;
import com.sun.javafx.collections.UnmodifiableListSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.io.IOException;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public final class AircraftStateManager {

    private final Map<IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> aircraftStateAccumulatorMap; //todo nombre por buscar
    private final ObservableSet<ObservableAircraftState> statePlaneSet;
    private final AircraftDatabase aircraftDatabase;
    private long lastTimeStampNs = 0;
    public final long MAX_DIFFERENCE_TIME = (long) (Units.Time.MINUTE * Math.pow(10, 9));


    public AircraftStateManager(AircraftDatabase aircraftDatabase){
        requireNonNull(aircraftDatabase);
        this.aircraftDatabase = aircraftDatabase;
        aircraftStateAccumulatorMap = FXCollections.observableHashMap();
        // FXCollections.observableArrayList()
        statePlaneSet = FXCollections.observableSet();
    }

    public void updateWithMessage(Message message) throws IOException {
        requireNonNull(message);
        IcaoAddress icaoAddress = message.icaoAddress();
        lastTimeStampNs = message.timeStampNs();
        if (!aircraftStateAccumulatorMap.containsKey(icaoAddress)){
            aircraftStateAccumulatorMap.put(icaoAddress, new AircraftStateAccumulator<>(
                            new ObservableAircraftState(icaoAddress,  aircraftDatabase.get(icaoAddress))));
        }
        aircraftStateAccumulatorMap.get(icaoAddress).update(message);
        AircraftStateAccumulator AAAAA = new AircraftStateAccumulator<>(new ObservableAircraftState(icaoAddress, aircraftDatabase.get(icaoAddress)));
        if(
        //usar database.Tsetter o algo asi para el add
        if(message instanceof AirbornePositionMessage){
            statePlaneSet.add();
        }
    }

    public void purge(){
        for (IcaoAddress icaoAddress : aircraftStateAccumulatorMap.keySet()) {
            ObservableAircraftState statePlane = aircraftStateAccumulatorMap.get(icaoAddress).stateSetter();
            if (lastTimeStampNs - statePlane.getLastMessageTimeStampNs()
                    >= MAX_DIFFERENCE_TIME ){
                aircraftStateAccumulatorMap.remove(icaoAddress);
                statePlaneSet.remove(statePlane);
            }
        }
    }

    public ObservableSet<ObservableAircraftState> states(){
        return statePlaneSet;
    }
}





