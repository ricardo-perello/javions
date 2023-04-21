package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import ch.epfl.javions.adsb.AirbornePositionMessage;
import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public final class AircraftStateManager {

    private Map<IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> nombrePorBuscar; //todo nombre por buscar
    private ObservableSet<ObservableAircraftState> setStatePlane;
    private final AircraftData aircraftData;
    private long lastTimeStamNs = 0;
    public final long MAX_DIFFERENCE_TIME = (long) Math.pow(10, 9);


    public AircraftStateManager(AircraftData aircraftData){
        requireNonNull(aircraftData);
        this.aircraftData = aircraftData;
    }

    public void updateWithMessage(Message message){
        requireNonNull(message);
        IcaoAddress icaoAddress = message.icaoAddress();
        lastTimeStamNs = message.timeStampNs();
        if (!nombrePorBuscar.containsKey(icaoAddress)){
            nombrePorBuscar.put(icaoAddress, new AircraftStateAccumulator<>(
                            new ObservableAircraftState(icaoAddress, aircraftData)));
        }

        if(message instanceof AirbornePositionMessage){
            setStatePlane.add(new ObservableAircraftState(icaoAddress, aircraftData));
        }
        nombrePorBuscar.get(icaoAddress).update(message);
    }

    public void purge(){
        for (IcaoAddress icaoAddress : nombrePorBuscar.keySet()) {
            ObservableAircraftState statePlane = nombrePorBuscar.get(icaoAddress).stateSetter();
            if (lastTimeStamNs - statePlane.getLastMessageTimeStampNs()
                    >= MAX_DIFFERENCE_TIME ){
                nombrePorBuscar.remove(icaoAddress);
                setStatePlane.remove(statePlane);
            }
        }
    }
}





