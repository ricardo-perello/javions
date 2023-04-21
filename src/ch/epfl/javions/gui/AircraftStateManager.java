package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.AirbornePositionMessage;
import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.collections.ObservableSet;

import java.util.Map;

import static java.util.Objects.requireNonNull;

public final class AircraftStateManager {

    private Map<IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> aircraftStateAccumulatorMap; //todo nombre por buscar
    private ObservableSet<ObservableAircraftState> statePlaneSet;
    private final AircraftData aircraftData;
    private long lastTimeStampNs = 0;
    public final long MAX_DIFFERENCE_TIME = (long) Math.pow(10, 9);


    public AircraftStateManager(AircraftData aircraftData){
        requireNonNull(aircraftData);
        this.aircraftData = aircraftData;

    }

    public void updateWithMessage(Message message){
        requireNonNull(message);
        IcaoAddress icaoAddress = message.icaoAddress();
        lastTimeStampNs = message.timeStampNs();
        if (!aircraftStateAccumulatorMap.containsKey(icaoAddress)){
            aircraftStateAccumulatorMap.put(icaoAddress, new AircraftStateAccumulator<>(
                            new ObservableAircraftState(icaoAddress, aircraftData)));
        }

        if(message instanceof AirbornePositionMessage){
            statePlaneSet.add(new ObservableAircraftState(icaoAddress, aircraftData));
        }
        aircraftStateAccumulatorMap.get(icaoAddress).update(message);
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
}





